/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.informatik.tolowiz.model.ontology.Instance;
import edu.kit.informatik.tolowiz.model.ontology.InstanceType;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;
import edu.kit.informatik.tolowiz.model.ontology.ValueType;

public class InstanceTypeConfigurationTest {
    private InstanceTypeConfiguration objectToTest;
    private InstanceType base;
    private Set<InstanceType> subtypes;
    private Set<Instance> members;
    private Set<ValueType> values;

    /**
     * Generates a new OTT for each test.
     */
    @BeforeEach
    void setUp() {
        this.subtypes = new HashSet<>();
        this.members = new HashSet<>();
        this.values = new HashSet<>();

        Ontology ont = new Ontology("someiri_anne_was_here", "hi");
        this.base = ont.addType("internalName", this.values, null, this.subtypes, this.members, "internalIRI123");
        this.objectToTest = new InstanceTypeConfiguration(this.base);
    }

    /**
     * Tests if the name is stored correctly.
     */
    @Test
    void testName() {
        Assertions.assertEquals(this.objectToTest.getName(), this.base.getName());
    }

    /**
     * Tests if the IRI is stored correctly.
     */
    @Test
    void testIRI() {
        Assertions.assertEquals(this.objectToTest.getIRI(), this.base.getIRI());
    }

    /**
     * Tests the getSubTypes() method by assuring the two sets have the same size.
     */
    @Test
    void testSubtypesSize() {
        Assertions.assertEquals(this.objectToTest.getSubTypes().size(), this.base.getSubTypes().size());
    }

    /**
     * Tests the getAllValues() method to see if both sets are the same.
     */
    @Test
    void testAllValues() {
        Assertions.assertEquals(this.objectToTest.getAllValues(), this.base.getValues());
    }

    /**
     * Tests the getActiveValues() method to see if it returns a subset of all
     * values.
     */
    @Test
    void testActiveValues() {
        Assertions.assertTrue(this.objectToTest.getActiveValues().stream()
                .allMatch(a -> this.objectToTest.getAllValues().contains(a)));
    }

}
