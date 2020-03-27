/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.ontology;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RelationTypeTest {

    /**
     * tests RelationType Constructor
     */
    @Test
    public void testCreateRelationType() {
        RelationType rt = new RelationType("A", "uri");
        Assertions.assertEquals("A", rt.getName());
    }

    /**
     * tests if relations are added correctly to their relationType.
     */
    @Test
    public void testAddRelation() {
        RelationType rt = new RelationType("A", "uri");
        Relation r = new Relation("uri", rt, null, null);
        rt.addRelations(r);
        Assertions.assertTrue(rt.getRelations().contains(r));
        Assertions.assertTrue(rt.getRelations().size() == 1);
    }

}
