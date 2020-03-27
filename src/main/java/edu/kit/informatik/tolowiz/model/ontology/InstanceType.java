/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.ontology;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * InstanceTypes (or classes in the Protege context) categorize Instances in the
 * ontology. InstanceTypes also have a hierarchy between them.
 *
 * @author Anne Bernhart
 */
public class InstanceType implements Serializable {

    /**
     * Serial version uid
     */
    private static final long serialVersionUID = -3422913142441434789L;
    private String name;
    private Set<ValueType> values;
    private Set<InstanceType> superTypes;
    private Set<InstanceType> subTypes;
    private Set<Instance> instances;
    private String iri;

    /**
     * The constructor of the InstanceType class.
     *
     * @param name       of the InstanceType
     * @param values     the ValueTypes Instances of this InstanceType will have
     * @param superTypes of this InstanceType
     * @param subTypes   a Set of InstanceTypes that are subtypes of this one
     * @param instances  a Set of Instances of this InstanceType
     * @param iri        of this InstanceType
     */
    InstanceType(String name, Set<ValueType> values, Set<InstanceType> superTypes, Set<InstanceType> subTypes,
            Set<Instance> instances, String iri) {
        this.name = name;
        this.values = (values != null ? values : new HashSet<>());
        this.superTypes = (superTypes != null ? superTypes : new HashSet<>());
        this.subTypes = (subTypes != null ? subTypes : new HashSet<>());
        this.instances = (instances != null ? instances : new HashSet<>());
        this.iri = iri;
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
        if (!(obj instanceof InstanceType)) {
            return false;
        }
        InstanceType other = (InstanceType) obj;
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
        if (this.superTypes == null) {
            if (other.superTypes != null) {
                return false;
            }
        } else if (!this.superTypes.equals(other.superTypes)) {
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
     * Gets you the name of this instance
     *
     * @return the name of this instance
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets you the superType of this Type.
     *
     * @return the direct superType of this Type. null, if no superType exists
     */
    public Set<InstanceType> getSuperTypes() {
        return this.superTypes;
    }

    /**
     * Gets you the subTypes of this Type.
     *
     * @return a set of all direct and indirect subtypes of this InstanceType
     */
    public Set<InstanceType> getSubTypes() {
        return this.subTypes;
    }

    /**
     * Gets you a set of all instances of this type.
     *
     * @return a set of all instances of this type
     */
    public Set<Instance> getInstances() {
        return this.instances;
    }

    /**
     * Gets you a set of all valueTypes of values, instances of this type will have.
     *
     * @return a set of all valueTypes, instances of this type will have
     */
    public Set<ValueType> getValues() {
        return this.values;
    }

    /**
     * Returns the IRI of this type.
     *
     * @return the IRI
     */
    public String getIRI() {
        return this.iri;
    }

    /**
     * Adds an instance of this type to the type's set of instances
     *
     * @param newInstance The instance to add.
     */
    void addInstance(Instance newInstance) {
        this.instances.add(newInstance);
    }

    /**
     * Adds a ValueType to this InstanceType. All Instances of this InstanceType
     * will have a value of this ValueType.
     *
     * @param newValue they new ValueType
     */
    void addValue(ValueType newValue) {
        this.values.add(newValue);
    }

    /**
     * @param superType the new superType of this InstanceType
     */
    void addSuperType(InstanceType superType) {
        this.superTypes.add(superType);
    }

    /**
     * Registers a new InstanceType as subtype of this InstanceType
     *
     * @param subType the new subType of this InstanceType
     */
    void addSubType(InstanceType subType) {
        this.subTypes.add(subType);
    }

}
