/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.graph;

import java.util.Optional;

import org.graphstream.graph.Edge;
import org.graphstream.ui.spriteManager.Sprite;

import edu.kit.informatik.tolowiz.model.visualization.Color;
import edu.kit.informatik.tolowiz.model.visualization.RelationConfiguration;
import edu.kit.informatik.tolowiz.model.visualization.RelationListenerInterface;
import edu.kit.informatik.tolowiz.model.visualization.RelationStyle.ArrowShape;
import edu.kit.informatik.tolowiz.model.visualization.RelationStyle.RelationStroke;

/**
 * Models an edge in the {@link Graph} that visualizes the ontology. <br>
 * An edge visualizes a relation, a node visualizes an instance. <br>
 * Refreshes when changes in the visualization of that relation object are made.
 * Has an GraphStream Edge attribute.
 *
 * @author Anja
 * @version 1.0
 */
class ToloEdge implements RelationListenerInterface {

    /**
     * The configuration the edge representing the relation should be visualized
     * with.
     *
     * @see RelationConfiguration
     */
    private RelationConfiguration relation;

    /**
     * The edge object of GraphStream.
     */
    private Edge gsEdge;

    /**
     * The graph this edge belongs to.
     */
    private Graph graph;

    /**
     * The label of an edge, showing its name.
     */
    private Sprite label;

    /**
     * Constructor. Registers itself as listener for the changes in the
     * visualization of the edge it represents.
     *
     * @param graph    The graph the edge belongs to.
     * @param relation How the edge should be visualized.
     */
    ToloEdge(Graph graph, RelationConfiguration relation) {
        this.graph = graph;
        this.relation = relation;
        relation.addListener(this);
    }

    /**
     * The graph constructor calls the paint() method which sets the GraphStream
     * edge here.
     *
     * @param id The String id GraphStream identifies the edge with.
     */
    void setGsEdge(String id) {
        this.gsEdge = this.graph.getVisualizedGraph().getEdge(id);
        assert (this.gsEdge != null);

        /*
         * workaround about labeling edges correctly: GraphStream labels interfere with
         * each other if there a mutliple edges between the same two nodes. The
         * "text-alignment: along;" command for the css style sheet does not work
         * correctly. Therefore, ToloWiz creates an invisible sprite attached to the
         * edge, and the sprite has a label.
         */

        // sprite ids cannot contain dots:
        this.label = this.graph.getSpriteManager().addSprite(this.getRelation().getURI().replace(".", "_"));

        this.label.attachToEdge(this.gsEdge.getId());
        this.label.setPosition(0.5);
        this.label.setAttribute("ui.style",
                "size: 0px ; text-background-mode: rounded-box; text-visibility-mode: normal;");
        this.label.setAttribute("ui.label", this.relation.getRelationType().getName());

        // GraphStreams provided labels:
        // this.gsEdge.setAttribute("ui.label",
        // this.relation.getRelationType().getName());
    }

    /**
     * Gets new information about the visualization of a relation out of the model
     * and refreshes it.
     *
     * Use this method for nothing else but as a listener, especially not for
     * initially building the object, as the view will also be refreshed and
     * therefore an infinite loop may occur.
     *
     * @see RelationListenerInterface
     */
    @Override
    public void onChange() {
        this.graph.runLater(() -> {
            this.build();
            this.graph.viewRefresh();
        });
    }

    /**
     * Refreshes the visual representation according to the current status in the
     * model. Always acts as if everything possible had changed: all methods get
     * called successively, as the notification only informed that something has
     * changed, without any information about what exactly.
     */
    void build() {
        this.showHide();
        this.gsEdge.setAttribute("ui.style", this.changeRelationTypeSymbol() + "visibility-mode: normal;");
    }

    /**
     * Shows or hides the edge in the visualization.
     */
    private void showHide() {
        assert (this.gsEdge != null);
        if (!this.relation.isVisible()) {
            this.gsEdge.setAttribute("ui.hide");
            this.label.setAttribute("ui.hide");
        } else {
            this.gsEdge.removeAttribute("ui.hide");
            this.label.removeAttribute("ui.hide");
        }
    }

    /**
     * Changes the style of the edge representing the relation in the graph.
     *
     * @return The current style sheet for that particular edge.
     */
    private String changeRelationTypeSymbol() {
        String stroke;
        String shape;
        String color;

        assert (this.relation != null);
        assert (this.relation.getCurrentStyle() != null);

        Optional<RelationStroke> strokeOpt = this.relation.getCurrentStyle().getStroke();
        Optional<ArrowShape> shapeOpt = this.relation.getCurrentStyle().getShape();
        Optional<Color> colorOpt = this.relation.getCurrentStyle().getColor();

        if (strokeOpt.isEmpty()) {
            stroke = "none";

        } else {
            stroke = strokeOpt.get().toString().toLowerCase();
        }

        if (shapeOpt.isEmpty()) {
            shape = "none";
        } else {
            shape = shapeOpt.get().toString().toLowerCase();
        }

        if (colorOpt.isEmpty()) {
            color = "rgb(0,0,0);"; // default black
        } else {
            color = "rgb(" + colorOpt.get().getRed() + "," + colorOpt.get().getGreen() + "," + colorOpt.get().getBlue()
                    + ");";
        }

        return "arrow-shape: " + shape + "; stroke-mode: " + stroke + "; fill-color: " + color;

    }

    /**
     * @return the configuration the relation is represented with.
     */
    public RelationConfiguration getRelation() {
        return this.relation;
    }

}
