/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import edu.kit.informatik.tolowiz.model.ontology.RelationType;

/**
 * Represents the configuration of one type of relations, for example
 * {@code "is-a"}. <br>
 * <br>
 * This configuration is not supposed to be displayed directly, it is only used
 * to store data that is the same for all of its members, such as the style or
 * visibility.
 *
 * @author Fabian Palitza
 * @version 1.0
 * @see edu.kit.informatik.tolowiz.model.ontology.RelationType
 */
public class RelationTypeConfiguration implements Serializable, Cloneable, Comparable<RelationTypeConfiguration> {

    private static final long serialVersionUID = -2458056766160668110L;

    /**
     * The visibility of this RelationType in the current configuration. Accepts
     * boolean values which represent "visible" if true and "invisible" if false.
     *
     * @serial
     */
    private boolean visible;

    /**
     * The members of this RelationType. Accepts a {@link java.util.Set Set} of
     * {@link RelationConfiguration}s, which includes all Relations from this type.
     *
     * @serial
     */
    private Set<RelationConfiguration> members;

    /**
     * The name of this RelationTypeConfiguration
     *
     * @serial
     */
    private String name;

    /**
     * The style used to display relations of this type.
     *
     * @serial
     */
    private RelationStyle style;

    /**
     *
     * Creates a new RelationTypeConfiguration based on a RelationType
     *
     * @param base      the type to base this Configuration on.
     * @param instances the instances to search
     */
    RelationTypeConfiguration(RelationType base, Set<InstanceConfiguration> instances) {
        this.name = base.getName();
        this.style = new RelationStyle();
        this.members = new HashSet<>();
        base.getRelations().stream().forEach(a -> this.members.add(new RelationConfiguration(a, this, instances)));
        this.visible = false;
    }

    /**
     * Clones this RelationTypeConfiguration
     *
     * @param instances The cloned instances to use
     * @return a RelationTypeConfiguration which will be equal to, but not the same
     *         object as this.
     */
    RelationTypeConfiguration cloneWithInstances(Set<InstanceConfiguration> instances) {
        Object otherObject = null;
        try {
            otherObject = super.clone();
        } catch (CloneNotSupportedException e) {
            assert (false);
        }
        RelationTypeConfiguration other = (RelationTypeConfiguration) otherObject;
        other.name = this.name;
        other.style = this.style;
        other.members = new HashSet<>();
        this.members.forEach(r -> other.members.add(r.cloneWithInstances(other, instances)));
        other.visible = this.visible;
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
        if (!(obj instanceof RelationTypeConfiguration)) {
            return false;
        }
        RelationTypeConfiguration other = (RelationTypeConfiguration) obj;
        return Objects.equals(this.name, other.name);
    }


    @Override
    public int compareTo(RelationTypeConfiguration other) {
        return this.name.compareTo(other.name);
    }

    /**
     * Gets the name of this type.
     *
     * @return the name of this RelationType
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the members of this RelationType.
     *
     * @return a collection of the RelationConfigurations of this type.
     */
    public Set<RelationConfiguration> getMembers() {
        return this.members;
    }

    /**
     * Checks if this type of relations is visible in the current configuration.<br>
     * This does not necessarily imply that all members are visible, since some
     * {@link InstanceConfiguration Instances} could be invisible.
     *
     * @return {@code True} if visible, {@code False} if invisible.
     * @see #show()
     * @see #hide()
     * @see RelationConfiguration#isVisible()
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Shows this type of relations. If already visible, this method call will do
     * nothing. <br>
     * If it was previously invisible, it will be shown, meaning all members of this
     * type will be shown. Additionally, it calls the registered
     * {@link RelationListenerInterface ChangeListeners} of every member.
     *
     * @see #isVisible()
     * @see #hide()
     * @see RelationConfiguration#isVisible()
     */
    public void show() {
        if (!this.visible) {
            this.visible = true;
            this.changed();
        }
    }

    /**
     * Hides this type of relations. If already invisible, this method call will do
     * nothing. <br>
     * If it was previously visible, it will be hidden, meaning all members of type
     * will be hidden. Additionally, it calls the registered
     * {@link RelationListenerInterface ChangeListeners} of every member.
     *
     * @see #isVisible()
     * @see #show()
     * @see RelationConfiguration#isVisible()
     */
    public void hide() {
        if (this.visible) {
            this.visible = false;
            this.changed();
        }
    }

    /**
     * Gets the current style.<br>
     * This is used by the members of this type to display their own style.
     *
     * @return the style of this type
     * @see RelationConfiguration#getCurrentStyle()
     */
    public RelationStyle getStyle() {
        return this.style;
    }

    /**
     * Sets the style to use. <br>
     * Note that it is set on the Type level and returned at the
     * RelationConfiguration level.
     *
     * @param style the style to use
     */
    public void setStyle(RelationStyle style) {
        this.style = style;
        this.changed();
    }

    /**
     * Indicates that this object was changed. <br>
     * <br>
     * This means that all members could potentially have been changed, and their
     * {@link RelationListenerInterface ChangeListeners} need to be notified.
     *
     * @see RelationConfiguration#changed()
     */
    private void changed() {
        this.members.forEach(RelationConfiguration::changed);
    }
}
