/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.ontology;

import java.io.Serializable;

/**
 * All Instances can have values and these values need a Type to define what
 * they describe. A ValueType may be "IP4-address" or "Construction year".
 * Objects of this class represent a Type of Value. They should be referenced
 * whenever a specific value is introduced.
 *
 * @author Anne Bernhart
 *
 */
public class ValueType implements Serializable, Comparable<ValueType> {
    private static final long serialVersionUID = -7188505631903967325L;
    private String name;
    private String uri;

    /**
     * The constructor of the ValueType class.
     *
     * @param name the name of the new ValueType.
     * @param uri  the uri of the new ValueType.
     */
    ValueType(String name, String uri) {
        this.name = name;
        this.uri = uri;
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
        if (!(obj instanceof ValueType)) {
            return false;
        }
        ValueType other = (ValueType) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!this.uri.equals(other.uri)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(ValueType other) {
        return this.name.compareTo(other.name);
    }

    /**
     * @return the name of this ValueType.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the uri of this ValueType
     */
    public String getURI() {
        return this.uri;
    }

}
