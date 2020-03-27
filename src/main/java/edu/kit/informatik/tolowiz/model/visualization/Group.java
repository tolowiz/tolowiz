/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * A group that holds multiple Instances.<br>
 * <br>
 *
 * The normal use case is to operate on all instances of a group, like
 * {@code Group.getInstances().forEach(m -> m.show());}
 *
 * @author Fabian Palitza
 * @version 1.0
 * @see InstanceConfiguration
 */
public class Group implements Serializable, Cloneable {

    private static final long serialVersionUID = -4614795669732819198L;

    /**
     * The set of Instances this group has
     *
     * @serial
     */
    private Set<InstanceConfiguration> instances;

    /**
     * The name of this group
     *
     * @serial
     */
    private String name;

    /**
     * The style of this group.
     *
     * @serial
     */
    private InstanceMark instanceMark;

    /**
     * Creates a new group with a name and no instances.
     *
     * @param name The name of the group
     */
    public Group(String name) {
        this.name = name;
        this.instances = new HashSet<>();
        this.instanceMark = new InstanceMark();
    }

    /**
     * Creates a new group from a name and a single InstanceConfiguration.
     *
     * @param name  The name of the group
     * @param entry The first entry of the group
     */
    public Group(String name, InstanceConfiguration entry) {
        this.name = name;
        this.instances = new HashSet<>();
        this.instances.add(entry);
        this.instanceMark = new InstanceMark();
    }

    /**
     * Creates and initializes a new group with a set of instances.
     *
     * @param name      The name of the group
     * @param instances A set of InstanceConfigurations to be used as base for this
     *                  group
     */
    public Group(String name, Set<InstanceConfiguration> instances) {
        this.name = name;
        this.instances = new HashSet<>(instances);
        this.instanceMark = new InstanceMark();
    }

    @Override
    public Group clone() {
        Object otherObject = null;
        try {
            otherObject = super.clone();
        } catch (CloneNotSupportedException e) {
            assert (false);
        }
        Group other = (Group) otherObject;
        other.name = this.name;
        other.instances = new HashSet<>();
        other.instanceMark = new InstanceMark();
        return other;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Group)) {
            return false;
        }
        Group other = (Group) obj;
        return Objects.equals(this.name, other.name);
    }

    /**
     * Adds an Instance to this group. Instances can be part of more than one group.
     *
     * @param instance The InstanceConfiguration to add.
     */
    public void addInstance(InstanceConfiguration instance) {
        this.instances.add(instance);
        instance.mark(this.instanceMark);
        instance.getGroups().add(this);
    }

    /**
     * Removes an Instance from this group.
     *
     * @param instance The InstanceConfiguration to remove.
     */
    public void removeInstance(InstanceConfiguration instance) {
        this.instances.remove(instance);
        instance.unmark(this.instanceMark);
        instance.getGroups().remove(this);
    }

    /**
     * Getter for the Instances this group in this configuration includes.
     *
     * @return a set of InstanceConfigurations with all instances from this group
     */
    public Set<InstanceConfiguration> getInstances() {
        return this.instances;
    }

    /**
     * Gets the given name of this group.
     *
     * @return the name of this group.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the given name of this group.
     *
     * @param name The new name for this group.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the mark for this specific group. It is not guaranteed that any instance
     * actually uses this style, as there may be other marks that are displayed with
     * a higher priority.
     *
     * @return the InstanceMark for this group.
     */
    public InstanceMark getMark() {
        return this.instanceMark;
    }

    /**
     * Sets the mark for this group and applies it to every member.
     *
     * @param m The InstanceMark to use.
     */
    public void setMark(InstanceMark m) {
        if (!m.getColor().isEmpty()) {
            this.instanceMark.setColor(m.getColor().get());
        }
        if (!m.getShape().isEmpty()) {
            this.instanceMark.setShape(m.getShape().get());
        }
        if (!m.getStroke().isEmpty()) {
            this.instanceMark.setStroke(m.getStroke().get());
        }
        this.instances.forEach(i -> i.mark(this.instanceMark));
    }

    /**
     * Resets the group. This removes all instances, sets the default style and
     * removes the previous style from all members.
     */
    public void clear() {
        this.clearMarks();
        Iterator<InstanceConfiguration> it = this.instances.iterator();
        while (it.hasNext()) {
            var instance = it.next();
            instance.getGroups().remove(this);
            it.remove();
        }
    }

    /**
     * Resets the marks of the group. Sets the default style and removes the
     * previous style from all members.
     */
    public void clearMarks() {
        this.instances.forEach(i -> i.unmark(this.instanceMark));
        this.instanceMark = new InstanceMark();
    }
}
