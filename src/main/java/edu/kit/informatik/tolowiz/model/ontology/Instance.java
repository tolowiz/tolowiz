/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.ontology;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.javatuples.Pair;

/**
 * Instances (or Individuals in Protege context) are specific Objects contained
 * in the ontology. Instances are of a certain type, have values and are related
 * to other instances.
 *
 * @author Anne Bernhart
 *
 */
public class Instance implements Serializable {
    /**
     * Serial version uid
     */
    private static final long serialVersionUID = -204924436364939879L;

    private String name;
    private String uri;
    private Set<InstanceType> types;
    private Set<Relation> relations;
    private Set<Pair<ValueType, String>> values;

    /**
     * The constructor of the Instance class.
     *
     * @param name      the name of the Instance
     * @param uri       th uri
     * @param types     the types
     * @param relations a Set of relations concerning this Instance
     * @param values    a Set of ValueTypes and Strings representing the specific
     *                  values of this Instance
     */
    Instance(String name, String uri, Set<InstanceType> types, Set<Relation> relations,
            Set<Pair<ValueType, String>> values) {
        this.name = name;
        this.uri = uri;
        this.types = types;
        this.relations = (relations == null ? new HashSet<>() : relations);
        this.values = (values == null ? new HashSet<>() : values);
    }

    /**
     * Creates a new instance
     * 
     * @param name  the name of the instance
     * @param types the types the instance belongs to
     */
    Instance(String name, Set<InstanceType> types) {
        this.name = name;
        this.types = types;
        this.relations = new HashSet<>();
        this.values = new HashSet<>();
    }

    @Override
    public int hashCode() {

        return ((this.uri == null) ? 0 : this.uri.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Instance)) {
            return false;
        }
        Instance other = (Instance) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.types == null) {
            if (other.types != null) {
                return false;
            }
        } else if (!this.types.equals(other.types)) {
            return false;
        }
        if (this.uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!this.uri.equals(other.uri)) {
            return false;
        }
        if (this.values == null) {
            if (other.values != null) {
                return false;
            }
        } else if (!this.values.equals(other.values)) {
            return false;
        }
        return true;
    }

    /**
     * Gets you the name of the instance.
     *
     * @return the name of this instance.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the uri of this Instance.
     */
    public String getURI() {
        return this.uri;
    }

    /**
     * Gets you a set of all relations concerning this instance.
     *
     * @return relations that go from or to this instance.
     */
    public Set<Relation> getRelations() {
        return this.relations;
    }

    /**
     * Returns the type of the instance.
     *
     * @return type of the instance.
     */
    public Set<InstanceType> getType() {
        return this.types;
    }

    /**
     * Returns a set of pairs of values of this instance and the current content of
     * these values for the given instance.
     *
     * @return a set of pairs of values and content.
     */
    public Set<Pair<ValueType, String>> getValues() {
        return this.values;
    }

    /**
     * Adds a new relation that concerns this instance to the instances set of
     * relations.
     *
     * @param newRelation the relation that should be added.
     */
    void addRelation(Relation newRelation) {
        this.relations.add(newRelation);
    }

    /**
     * Adds a new value and it's content.
     *
     * @param type     the ValueType of the value you want to add.
     * @param newValue the content of this new value.
     */
    void addValue(ValueType type, String newValue) {
        this.values.add(new Pair<>(type, newValue));
    }

}
