/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.graph;

import java.util.Set;

import edu.kit.informatik.tolowiz.model.data.icons.IconInterface;
import edu.kit.informatik.tolowiz.model.ontology.ValueType;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;
import edu.kit.informatik.tolowiz.model.visualization.DefaultHandler;
import edu.kit.informatik.tolowiz.model.visualization.Group;
import edu.kit.informatik.tolowiz.model.visualization.InstanceConfiguration;
import edu.kit.informatik.tolowiz.model.visualization.InstanceMark;
import edu.kit.informatik.tolowiz.model.visualization.InstanceTypeConfiguration;
import edu.kit.informatik.tolowiz.model.visualization.RelationStyle;
import edu.kit.informatik.tolowiz.model.visualization.RelationTypeConfiguration;

/**
 * Interface that provides methods to the controller so that the view can call
 * them. Implemented by GraphController in the controller.visualization package.
 *
 * @author Anja, Fabian
 * @version 1.0
 */
public interface GraphControllerInterface {

    /**
     * Shows the nodes of a certain type of instance of the graph.
     *
     * @param instanceType The type that should be visualized.
     */
    public void showInstanceType(InstanceTypeConfiguration instanceType);

    /**
     * Hides the nodes of a certain type of instance of the graph.
     *
     * @param instanceType The type that should not be visualized (anymore).
     */
    public void hideInstanceType(InstanceTypeConfiguration instanceType);

    /**
     * Sets a handler for a given InstanceTypeConfiguration. It will be used to
     * calculate positions for instances of that type.
     *
     * @param instanceType The type to set the handler for
     * @param handler      The handler to set
     */
    public void setHandlerForType(InstanceTypeConfiguration instanceType, DefaultHandler handler);

    /**
     * Shows the node of a single instance of the graph.
     *
     * @param node The node that should be visualized.
     */
    public void showInstance(InstanceConfiguration node);

    /**
     * Hides the node of a single instance of the graph.
     *
     * @param node The node that should not be visualized (anymore).
     */
    public void hideInstance(InstanceConfiguration node);

    /**
     * Shows an entire group.
     *
     * @param g The Group to show.
     */
    public void showGroup(Group g);

    /**
     * Hides an entire group.
     *
     * @param g The Group to hide.
     */
    public void hideGroup(Group g);

    /**
     * Shows the edges of certain type of relation of the graph.
     *
     * @param relationType The type of relation that should be visualized.
     */
    public void showRelationType(RelationTypeConfiguration relationType);

    /**
     * Hides the edges of certain type of relation of the graph.
     *
     * @param relationType The type of relation that should not be visualized
     *                     (anymore).
     */
    public void hideRelationType(RelationTypeConfiguration relationType);

    /**
     * Shows the edges of all relations of the graph.
     */
    public void showAllRelations();

    /**
     * Hides the edges of all relations of the graph.
     */
    public void hideAllRelations();

    /**
     * Changes the visualization style of the edges for a certain type of relation.
     *
     * @param relationType The type of relation.
     * @param style        The new style the relation should be visualized with.
     */
    public void changeRelationTypeSymbol(RelationTypeConfiguration relationType, RelationStyle style);

    /**
     * Changes the icon that visualizes an instance for all nodes of a certain type
     * of instance.
     *
     * @param instanceType The type of instance.
     * @param icon         The icon that should visualize the nodes.
     */
    public void changeInstanceTypeSymbol(InstanceTypeConfiguration instanceType, IconInterface icon);

    /**
     * Adds a mark to highlight a certain node.
     *
     * @param node The highlighted node.
     * @param mark The mark that should highlight the node.
     */
    public void markInstance(InstanceConfiguration node, InstanceMark mark);

    /**
     * Removes a mark that highlights a certain node.
     *
     * @param node The highlighted node.
     * @param mark The mark that should no longer highlight the node.
     */
    public void unmarkInstance(InstanceConfiguration node, InstanceMark mark);

    /**
     * Removes all marks from a node.
     *
     * @param node The highlighted node.
     */
    public void clearInstance(InstanceConfiguration node);

    /**
     * Creates a new Group with a single instance in it.
     *
     * @param name     The name of the group.
     * @param instance The instance in it.
     */
    public void createNewGroup(String name, InstanceConfiguration instance);

    /**
     * Creates a new Group with multiple instances in it.
     *
     * @param name      The name of the group.
     * @param instances The instances in it.
     */
    public void createNewGroup(String name, Set<InstanceConfiguration> instances);

    /**
     * Deletes a group from the configuration.
     *
     * @param g the Group to delete
     */
    public void deleteGroup(Group g);

    /**
     * Adds an instance to a already existing Group.
     *
     * @param g        The group
     * @param instance The instance that will be added.
     */
    public void addToGroup(Group g, InstanceConfiguration instance);

    /**
     * Removes an instance from a already existing Group.
     *
     * @param g        The group
     * @param instance The instance that will be removed.
     */
    public void removeFromGroup(Group g, InstanceConfiguration instance);

    /**
     * Selects a type of value so that it will be shown for a selected instance of a
     * certain type of instance in the data area.
     *
     * @param type  The type of instance a type of value should be shown for.
     * @param value The type of value.
     */
    public void activateValue(InstanceTypeConfiguration type, ValueType value);

    /**
     * Unselects a type of value so that it will not be shown for a selected
     * instance of a certain type of instance in the data area.
     *
     * @param type  The type of instance a type of value should not be shown for.
     * @param value The type of value.
     */
    public void deactivateValue(InstanceTypeConfiguration type, ValueType value);

    /**
     * Selects all values to be shown for a certain InstanceTypeConfiguration.
     * @param type The InstanceType to show all values for.
     */
    public void activateAllValues(InstanceTypeConfiguration type);

    /**
     * Deselects all values from being shown for a certain InstanceTypeConfiguration.
     * @param type The InstanceType to show no values for.
     */
    public void deactivateAllValues(InstanceTypeConfiguration type);

    /**
     * Reposition a single node of the graph.
     *
     * @param node The node that will be repositioned.
     * @param x    The x-coordinate to move it to
     * @param y    The y-coordinate to move it to
     */
    public void moveNodeTo(InstanceConfiguration node, double x, double y);

    /**
     * Scrolls on the graph by delta x and delta y.
     *
     * @param dx The shift of the x-coordinate as delta in percent.
     * @param dy The shift of the y-coordinate as delta in percent.
     */
    public void scrollBy(double dx, double dy);

    /**
     * Restores the alignment of the nodes and edges that was shown when that
     * specific ontology was opened and visualized by the graph visualizing tool for
     * the first time.
     *
     */
    public void restoreStandardAlignment();

    /**
     * Restores the standard view of the graph, e.g. which area should be shown.
     */
    public void restoreStandardView();

    /**
     * Sets the relation depth.
     *
     * @param depth The (new) relation depth.
     */
    public void setRelationDepth(int depth);

    /**
     * Sets the maximal number of parallel relations that can be visualized between
     * two instances.
     *
     * @param parallel The number of parallel relations.
     */
    public void setNumberOfParallelRelations(int parallel);

    /**
     * @return current configuration.
     */
    public Configuration getConfiguration();

}
