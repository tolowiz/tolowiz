/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.ontology;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RelationTest {
    private Relation r;
    private RelationType rt;
    private Set<InstanceType> it;
    private Instance orig;
    private Instance dest;

    /**
     * setup methods
     */
    @BeforeEach
    public void buildObjects() {
        this.it = new HashSet<>();
        this.it.add(new InstanceType("A", null, null, null, null, "blepblop"));
        this.orig = new Instance("origin", "uri", this.it, null, null);
        this.dest = new Instance("destination", "uri", this.it, null, null);
        this.rt = new RelationType("B", "iri");

    }

    /**
     * tests whether the relation's relationType is returned correctly
     */
    @Test
    public void testGetRelationType() {
        this.r = new Relation("uri", this.rt, this.orig, this.dest);
        Assertions.assertTrue(this.rt.equals(this.r.getRelationType()));
    }

    /**
     * tests whether the relation's origin Instance is returned correctly
     */
    @Test
    public void testGetOrigin() {
        this.r = new Relation("uri", this.rt, this.orig, this.dest);
        Assertions.assertTrue(this.orig.equals(this.r.getOrigin()));
    }

    /**
     * tests whether the relation's destination Instance is returned correctly
     */
    @Test
    public void testGetDestination() {
        this.r = new Relation("uri", this.rt, this.orig, this.dest);
        Assertions.assertTrue(this.dest.equals(this.r.getDestination()));
    }

}
