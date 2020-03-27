/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.controller;

import java.util.Set;

import edu.kit.informatik.tolowiz.model.data.icons.IconInterface;
import edu.kit.informatik.tolowiz.model.ontology.ValueType;
import edu.kit.informatik.tolowiz.model.visualization.CameraConfiguration;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;
import edu.kit.informatik.tolowiz.model.visualization.DefaultHandler;
import edu.kit.informatik.tolowiz.model.visualization.Group;
import edu.kit.informatik.tolowiz.model.visualization.InstanceConfiguration;
import edu.kit.informatik.tolowiz.model.visualization.InstanceMark;
import edu.kit.informatik.tolowiz.model.visualization.InstanceTypeConfiguration;
import edu.kit.informatik.tolowiz.model.visualization.Point;
import edu.kit.informatik.tolowiz.model.visualization.RelationStyle;
import edu.kit.informatik.tolowiz.model.visualization.RelationTypeConfiguration;
import edu.kit.informatik.tolowiz.view.graph.GraphControllerInterface;

/**
 * A controller that receives instructions from the
 * {@link edu.kit.informatik.tolowiz.view.graph Graph} and forwards them to the
 * {@link edu.kit.informatik.tolowiz.model.visualization.Configuration
 * Configuration}.<br>
 * It is always associated with a configuration to work on. Methods should be
 * treated as commands, that means they are <b>public void</b> with one or only
 * a few parameters.
 *
 * @author Fabian Palitza
 * @version 1.0
 * @see edu.kit.informatik.tolowiz.view.graph.GraphControllerInterface
 *      GraphControllerInterface
 *
 */
@SuppressWarnings("deprecation") // CameraConfiguration is deprecated, but not scheduled for removal.
class GraphController implements GraphControllerInterface {

    private final TabController controller;

    /**
     * Creates a new GraphController
     *
     * @param tabController the tab controller this graph controller is associated
     *                      with.
     */
    GraphController(TabController tabController) {
        this.controller = tabController;
    }

    // Private helper methods to simplify argument checking

    private void checkValueType(InstanceTypeConfiguration type, ValueType value) throws IllegalArgumentException {
        if (!type.getAllValues().contains(value)) {
            throw new IllegalArgumentException(
                    "InstanceTypeConfiguration" + type.getName() + "does not have values for type " + value.getName());
        }
    }

    private void checkInstanceType(InstanceTypeConfiguration instanceType) throws IllegalArgumentException {
        if (!this.controller.getConfig().getInstanceTypes().contains(instanceType)) {
            throw new IllegalArgumentException("InstanceTypeConfiguration " + instanceType.getName()
            + " is not included in this configuration (ID: "
            + this.controller.getConfig().getOntology().getIRI() + ")");
        }
    }

    private void checkInstanceConfiguration(InstanceConfiguration instance) throws IllegalArgumentException {

        if (!this.controller.getConfig().getInstances().contains(instance)) {
            throw new IllegalArgumentException(
                    "InstanceConfiguration " + instance.getName() + " is not included in this configuration (ID: "
                            + this.controller.getConfig().getOntology().getIRI() + ")");

        }
    }

    private void checkRelationTypeConfiguration(RelationTypeConfiguration relationType)
            throws IllegalArgumentException {
        if (!this.controller.getConfig().getRelationTypes().contains(relationType)) {
            throw new IllegalArgumentException("RelationTypeConfiguration " + relationType.getName()
            + " is not included in this configuration (ID: "
            + this.controller.getConfig().getOntology().getIRI() + ")");
        }
    }

    private void checkGroup(Group g) throws IllegalArgumentException {
        if (!this.controller.getConfig().getGroups().contains(g)) {
            throw new IllegalArgumentException("Group " + g.getName() + "is not included in this configuration (ID: "
                    + this.controller.getConfig().getOntology().getIRI() + ")");
        }
    }

    /**
     * @throws IllegalArgumentException If the given InstanceType is not present in
     *                                  this configuration.
     */
    @Override
    public void showInstanceType(InstanceTypeConfiguration instanceType) throws IllegalArgumentException {
        this.checkInstanceType(instanceType);
        this.controller.doOperation();
        instanceType.getMembers().forEach(i -> {
            this.controller.getConfig().getHiddenInstances().remove(i);
            i.show();
        });
    }

    /**
     * @throws IllegalArgumentException If the given InstanceType is not present in
     *                                  this configuration.
     */
    @Override
    public void hideInstanceType(InstanceTypeConfiguration instanceType) throws IllegalArgumentException {
        this.checkInstanceType(instanceType);
        this.controller.doOperation();
        instanceType.getMembers().forEach(i -> {
            this.controller.getConfig().getHiddenInstances().remove(i);
            i.hide();
        });
    }

    /**
     * @throws IllegalArgumentException If the given node is not present in this
     *                                  configuration.
     */
    @Override
    public void showInstance(InstanceConfiguration node) throws IllegalArgumentException {
        this.checkInstanceConfiguration(node);
        this.controller.doOperation();
        this.controller.getConfig().getHiddenInstances().remove(node);
        node.show();
    }

    /**
     * @throws IllegalArgumentException If the given node is not present in this
     *                                  configuration.
     */
    @Override
    public void hideInstance(InstanceConfiguration node) throws IllegalArgumentException {
        this.checkInstanceConfiguration(node);
        this.controller.doOperation();
        this.controller.getConfig().getHiddenInstances().add(node);
        node.hide();
    }

    /**
     *
     * @throws IllegalArgumentException If the given group is not in this
     *                                  configuration.
     */
    @Override
    public void showGroup(Group g) throws IllegalArgumentException {
        this.checkGroup(g);
        this.controller.doOperation();
        g.getInstances().forEach(i -> {
            i.show();
            this.controller.getConfig().getHiddenInstances().remove(i);
        });
    }

    /**
     *
     * @throws IllegalArgumentException If the given group is not in this
     *                                  configuration.
     */
    @Override
    public void hideGroup(Group g) throws IllegalArgumentException {
        this.checkGroup(g);
        this.controller.doOperation();
        g.getInstances().forEach(i -> {
            i.hide();
            this.controller.getConfig().getHiddenInstances().remove(i);
        });
    }

    /**
     * @throws IllegalArgumentException If the given InstanceType is not present in
     *                                  this configuration or if the handler is not
     *                                  allowed for the given type.
     */
    @Override
    public void setHandlerForType(InstanceTypeConfiguration instanceType, DefaultHandler handler)
            throws IllegalArgumentException {
        this.checkInstanceType(instanceType);
        assert (handler != null);
        if (!handler.checkLegality(instanceType)) {
            throw new IllegalArgumentException(
                    "It's not possible to assign this handler to type " + instanceType.getName());
        }
        this.controller.doOperation();
        instanceType.setHandler(handler);
    }

    /**
     * @throws IllegalArgumentException If the given relationType is not present in
     *                                  this configuration.
     */
    @Override
    public void showRelationType(RelationTypeConfiguration relationType) throws IllegalArgumentException {
        this.checkRelationTypeConfiguration(relationType);
        this.controller.doOperation();
        relationType.show();
    }

    /**
     * @throws IllegalArgumentException If the given relationType is not present in
     *                                  this configuration.
     */
    @Override
    public void hideRelationType(RelationTypeConfiguration relationType) throws IllegalArgumentException {
        this.checkRelationTypeConfiguration(relationType);
        this.controller.doOperation();
        relationType.hide();
    }

    @Override
    public void showAllRelations() {
        this.controller.doOperation();
        this.controller.getConfig().getRelationTypes().forEach(RelationTypeConfiguration::show);
    }

    @Override
    public void hideAllRelations() {
        this.controller.doOperation();
        this.controller.getConfig().getRelationTypes().forEach(RelationTypeConfiguration::hide);
    }

    /**
     * @throws IllegalArgumentException If the given relationType is not present in
     *                                  this configuration.
     */
    @Override
    public void changeRelationTypeSymbol(RelationTypeConfiguration relationType, RelationStyle style)
            throws IllegalArgumentException {
        this.checkRelationTypeConfiguration(relationType);
        this.controller.doOperation();
        relationType.setStyle(style);
    }

    /**
     * @throws IllegalArgumentException If the given instanceType is not present in
     *                                  this configuration.
     */
    @Override
    public void changeInstanceTypeSymbol(InstanceTypeConfiguration instanceType, IconInterface icon)
            throws IllegalArgumentException {
        this.checkInstanceType(instanceType);
        this.controller.doOperation();
        instanceType.setIcon(icon);
    }

    /**
     * @throws IllegalArgumentException If the given node is not present in this
     *                                  configuration.
     */
    @Override
    public void markInstance(InstanceConfiguration node, InstanceMark mark) throws IllegalArgumentException {
        this.checkInstanceConfiguration(node);
        this.controller.doOperation();
        node.mark(mark);
    }

    /**
     * @throws IllegalArgumentException If the given node is not present in this
     *                                  configuration.
     */
    @Override
    public void unmarkInstance(InstanceConfiguration node, InstanceMark mark) throws IllegalArgumentException {
        this.checkInstanceConfiguration(node);
        this.controller.doOperation();
        node.unmark(mark);
    }

    /**
     * @throws IllegalArgumentException If the given node is not present in this
     *                                  configuration.
     */
    @Override
    public void clearInstance(InstanceConfiguration node) throws IllegalArgumentException {
        this.checkInstanceConfiguration(node);
        this.controller.doOperation();
        node.getMarks().clear();
        Point p = node.getPosition().orElse(null);
        node.setPosition((p != null) ? new Point(p.getX(), p.getY()) : null); // trigger change
    }

    /**
     * @implNote The given instance can be {@code null}. In this case, an empty
     *           group is created.
     */
    @Override
    public void createNewGroup(String name, InstanceConfiguration instance) {
        this.controller.doOperation();
        Group g = (instance == null ? new Group(name) : new Group(name, instance));
        this.controller.getConfig().getGroups().add(g);
    }

    /**
     * @throws IllegalArgumentException If the given set of instances is
     *                                  {@code null}
     */
    @Override
    public void createNewGroup(String name, Set<InstanceConfiguration> instances) throws IllegalArgumentException {
        if (instances == null) {
            throw new IllegalArgumentException("Can not create a group with a null set!");
        }
        this.controller.doOperation();
        Group g = new Group(name, instances);
        this.controller.getConfig().getGroups().add(g);
    }

    /**
     * @throws IllegalArgumentException If the given group is not part of the
     *                                  associated configuration.
     */
    @Override
    public void deleteGroup(Group g) throws IllegalArgumentException {
        this.checkGroup(g);
        this.controller.doOperation();
        g.clear();
        this.controller.getConfig().getGroups().remove(g);
    }

    /**
     * @throws IllegalArgumentException If the given group or instance is not part
     *                                  of the associated configuration.
     */
    @Override
    public void addToGroup(Group g, InstanceConfiguration instance) throws IllegalArgumentException {
        this.checkInstanceConfiguration(instance);
        this.checkGroup(g);
        this.controller.doOperation();
        g.addInstance(instance);
    }

    /**
     * @throws IllegalArgumentException If the given group or instance is not part
     *                                  of the associated configuration.
     */
    @Override
    public void removeFromGroup(Group g, InstanceConfiguration instance) throws IllegalArgumentException {
        this.checkInstanceConfiguration(instance);
        this.checkGroup(g);
        this.controller.doOperation();
        g.removeInstance(instance);
    }

    /**
     * @throws IllegalArgumentException If the type is not part of the
     *                                  configuration, or the value is not
     *                                  applicable for the given type.
     */
    @Override
    public void activateValue(InstanceTypeConfiguration type, ValueType value) throws IllegalArgumentException {
        this.checkInstanceType(type);
        this.checkValueType(type, value);
        this.controller.doOperation();
        type.activateValue(value);
    }

    /**
     * @throws IllegalArgumentException If the type is not part of the
     *                                  configuration, or the value is not
     *                                  applicable for the given type.
     */
    @Override
    public void deactivateValue(InstanceTypeConfiguration type, ValueType value) throws IllegalArgumentException {
        this.checkInstanceType(type);
        this.checkValueType(type, value);
        this.controller.doOperation();
        type.deactivateValue(value);
    }

    /**
     * @throws IllegalArgumentException If the type is not part of the configuration
     */
    @Override
    public void activateAllValues(InstanceTypeConfiguration type) throws IllegalArgumentException {
        this.checkInstanceType(type);
        this.controller.doOperation();
        type.getAllValues().forEach(type::activateValue);
    }

    /**
     * @throws IllegalArgumentException If the type is not part of the configuration
     */
    @Override
    public void deactivateAllValues(InstanceTypeConfiguration type) throws IllegalArgumentException {
        this.checkInstanceType(type);
        this.controller.doOperation();
        type.getAllValues().forEach(type::deactivateValue);
    }

    /**
     * @throws IllegalArgumentException If the node is not part of the configuration
     */
    @Override
    public void moveNodeTo(InstanceConfiguration node, double x, double y) {
        this.checkInstanceConfiguration(node);
        this.controller.doOperation();
        // ensures x fits
        /*
         * if (x > (this.controller.getConfig().getCameraConfiguration().getWidth() /
         * 2)) { this.controller.getConfig().getCameraConfiguration().setWidth((int)
         * Math.ceil(2 * x)); } else if (x <
         * -(this.controller.getConfig().getCameraConfiguration().getWidth() / 2)) {
         * this.controller.getConfig().getCameraConfiguration().setWidth((int)
         * Math.ceil(-2 * x)); } // ensures y fits if (y >
         * (this.controller.getConfig().getCameraConfiguration().getHeight() / 2)) {
         * this.controller.getConfig().getCameraConfiguration().setWidth((int)
         * Math.ceil(2 * y)); } else if (y <
         * -(this.controller.getConfig().getCameraConfiguration().getHeight() / 2)) {
         * this.controller.getConfig().getCameraConfiguration().setWidth((int)
         * Math.ceil(-2 * y)); }
         */
        node.setPosition(new Point(x, y));
    }

    @Override
    @Deprecated
    public void scrollBy(double dx, double dy) {
        this.controller.doOperation();
        CameraConfiguration camera = this.controller.getConfig().getCameraConfiguration();
        double scrollX = (camera.getWidth() / 100.0) * dx;
        double scrollY = (camera.getHeight() / 100.0) * dy;
        Point center = camera.getCenter();

        camera.setCenter(new Point(center.getX() + scrollX, center.getY() + scrollY));
    }

    /**
     * This includes instance position, selected types and instances, and
     * CameraConfiguration.
     */
    @Override
    public void restoreStandardAlignment() {
        this.controller.doOperation();
        this.controller.getConfig().getInstanceTypes().forEach(InstanceTypeConfiguration::hide);
        this.controller.getConfig().getHiddenInstances().clear();
        this.controller.getConfig().getRelationTypes().forEach(RelationTypeConfiguration::hide);
        this.controller.getConfig().getInstances().forEach(InstanceConfiguration::restoreDefaultPosition);
        this.controller.getConfig().getCameraConfiguration().reset();
    }

    /**
     * This includes Groups, Icons, Marks and Styles.
     */
    @Override
    public void restoreStandardView() {
        this.controller.doOperation();
        this.controller.getConfig().getGroups().forEach(Group::clearMarks);
        this.controller.getConfig().getInstanceTypes().forEach(t -> t.setIcon(this.controller.getDefaultIcon(t)));
        this.controller.getConfig().getInstances().forEach(i -> this.clearInstance(i));
        this.controller.getConfig().getRelationTypes().forEach(r -> r.setStyle(new RelationStyle()));
    }

    /**
     * @throws IllegalArgumentException if depth is negative
     */
    @Override
    public void setRelationDepth(int depth) throws IllegalArgumentException {
        if (depth < 0) {
            throw new IllegalArgumentException("The depth must not be negative!");
        }
        this.controller.doOperation();
        this.controller.getConfig().setDepth(depth);
    }

    /**
     * @throws IllegalArgumentException if parallel is less than 1.
     */
    @Override
    public void setNumberOfParallelRelations(int parallel) throws IllegalArgumentException {
        if (!(parallel > 0)) {
            throw new IllegalArgumentException("You can not display less than one relations in parallel");
        }
        this.controller.doOperation();
        this.controller.getConfig().setMaxParallelRelations(parallel);
    }

    @Override
    public Configuration getConfiguration() {
        assert (this.controller.getConfig() != null);
        return this.controller.getConfig();
    }

}
