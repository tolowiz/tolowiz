/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.informatik.tolowiz.model.ontology.Ontology;
import edu.kit.informatik.tolowiz.model.ontology.RelationType;

public class RelationTypeConfigurationTest {
    private RelationTypeConfiguration objectToTest;
    private RelationType base;

    /**
     * Generates a new OTT for each test.
     */
    @BeforeEach
    void setUp() {
        Ontology ont = new Ontology("someiri_anne_was_here", "hi");
        this.base = ont.addRelationType("typeName", "someuri_anne_was_here");
        this.objectToTest = new RelationTypeConfiguration(this.base, null);
    }

    /**
     * Asserts that the name matches the name of the base relation.
     */
    @Test
    void testName() {
        Assertions.assertEquals(this.objectToTest.getName(), this.base.getName());
    }

    /**
     * Ensures that the visibility methods are working correctly.
     */
    @Test
    void testVisibility() {
        Assertions.assertFalse(this.objectToTest.isVisible());
        this.objectToTest.show();
        Assertions.assertTrue(this.objectToTest.isVisible());
        this.objectToTest.hide();
        Assertions.assertFalse(this.objectToTest.isVisible());
    }

    /**
     * Tests if the style methods work correctly.
     */
    @Test
    void testStyles() {
        RelationStyle style = new RelationStyle();
        Assertions.assertEquals(this.objectToTest.getStyle(), style);
        style.setColor(Color.RED);
        Assertions.assertNotEquals(this.objectToTest.getStyle(), style);
    }
}
