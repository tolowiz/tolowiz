/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.ontology;

import java.io.Serializable;

/**
 * Relations (or object properties in the Protege context) characterize
 * relationships between instances of our ontology. Since Relations have a
 * direction, they originate from one Instance and have the other as their
 * destination.
 *
 * @author Anne Bernhart
 */
public class Relation implements Serializable {
    private static final long serialVersionUID = -3239766920088454801L;
    private String uri;
    private RelationType type;
    private Instance origin;
    private Instance destination;

    /**
     * The constructor of the Relation class
     * @param uri the uri
     *
     * @param type        the RelationType of this new Relation
     * @param origin      the Instance, this Relation originates from
     * @param destination the destination Instance of this Relation
     */
    Relation(String uri, RelationType type, Instance origin, Instance destination) {
        this.uri = uri;
        this.type = type;
        this.origin = origin;
        this.destination = destination;
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
        if (!(obj instanceof Relation)) {
            return false;
        }
        Relation other = (Relation) obj;
        if (this.destination == null) {
            if (other.destination != null) {
                return false;
            }
        } else if (!this.destination.equals(other.destination)) {
            return false;
        }
        if (this.origin == null) {
            if (other.origin != null) {
                return false;
            }
        } else if (!this.origin.equals(other.origin)) {
            return false;
        }
        if (this.type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!this.type.equals(other.type)) {
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

    /**
     * @return the Instance, this Relation originates from.
     */
    public Instance getOrigin() {
        return this.origin;
    }

    /**
     * @return the destination Instance of this Relation.
     */
    public Instance getDestination() {
        return this.destination;
    }

    /**
     * @return the RelationType of this Relation.
     */
    public RelationType getRelationType() {
        return this.type;
    }

    /**
     * @return the uri of this Relation.
     */
    public String getURI() {
        return this.uri;
    }
}
