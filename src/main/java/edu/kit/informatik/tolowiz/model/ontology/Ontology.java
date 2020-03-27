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
 * The ontology contains all knowledge of a certain domain. This knowledge is
 * represented as Types of Instances, a hierarchy between types and relations
 * between Instances which might also have values.
 *
 * @author Anne Bernhart
 *
 */
public class Ontology implements Serializable {
    private static final long serialVersionUID = 5837962765554811342L;
    private Set<InstanceType> types;
    private Set<Instance> instances;
    private Set<ValueType> valueTypes;
    private Set<Relation> relations;
    private Set<RelationType> relationTypes;
    private String iri;
    private String name;

    /**
     * Create an ontology with the specific iri
     *
     * @param iri  the iri of this Ontology derived from the file
     * @param name of the Ontology file
     */
    public Ontology(String iri, String name) {
        this.types = new HashSet<>();
        this.instances = new HashSet<>();
        this.valueTypes = new HashSet<>();
        this.relations = new HashSet<>();
        this.relationTypes = new HashSet<>();
        this.iri = iri;
        this.name = name;
    }

    /**
     * @return name of this ontology as String
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the number of Instances in this ontology
     */
    public int getNumberOfRelations() {
        return this.relations.size();
    }

    /**
     * @return the number of relations in this ontology
     */
    public int getNumberOfInstances() {
        return this.instances.size();
    }

    /**
     * Adds a new Instance to the ontology
     *
     * @param name      the name of the new Instance
     * @param uri       the uri
     * @param types     the InstanceTypes of the new Instance
     * @param relations a Set of Relations concerning the new Instance
     * @param values    a Set of Pairs of ValueType and corresponding value as a
     *                  String
     * @return the newly created Instance object
     */
    public Instance addInstance(String name, String uri, Set<InstanceType> types, Set<Relation> relations,
            Set<Pair<ValueType, String>> values) {
        Instance newInstance = new Instance(name, uri, types, relations, values);
        for (InstanceType type : types) {
            type.addInstance(newInstance);
            for (Pair<ValueType, String> pair : values) {
                type.addValue(pair.getValue0());
            }
            this.addInstanceToSuperTypes(newInstance, type);
        }
        this.instances.add(newInstance);
        return newInstance;
    }

    /**
     * adds an Instance to the Instance-Set of all supertypes of this Instance
     *
     * @param ins  the instance
     * @param type the direct supertype of the instance
     */
    private void addInstanceToSuperTypes(Instance ins, InstanceType type) {
        Set<InstanceType> types = type.getSuperTypes();
        for (InstanceType currentType : types) {
            currentType.addInstance(ins);
            for (Pair<ValueType, String> pair : ins.getValues()) {
                type.addValue(pair.getValue0());
            }
            if (!currentType.getSuperTypes().isEmpty()) {
                this.addInstanceToSuperTypes(ins, currentType);
            }
        }
    }

    @Override
    public int hashCode() {

        return ((this.iri == null) ? 0 : this.iri.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Ontology)) {
            return false;
        }
        Ontology other = (Ontology) obj;
        if (this.instances == null) {
            if (other.instances != null) {
                return false;
            }
        } else if (!this.instances.equals(other.instances)) {
            return false;
        }
        if (this.iri == null) {
            if (other.iri != null) {
                return false;
            }
        } else if (!this.iri.equals(other.iri)) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.relationTypes == null) {
            if (other.relationTypes != null) {
                return false;
            }
        } else if (!this.relationTypes.equals(other.relationTypes)) {
            return false;
        }
        if (this.relations == null) {
            if (other.relations != null) {
                return false;
            }
        } else if (!this.relations.equals(other.relations)) {
            return false;
        }
        if (this.types == null) {
            if (other.types != null) {
                return false;
            }
        } else if (!this.types.equals(other.types)) {
            return false;
        }
        if (this.valueTypes == null) {
            if (other.valueTypes != null) {
                return false;
            }
        } else if (!this.valueTypes.equals(other.valueTypes)) {
            return false;
        }
        return true;
    }

    /**
     * Adds a new Relation to the ontology
     *
     * @param uri         the uri of the relation
     * @param type        the type of the new Relation
     * @param origin      the Instance this Relation originates from
     * @param destination the destination Instance of this Relation
     * @return the newly created relation object
     */
    public Relation addRelation(String uri, RelationType type, Instance origin, Instance destination) {
        Relation newRelation = new Relation(uri, type, origin, destination);
        this.relations.add(newRelation);
        type.addRelations(newRelation);
        origin.addRelation(newRelation);
        return newRelation;
    }

    /**
     * Adds a new type of relation to the ontology.
     *
     * @param name the name of the RelationType you want to add
     * @param uri  the uri
     * @return the newly created RelationType object
     */
    public RelationType addRelationType(String name, String uri) {
        RelationType newRelationType = new RelationType(name, uri);
        this.relationTypes.add(newRelationType);
        return newRelationType;
    }

    /**
     *
     * @param name       The name of this type of instance.
     * @param values     The collection of values for this type of instance.
     * @param superTypes the supertypes for the type
     * @param subTypes   The sub types of the new InstanceType.
     * @param instances  The instances of this type of instance.
     * @param iri        The iri of the new type to add.
     * @return the newly created InstanceType object
     */
    public InstanceType addType(String name, Set<ValueType> values, Set<InstanceType> superTypes,
            Set<InstanceType> subTypes, Set<Instance> instances, String iri) {
        InstanceType newType = new InstanceType(name, values, superTypes, subTypes, instances, iri);
        this.types.add(newType);
        return newType;
    }

    /**
     * @param name The name of the value to add.
     * @param uri  the uri
     * @return the newly added ValueType object
     */
    public ValueType addValueType(String name, String uri) {
        ValueType newValueType = new ValueType(name, uri);
        this.valueTypes.add(newValueType);
        return newValueType;
    }

    /**
     * @return a set of all instances of the ontology
     */
    public Set<Instance> getInstances() {
        return this.instances;
    }

    /**
     * @return a Set of all Relations of the ontology
     */
    public Set<Relation> getRelations() {
        return this.relations;
    }

    /**
     * @return a Set of all RelationTypes of the ontology
     */
    public Set<RelationType> getRelationTypes() {
        return this.relationTypes;
    }

    /**
     * @return a set of all Types of the ontology
     */
    public Set<InstanceType> getTypes() {
        return this.types;
    }

    /**
     * @return a Set of all ValueTypes of the ontology
     */
    public Set<ValueType> getValueTypes() {
        return this.valueTypes;
    }

    /**
     * Adds a new subtype to the given type.
     *
     * @param type    the type you want to add a subtype to.
     * @param subType the subtype you want to add.
     */
    public void addSubType(InstanceType type, InstanceType subType) {
        type.addSubType(subType);
    }

    /**
     * sets the supertype of a given type.
     *
     * @param type      the type you want to set the supertype of.
     * @param superType the superType you want to set.
     */
    public void addSuperType(InstanceType type, InstanceType superType) {
        type.addSuperType(superType);
    }

    /**
     * Returns the IRI of the ontology.
     *
     * @return the IRI
     */
    public String getIRI() {
        return this.iri;
    }

}
