/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.ontology;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class RelationType implements Serializable {
    private static final long serialVersionUID = 9038643826534541074L;
    private String name;
    private String uri;
    private Set<Relation> relations;

    /**
     * The constructor of the RelationType class.
     *
     * @param name the name of this RelationType.
     * @param uri  the uri of this RelationType.
     */
    RelationType(String name, String uri) {
        this.name = name;
        this.uri = uri;
        this.relations = new HashSet<>();
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
        if (!(obj instanceof RelationType)) {
            return false;
        }
        RelationType other = (RelationType) obj;
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

    /**
     * @return a Set of all relations of this type
     */
    public Set<Relation> getRelations() {
        return this.relations;
    }

    /**
     * @param relation the new relation you want to add
     */
    public void addRelations(Relation relation) {
        this.relations.add(relation);
    }

    /**
     * @return the name of this RelationType.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the uri of this RelationType
     */
    public String getURI() {
        return this.uri;
    }
}
