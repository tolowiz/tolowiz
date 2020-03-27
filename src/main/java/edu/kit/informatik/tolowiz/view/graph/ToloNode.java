/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.graph;

import java.util.Optional;

import org.graphstream.graph.Node;

import edu.kit.informatik.tolowiz.model.data.icons.IconDatabaseException;
import edu.kit.informatik.tolowiz.model.data.icons.IconInterface;
import edu.kit.informatik.tolowiz.model.visualization.Color;
import edu.kit.informatik.tolowiz.model.visualization.InstanceConfiguration;
import edu.kit.informatik.tolowiz.model.visualization.InstanceListenerInterface;
import edu.kit.informatik.tolowiz.model.visualization.InstanceMark.InstanceShape;
import edu.kit.informatik.tolowiz.model.visualization.InstanceMark.InstanceStroke;
import edu.kit.informatik.tolowiz.model.visualization.InstanceTypeConfiguration;
import edu.kit.informatik.tolowiz.model.visualization.Point;

/**
 * Models a node in the {@link Graph} that visualizes the ontology. <br>
 * A node visualizes an instance, an edge visualizes a relation. <br>
 * Refreshes when changes in the visualization of that instance object are made.
 *
 * @author Anja
 * @version 1.0
 */

class ToloNode implements InstanceListenerInterface {

    /**
     * The configuration the node representing the instance should be visualized
     * with.
     *
     * @see InstanceConfiguration
     */
    private InstanceConfiguration instance;

    /**
     * The node object of GraphStream.
     */
    private Node gsNode;

    /**
     * The graph this node belongs to.
     */
    private Graph graph;

    /**
     * Constructor. Registers itself as listener for the changes in the
     * visualization of the instance it represents.
     *
     * @param graph    The graph the node belongs to.
     * @param instance How the node should be visualized.
     */
    ToloNode(Graph graph, InstanceConfiguration instance) {
        this.instance = instance;
        instance.addListener(this);
        this.graph = graph;
    }

    /**
     * The graph constructor calls the paint() method which sets the GraphStream
     * node here.
     *
     * @param id The String id GraphStream identifies the node with.
     */
    void setGsNode(String id) {
        this.gsNode = this.graph.getVisualizedGraph().getNode(id);
        assert (this.gsNode != null);
    }

    /**
     * @return the node object of GraphStream.
     */
    public Node getGsNode() {
        return this.gsNode;
    }

    /**
     * Gets new information about the visualization of an instance out of the model
     * and refreshes it. Only gets a notification that something has changed but
     * does not know what exactly.
     *
     * Use this method for nothing else but as a listener, especially not for
     * initially building the object, as the view will also be refreshed and
     * therefore an infinite loop may occur.
     *
     * @see InstanceListenerInterface
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
        this.reposition();

        String style = "";
        style += this.changeInstanceTypeSymbol();
        style += this.getStyleSheetMarks();
        style = "size: 40px; fill-mode: image-scaled;" + style
                + " text-background-mode: plain; text-alignment: under; z-index: 4;";

        this.gsNode.setAttribute("ui.style", style);
    }

    /**
     * Shows or hides the edge in the visualization.
     *
     */
    private void showHide() {
        if (!this.instance.isVisible()) {
            this.gsNode.setAttribute("ui.hide");
        } else {
            this.gsNode.removeAttribute("ui.hide");
        }
    }

    /**
     * Positions a node at a certain (x,y)-coordinate.
     *
     */
    private void reposition() {
        Optional<Point> position = this.instance.getPosition();
        if (!position.isEmpty()) {
            this.gsNode.setAttribute("x", position.get().getX());
            this.gsNode.setAttribute("y", position.get().getY());
        }
    }

    /**
     * Changes the icon that is the node and represents e.g. a type of instance,
     * could be a default icon.
     *
     * @return The path to the image as String to use in the style sheet.
     */
    private String changeInstanceTypeSymbol() {
        try {
            if (this.instance.getIcon() == null) { // insert default icon
                return "fill-image: url('" + this.getDefaultIcon().getPath().toString() + "');";
            } else {
                String path = (this.instance.getIcon().getPath()).toString();
                return "fill-image: url('" + path + "');";
            }
        } catch (IconDatabaseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Changes the style of the mark surrounding the node representing the instance
     * in the graph.
     *
     * @return The current style sheet for that particular node.
     */
    private String getStyleSheetMarks() {
        String stroke;
        String shape;
        String color;

        assert (this.instance != null);
        assert (this.instance.getEffectiveMark() != null);

        Optional<InstanceStroke> strokeOpt = this.instance.getEffectiveMark().getStroke();
        Optional<InstanceShape> shapeOpt = this.instance.getEffectiveMark().getShape();
        Optional<Color> colorOpt = this.instance.getEffectiveMark().getColor();

        if (strokeOpt.isEmpty()) {
            stroke = "none";
        } else {
            stroke = strokeOpt.get().toString().toLowerCase();
        }

        if (shapeOpt.isEmpty()) {
            shape = "box";
        } else {
            shape = shapeOpt.get().toString().toLowerCase();
        }

        if (colorOpt.isEmpty()) {
            color = "rgb(0,0,0);";
        } else {
            color = "rgb(" + colorOpt.get().getRed() + "," + colorOpt.get().getGreen() + "," + colorOpt.get().getBlue()
                    + ");";
        }

        return "shape: " + shape + "; stroke-mode: " + stroke + "; stroke-color: " + color;
    }

    /**
     * @return the default icon. (a question mark)
     */
    public IconInterface getDefaultIcon() {
        InstanceTypeConfiguration itc = this.getInstance().getTypes().stream().findFirst().orElse(null);
        assert (itc != null);
        return this.graph.getViewInterface().getDefaultIcon(itc.getInstanceType());
    }

    /**
     * @return the configuration the node is represented with.
     */
    public InstanceConfiguration getInstance() {
        return this.instance;
    }

}
