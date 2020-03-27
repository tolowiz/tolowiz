/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import edu.kit.informatik.tolowiz.model.ontology.Relation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Used to get all style configuration information about relations. Each object
 * directly represents a relation between {@link InstanceConfiguration
 * Instances}, for example {@code "Router is-a HardwareObject"}.
 *
 * @author Fabian Palitza
 * @version 1.0
 * @see edu.kit.informatik.tolowiz.model.ontology.Relation
 */
public class RelationConfiguration implements Serializable, Cloneable {

    private static final long serialVersionUID = 1214510311548147661L;
    private String uri;

    /**
     * The {@link RelationTypeConfiguration type} this Relation belongs to.
     * There can be at most one such type, since there is no inheritance
     * structure for Relations. It holds informations that are true for all
     * relations of this type, like the style.
     *
     * @serial
     */
    private RelationTypeConfiguration type;

    /**
     * The two {@link InstanceConfiguration InstanceConfigurations} this
     * relation connects.
     *
     * @serial
     */
    private InstanceConfiguration instance1;

    /**
     * The two {@link InstanceConfiguration InstanceConfigurations} this
     * relation connects.
     *
     * @serial
     */
    private InstanceConfiguration instance2;

    /**
     * A list of all listeners that want to listen to changes of this Relation
     *
     * @transient
     */
    private transient List<RelationListenerInterface> listeners;
    private boolean initialized;

    /**
     * Creates a new Relation a Relation to base on and parent type.
     *
     * @param base the Relation this is based on
     * @param type The RelationTypeConfiguration this belongs to
     * @param instances The configuration
     */
    public RelationConfiguration(Relation base, RelationTypeConfiguration type,
            Set<InstanceConfiguration> instances) {
        Stream<InstanceConfiguration> str = instances.stream();
        this.instance1 = str
                .filter(a -> base.getOrigin().getURI().equals(a.getURI()))
                .findFirst().orElse(null);
        assert (this.instance1 != null);
        this.instance1.addRelation(this);
        str = instances.stream();
        this.instance2 = str
                .filter(a -> base.getDestination().getURI().equals(a.getURI()))
                .findFirst().orElse(null);
        assert (this.instance2 != null);
        this.instance2.addRelation(this);
        this.type = type;
        this.listeners = new LinkedList<>();
        this.uri = base.getURI();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.instance1, this.instance2, this.type,
                this.uri);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RelationConfiguration)) {
            return false;
        }
        RelationConfiguration other = (RelationConfiguration) obj;
        return Objects.equals(this.instance1, other.instance1)
                && Objects.equals(this.instance2, other.instance2)
                && Objects.equals(this.type, other.type)
                && Objects.equals(this.uri, other.uri);
    }

    /**
     * Clones a relationConfiguration with the given instances.
     *
     * @param type the cloned type
     * @param instances the cloned instances to use
     * @return a new RelationConfiguration, which will be equal to, but not the
     * same object as this.
     */
    public RelationConfiguration cloneWithInstances(
            RelationTypeConfiguration type,
            Set<InstanceConfiguration> instances) {
        Object otherObject = null;
        try {
            otherObject = super.clone();
        } catch (CloneNotSupportedException e) {
            assert (false);
        }
        RelationConfiguration other = (RelationConfiguration) otherObject;
        other.type = type;
        other.listeners = new LinkedList<>();
        other.uri = this.uri;
        Stream<InstanceConfiguration> str = instances.stream();
        other.instance1 = str.filter(i -> this.instance1.equals(i)).findFirst()
                .orElse(null);
        assert (other.instance1 != null);
        other.instance1.addRelation(other);
        str = instances.stream();
        other.instance2 = str.filter(i -> this.instance2.equals(i)).findFirst()
                .orElse(null);
        assert (other.instance2 != null);
        other.instance2.addRelation(other);
        return other;
    }

    /**
     * Gets the origin instance for this Relation.
     *
     * @return the InstanceConfiguration of the first instance in this relation.
     */
    public InstanceConfiguration getOrigin() {
        return this.instance1;
    }

    /**
     * Gets the destination instance for this Relation.
     *
     * @return the InstanceConfiguration of the second instance in this
     * relation.
     */
    public InstanceConfiguration getDestination() {
        return this.instance2;
    }

    /**
     * Gets the URI for this RelationConfiguration. It is taken from the
     * underlying {@link edu.kit.informatik.tolowiz.model.ontology.Relation
     * Relation}.
     *
     * @return an unique identifier for this Relation
     */
    public String getURI() {
        return this.uri;
    }

    /**
     * Getter for the Type of this relation.
     *
     * @return the RelationTypeConfiguration of the parent type.
     */
    public RelationTypeConfiguration getRelationType() {
        return this.type;
    }

    /**
     * Gets the current style for this Relation.<br>
     * This is handled on the type level, since Relations can not be customized
     * individually.
     *
     * @return the style of the RelationTypeConfiguration that is used for this
     * Relation.
     * @see RelationTypeConfiguration#getStyle()
     * @see RelationTypeConfiguration#setStyle(RelationStyle)
     */
    public RelationStyle getCurrentStyle() {
        return this.type.getStyle();
    }

    /**
     * Checks if this relation is visible in the current configuration.<br>
     * This is only true if the parent type is visible and both instances are
     * visible.
     *
     * @return {@code True} if visible, {@code False} if invisible.
     * @see RelationTypeConfiguration#isVisible()
     * @see InstanceConfiguration#isVisible()
     */
    public boolean isVisible() {
        return (this.type.isVisible() && this.instance1.isVisible()
                && this.instance2.isVisible());
    }

    /**
     * Adds a ChangeListener that is notified when properties of this Relation
     * change. <br>
     * Ideally, the listener would call this method with itself, for example
     * {@code relation.addListener(this)}, but it is also permitted to add other
     * listeners.
     *
     * @param listener an object implementing the RelationListenerInterface
     * which wants to be notified of changes
     * @see #changed()
     * @see #removeListener(RelationListenerInterface)
     */
    public void addListener(RelationListenerInterface listener) {
        assert (this.listeners != null);
        if (!this.initialized) {
            this.instance1.addRelation(this);
            this.instance2.addRelation(this);
            this.initialized = true;
        }
        this.listeners.add(listener);
    }

    /**
     * Removes a ChangeListener from this Relation. <br>
     *
     * @param listener The listener to be removed
     * @see #addListener(RelationListenerInterface)
     * @see #changed()
     */
    public void removeListener(RelationListenerInterface listener) {
        assert (this.listeners != null);
        this.listeners.remove(listener);
    }

    /**
     * Indicates that this object was changed.<br>
     * This could be called from this class or from
     * {@link RelationTypeConfiguration#changed()}. All registered listeners
     * will be notified of the change via
     * {@link RelationListenerInterface#onChange()}.
     */
    void changed() {
        assert (this.listeners != null);
        for (RelationListenerInterface x : this.listeners) {
            assert (x != null);
        }
        this.listeners.forEach(RelationListenerInterface::onChange);
    }

    private void readObject(ObjectInputStream in)
            throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.listeners = new LinkedList<>();
        this.initialized = false;
    }
}
