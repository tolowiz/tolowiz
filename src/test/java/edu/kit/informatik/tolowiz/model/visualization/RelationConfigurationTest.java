/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.javatuples.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import edu.kit.informatik.tolowiz.model.ontology.Instance;
import edu.kit.informatik.tolowiz.model.ontology.InstanceType;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;
import edu.kit.informatik.tolowiz.model.ontology.Relation;
import edu.kit.informatik.tolowiz.model.ontology.ValueType;

public class RelationConfigurationTest {
    private RelationConfiguration objectToTest;
    private Relation base;
    private Instance origin;
    private Instance destination;
    private RelationTypeConfiguration parent;
    private RelationStyle style;
    private Configuration conf;

    /**
     * Generates a new OTT for each test.
     */
    @BeforeEach
    void setUp() {
        this.style = new RelationStyle();
        Ontology ont = new Ontology("someiri_anne_was_here", "hi");
        InstanceType type = ont.addType("type", null, null, null, new HashSet<Instance>(), "someIRI");
        Set<InstanceType> types = new HashSet<>();
        types.add(type);
        this.origin = ont.addInstance("instance1", "someuri_anne_was_here2", types, new HashSet<Relation>(),
                new HashSet<Pair<ValueType, String>>());
        this.destination = ont.addInstance("instance2", "someuri_anne_was_here_neu", types, new HashSet<Relation>(),
                new HashSet<Pair<ValueType, String>>());
        this.parent = Mockito.mock(RelationTypeConfiguration.class);
        Mockito.when(this.parent.isVisible()).thenReturn(true);
        Mockito.when(this.parent.getStyle()).thenReturn(null, this.style);

        this.conf = Mockito.mock(Configuration.class);
        TreeSet<InstanceConfiguration> members = new TreeSet<>();
        members.add(new InstanceConfiguration(this.origin, new TreeSet<InstanceTypeConfiguration>()));
        members.add(new InstanceConfiguration(this.destination, new TreeSet<InstanceTypeConfiguration>()));
        Mockito.when(this.conf.getInstances()).thenReturn(members);
        this.base = ont.addRelation("someuri_anne_was_here", ont.addRelationType("type", "someuri_anne_was_here"),
                this.origin, this.destination);
        this.objectToTest = new RelationConfiguration(this.base, this.parent, members);
    }

    /**
     * Asserts that the origin and destination fit.
     */
    @Test
    void testInstances() {
        Assertions.assertEquals(this.objectToTest.getOrigin().getName(), this.origin.getName());
        Assertions.assertEquals(this.objectToTest.getDestination().getName(), this.destination.getName());
    }

    /**
     * Ensures that the visibility methods are working correctly.
     */
    @Test
    void testVisibility() {
        Assertions.assertFalse(this.objectToTest.isVisible());
        this.objectToTest.getOrigin().show();
        Assertions.assertFalse(this.objectToTest.isVisible());
        this.objectToTest.getDestination().show();
        Assertions.assertTrue(this.objectToTest.isVisible());
    }

    /**
     * Tests if the style methods work correctly.
     */
    @Test
    void testStyles() {
        Assertions.assertEquals(this.objectToTest.getCurrentStyle(), null);
        this.parent.setStyle(this.style);
        Assertions.assertEquals(this.objectToTest.getCurrentStyle(), this.style);
    }

    /**
     * Tests if adding listeners and listening for change works as expected.
     */
    @Test
    public void testListeners() {
        RelationListenerInterface listener = mock(RelationListenerInterface.class);
        this.objectToTest.addListener(listener);
        this.objectToTest.changed(); // trigger change event
        verify(listener).onChange();
        verifyNoMoreInteractions(listener);
        this.objectToTest.removeListener(listener);
        this.objectToTest.changed(); // trigger another change
    }

    /**
     * Tests cloning and equals.
     */
    @Test
    public void testCloneAndEquals() {
        RelationConfiguration other = this.objectToTest.cloneWithInstances(this.objectToTest.getRelationType(),
                this.conf.getInstances());
        Assertions.assertEquals(this.objectToTest, other);

    }
}
