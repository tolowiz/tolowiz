/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import edu.kit.informatik.tolowiz.model.data.icons.IconDatabaseException;
import edu.kit.informatik.tolowiz.model.data.icons.IconDatabaseInterface;
import edu.kit.informatik.tolowiz.model.data.icons.IconInterface;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;
import edu.kit.informatik.tolowiz.model.ontology.ValueType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * This class represents a visualization configuration of a certain ontology.
 * Everything that is needed to create an appropriate visualization in a view
 * should be obtainable through this class.<br>
 * <br>
 * Objects that wish to be notified when the configuration changes may register
 * themselves with {@link #addListener(ConfigurationListenerInterface)
 * addListener()}.
 *
 * @author Fabian Palitza, Tobias Klumpp
 * @version 1.0
 * @see edu.kit.informatik.tolowiz.model.ontology.Ontology
 *
 */
@SuppressWarnings("deprecation") // CameraConfiguration is deprecated, but not
// scheduled for removal.
public class Configuration implements Serializable, Cloneable {

    private static final long serialVersionUID = -6154790733308393570L;

    /**
     * The maximum number of parallel relations that should be displayed. If
     * more connections between Nodes are present, the Graph should display one
     * special relation indicating they are compressed into one, and the number
     * of different relations.
     *
     * @serial
     */
    private int maxParallelRelations;

    /**
     * The depth until that relations should be displayed when showing details.
     * Accepts non-negative values.<br>
     * A value of zero indicates no relations should be displayed, a value of
     * one only those directly connected to an instance.
     *
     * @serial
     */
    private int depth;

    /**
     * @serial The ontology this Configuration is for.
     */
    private Ontology ontology;

    /**
     * The highest type in the hierarchy. It should be called OWL:Thing. This is
     * the only type without supertypes, all other types have this or another
     * type as supertype.
     *
     * @serial
     */
    private InstanceTypeConfiguration baseConfiguration;

    /**
     * The types we need to save configurations for.
     *
     * @serial
     */
    private Set<InstanceTypeConfiguration> instanceTypes;

    /**
     * The relationtypes we need to save configurations for.
     *
     * @serial
     */
    private SortedSet<RelationTypeConfiguration> relationTypes;

    /**
     * The instances we need to save configurations for.
     *
     * @serial
     */
    private SortedSet<InstanceConfiguration> instances;

    /**
     * The instances that were deliberately hidden. A subset of all instances.
     *
     * @serial
     */
    private SortedSet<InstanceConfiguration> hiddenInstances;

    // no need for a set of RelationConfigurations, we'll figure it out from the
    // types.

    /**
     * The groups of this configuration. It is possible to have empty groups,
     * this is why we can't rely on figuring them out from the Instances.
     *
     * @serial
     */
    private Set<Group> groups;

    /**
     * The handlers for this configuration.
     *
     * @serial
     */
    private Set<DefaultHandler> handlers;

    @Deprecated
    private CameraConfiguration camera;

    private transient List<ConfigurationListenerInterface> listeners;

    /**
     * Constructor for a complete configuration from an ontology. This should be
     * the only contructor called externally, which will then contruct a
     * complete set of every configuration object needed.
     *
     * @param ontology the ontology to be used in this Configuration
     * @param iconSupplier an IconDatabase that is used to get default icons for
     * the InstanceTypes
     */
    public Configuration(Ontology ontology,
            IconDatabaseInterface iconSupplier) {
        this.ontology = ontology;
        this.maxParallelRelations = 1;
        this.depth = 1;

        this.instanceTypes = new HashSet<>();
        this.relationTypes = new TreeSet<>();

        ontology.getTypes().forEach(
                t -> this.instanceTypes.add(new InstanceTypeConfiguration(t)));
        this.instanceTypes.forEach(i -> i.initHierarchy(this.instanceTypes));

        this.baseConfiguration = this.instanceTypes.stream()
                .filter(itc -> itc.getSuperTypes().isEmpty()).findFirst()
                .orElseThrow();

        this.instances = new TreeSet<>();
        this.hiddenInstances = new TreeSet<>();
        ontology.getInstances().forEach(i -> this.instances
                .add(new InstanceConfiguration(i, this.instanceTypes)));

        // thankfully I can just recreate all the relations here since there is
        // no
        // ordering
        ontology.getRelationTypes().forEach(t -> this.relationTypes
                .add(new RelationTypeConfiguration(t, this.instances)));

        this.groups = new HashSet<>();
        this.handlers = new HashSet<>();
        DefaultHandler def = new DefaultHandler(this);
        new InterfaceHandler(this);

        this.camera = new CameraConfiguration();
        this.listeners = new LinkedList<>();

        // All elements created, now we need to do some initializing.
        for (InstanceTypeConfiguration t : this.instanceTypes) {
            IconInterface icon = null;
            try {
                icon = (iconSupplier != null
                        ? iconSupplier.getDefaultIcon(t.getInstanceType())
                                : null);
            } catch (IconDatabaseException e) {
                // icon is just null
            }
            t.setIcon(icon);
            t.setHandler(def);
        }

        assert (ontology.getInstances().size() == this.getInstances().size());
    }

    @Override
    public Configuration clone() {
        Object otherObject = null;
        try {
            otherObject = super.clone();
        } catch (CloneNotSupportedException e) {
            assert (false);
        }
        Configuration other = (Configuration) otherObject;
        other.ontology = this.ontology;
        other.maxParallelRelations = this.maxParallelRelations;
        other.depth = this.depth;

        other.instanceTypes = new HashSet<>(this.instanceTypes.size());

        this.instanceTypes.forEach(t -> other.instanceTypes.add(t.clone()));
        other.instanceTypes.forEach(i -> i.initHierarchy(other.instanceTypes));

        other.baseConfiguration = other.instanceTypes.stream()
                .filter(itc -> itc.getSuperTypes().isEmpty()).findFirst()
                .orElseThrow();

        other.instances = new TreeSet<>();
        other.hiddenInstances = new TreeSet<>();
        this.getInstances().forEach(i -> other.instances
                .add(i.cloneWithTypes(other.instanceTypes)));

        Set<InstanceConfiguration> instances = other.getInstances();
        other.relationTypes = new TreeSet<>();
        this.relationTypes.forEach(
                a -> other.relationTypes.add(a.cloneWithInstances(instances)));

        other.groups = new HashSet<>(this.groups.size());
        this.groups.forEach(g -> {
            Group copiedGroup = g.clone();
            other.groups.add(copiedGroup); // clones the group
            instances.stream().filter(copy -> g.getInstances().contains(copy))
            .forEach(copy -> copiedGroup.addInstance(copy)); // finds
            // and adds
            // copied
            // instances
        });

        other.handlers = new HashSet<>(this.handlers);

        other.camera = this.camera.clone();
        other.listeners = new LinkedList<>();
        return other;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.instanceTypes, this.ontology,
                this.relationTypes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Configuration)) {
            return false;
        }
        Configuration other = (Configuration) obj;
        return Objects.equals(this.instanceTypes, other.instanceTypes)
                && Objects.equals(this.ontology, other.ontology)
                && Objects.equals(this.relationTypes, other.relationTypes);
    }

    /**
     * Gets the CameraConfiguration for this Configuration.
     *
     * @return the associated CameraConfiguration
     * @deprecated See {@link CameraConfiguration}.
     */
    @Deprecated
    public CameraConfiguration getCameraConfiguration() {
        return this.camera;
    }

    /**
     * Gets a collection of all Groups currently present in the displayed
     * Configuration.
     *
     * @return a set including all {@link Group}s in this Configuration
     */
    public Set<Group> getGroups() {
        return this.groups;
    }

    /**
     * Gets a collection of all Instances in this Configuration.
     *
     * @return a set including all {@link InstanceConfiguration}s in this
     * Configuration.
     */

    public SortedSet<InstanceConfiguration> getInstances() {
        return this.instances;
    }

    /**
     * Gets a set of all hidden Instances. This includes instances that were
     * hidden by themself or a group.
     *
     * Instances of Types that are not visible are not included
     *
     * @return A set of all InstanceConfigurations that are currently hidden
     * from view.
     */
    public Set<InstanceConfiguration> getHiddenInstances() {
        return this.hiddenInstances;
    }

    /**
     * Gets a collection of all <i>Types</i> of Instances in this Configuration.
     *
     * @return a set including all {@link InstanceTypeConfiguration}s in this
     * Configuration.
     */

    public Set<InstanceTypeConfiguration> getInstanceTypes() {
        return this.instanceTypes;
    }

    /**
     * Gets a collection of all Relations in this Configuration.
     *
     * @return a set including all {@link RelationConfiguration}s in this
     * Configuration.
     */
    public Set<RelationConfiguration> getRelations() {
        return this.relationTypes.stream()
                .flatMap(rtc -> rtc.getMembers().stream())
                .collect(Collectors.toSet());
    }

    /**
     * Gets a collection of all <i>Types</i> of Relations in this Configuration.
     *
     * @return a set including all {@link RelationTypeConfiguration}s in this
     * Configuration.
     */
    public SortedSet<RelationTypeConfiguration> getRelationTypes() {
        return this.relationTypes;
    }

    /**
     * Gets the maximum number of parallel relations.
     *
     * @return the maximum number of relations that should be displayed at the
     * same time
     */
    public int getmaxParallelRelations() {
        return this.maxParallelRelations;
    }

    /**
     * Sets the maximum number of parallel relations.
     *
     * @param maxParallelRelations The new maximum number of parallel relations
     * that should be displayed together
     */
    public void setMaxParallelRelations(int maxParallelRelations) {
        this.maxParallelRelations = maxParallelRelations;
        this.changed();
    }

    /**
     * Sets the relation depths for the details.
     *
     * @param depth The depth until which relations should be displayed.
     */
    public void setDepth(int depth) {
        this.depth = depth;
        this.changed();
    }

    /**
     * Gets the relation depths for the details.
     *
     * @return the depth until which relations should be displayed.
     */
    public int getDepth() {
        return this.depth;
    }

    /**
     * Returns the ontology this configuration is associated with
     *
     * @return the ontology
     */
    public Ontology getOntology() {
        return this.ontology;
    }

    /**
     * Returns a set of handlers this configuration uses.
     *
     * @return the handlers for this Configuration
     */
    public Set<DefaultHandler> getHandlers() {
        return this.handlers;
    }

    /**
     * Finds out which type has no supertype(s). In a tree structure, this will
     * be the root element to list all instances / types.
     *
     * @return The root type of this configuration.
     */
    public InstanceTypeConfiguration getRootType() {
        return this.baseConfiguration;
    }

    /**
     * Gets a type if the IRI is known.
     *
     * @param iri The IRI to search for
     * @return An InstanceTypeConfiguration with the correct IRI, or
     * {@code null} if this IRI does not exist.
     */
    public InstanceTypeConfiguration getTypeByIRI(String iri) {
        return this.instanceTypes.stream().filter(it -> it.getIRI().equals(iri))
                .findFirst().orElse(null);
    }

    /**
     * Gets a RelationType if the name is known.
     *
     * @param name The name to search for
     * @return A RelationTypeConfiguration with the correct name, or
     * {@code null} if this name does not exist.
     */
    public RelationTypeConfiguration getRelationTypeByName(String name) {
        return this.relationTypes.stream()
                .filter(x -> x.getName().contentEquals(name)).findFirst()
                .orElse(null);
    }

    /**
     * Gets a ValueType if the URI is known.
     *
     * @param valueURI the URI to search for
     * @return A ValueType with the corect URi, or {code null} if this URI does
     * not exist.
     */
    public ValueType getValueTypeByURI(String valueURI) {
        return this.instanceTypes.stream()
                .flatMap(x -> x.getAllValues().stream()) // stream all possible
                // ValueTypes
                .filter(v -> v.getURI().contentEquals(valueURI)).findFirst()
                .orElse(null); // find a fitting or null
    }

    /**
     * Adds a {@link ConfigurationListenerInterface ChangeListener} that is
     * notified when the complete configuration changes.<br>
     *
     * @param listener an object implementing the
     * {@link ConfigurationListenerInterface} that wishes to be notified of
     * changes on the Configuration.
     * @see ConfigurationListenerInterface
     * @see #removeListener
     */
    public void addListener(ConfigurationListenerInterface listener) {
        if (this.listeners == null) {
            this.listeners = new LinkedList<>();
        }
        this.listeners.add(listener);
    }

    /**
     * Removes a previously added {@link ConfigurationListenerInterface
     * ChangeListener}.
     *
     * @param listener The listener to be removed
     * @see ConfigurationListenerInterface
     * @see #addListener
     */
    public void removeListener(ConfigurationListenerInterface listener) {
        if (this.listeners == null) {
            this.listeners = new LinkedList<>();
        }
        this.listeners.remove(listener);
    }

    /**
     * Indicates that the entire configuration was changed and should be
     * rebuild. All ConfigurationListeners will be notified.
     *
     * @see ConfigurationListenerInterface#onFullChange()
     */
    private void changed() {
        if (this.listeners == null) {
            this.listeners = new LinkedList<>();
        }
        this.listeners.forEach(ConfigurationListenerInterface::onFullChange);
    }

    private void readObject(ObjectInputStream in)
            throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.listeners = new LinkedList<>();
    }
}
