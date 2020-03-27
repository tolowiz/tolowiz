/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import edu.kit.informatik.tolowiz.model.ontology.Instance;
import edu.kit.informatik.tolowiz.model.ontology.InstanceType;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;
import edu.kit.informatik.tolowiz.model.ontology.Relation;
import edu.kit.informatik.tolowiz.model.ontology.ValueType;
import edu.kit.informatik.tolowiz.model.visualization.InstanceMark.InstanceStroke;

import java.util.HashSet;
import java.util.Set;

import org.javatuples.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class InstanceConfigurationTest {
    private InstanceConfiguration objectToTest;
    private InstanceTypeConfiguration type;
    private Instance base;
    private InstanceType baseType;
    private Set<Instance> members;
    private Set<InstanceType> subtypes;
    private Set<ValueType> values;
    private Set<InstanceType> supertypes;
    private Set<InstanceTypeConfiguration> types;

    /**
     * Generates a new OTT for each test.
     */
    @BeforeEach
    void setUp() {

        this.members = new HashSet<>();
        this.values = new HashSet<>();
        this.subtypes = new HashSet<>();
        this.supertypes = new HashSet<>();

        Ontology ont = new Ontology("someiri_anne_was_here", "hi anne");
        // can't use a mock here, so we grab the real object
        this.baseType = ont.addType("internalTypeName", this.values, null,
                this.subtypes, this.members, "internalIRI123");
        this.supertypes.add(this.baseType);
        this.base = ont.addInstance("internalInstanceName",
                "someuri_anne_was_here", this.supertypes,
                new HashSet<Relation>(),
                new HashSet<Pair<ValueType, String>>());

        this.type = new InstanceTypeConfiguration(this.baseType);

        Configuration conf = Mockito.mock(Configuration.class);
        Mockito.when(conf.getHandlers())
                .thenReturn(new HashSet<DefaultHandler>());
        this.type.setHandler(new DefaultHandler(conf));

        this.types = new HashSet<>();
        this.types.add(this.type);

        this.objectToTest = new InstanceConfiguration(this.base, this.types);
    }

    /**
     * Tests if the name is stored correctly.
     */
    @Test
    void testName() {
        Assertions.assertEquals(this.objectToTest.getName(),
                this.base.getName());
    }

    /**
     * Tests if the groups behave correctly.
     */
    @Test
    void testGroups() {
        Assertions.assertTrue(this.objectToTest.getGroups().size() == 0);
        Group g = new Group("");
        this.objectToTest.addToGroup(g);
        Assertions.assertTrue(this.objectToTest.getGroups().contains(g));
        Assertions.assertTrue(this.objectToTest.getGroups().size() == 1);
        this.objectToTest.addToGroup(g);
        Assertions.assertTrue(this.objectToTest.getGroups().size() == 1);
        this.objectToTest.removeFromGroup(g);
        Assertions.assertTrue(this.objectToTest.getGroups().size() == 0);

    }

    /**
     * Tests the visibility-related methods.
     */
    @Test
    void testVisibility() {
        Assertions.assertFalse(this.objectToTest.isVisible());
        this.objectToTest.show();
        Assertions.assertTrue(this.objectToTest.isVisible());
        this.objectToTest.show();
        Assertions.assertTrue(this.objectToTest.isVisible());
        this.objectToTest.hide();
        Assertions.assertFalse(this.objectToTest.isVisible());
    }

    /**
     * Tests if the icon is correctly taken from the supertype.
     */
    @Test
    void testIcon() {
        Assertions.assertEquals(this.objectToTest.getIcon(),
                this.type.getIcon());
    }

    /**
     * Tests the marks by applying, removing, and re-applying different marks.
     */
    @Test
    void testMarks() {
        InstanceMark mark1 = new InstanceMark();
        InstanceMark mark2 = new InstanceMark();
        mark2.setColor(Color.RED);
        Assertions.assertTrue(this.objectToTest.getMarks().size() == 0);
        this.objectToTest.mark(mark1);
        Assertions.assertTrue(this.objectToTest.getMarks().size() == 1);
        this.objectToTest.mark(mark1);
        Assertions.assertTrue(this.objectToTest.getMarks().size() == 1);
        this.objectToTest.unmark(mark1);
        this.objectToTest.mark(mark2);
        Assertions.assertTrue(this.objectToTest.getMarks().get(0) == mark2);
        Assertions.assertTrue(this.objectToTest.getMarks().size() == 1);
        this.objectToTest.mark(mark1);
        Assertions.assertTrue(this.objectToTest.getMarks().get(1) == mark1);
        Assertions.assertTrue(this.objectToTest.getMarks().size() == 2);
        this.objectToTest.unmark(mark1);
        this.objectToTest.unmark(mark2);
        Assertions.assertTrue(this.objectToTest.getMarks().size() == 0);

    }

    /**
     * Tests if the position is correctly stored.
     */
    @Test
    void testPosition() {
        Point p = new Point(1, 2);
        Assertions.assertNull(this.objectToTest.getStoredPosition());
        this.objectToTest.setPosition(p);
        Assertions.assertEquals(this.objectToTest.getStoredPosition(), p);
        this.objectToTest.setPosition(null);
        Assertions.assertNull(this.objectToTest.getStoredPosition());
    }

    /**
     * Tests if setting and restoring the default position works
     */
    @Test
    public void testDefPosition() {
        Point pos = new Point(3, 6);
        Point old = this.objectToTest.getStoredPosition();
        Assertions.assertNotEquals(pos, old);
        this.objectToTest.setDefaultPosition(pos);
        Assertions.assertEquals(pos, this.objectToTest.getStoredPosition());
        this.objectToTest.setPosition(old);
        Assertions.assertNotEquals(pos, this.objectToTest.getStoredPosition());
        this.objectToTest.restoreDefaultPosition();
        Assertions.assertEquals(pos, this.objectToTest.getStoredPosition());
    }

    /**
     * Tests if cloning works correctly
     */
    @Test
    public void testCloneAndEquals() {
        InstanceConfiguration cloned = this.objectToTest
                .cloneWithTypes(this.types);
        Assertions.assertEquals(cloned, this.objectToTest);
    }

    /**
     * Tests if adding listeners and listening for change works as expected.
     */
    @Test
    public void testListeners() {
        InstanceListenerInterface listener = mock(
                InstanceListenerInterface.class);
        this.objectToTest.addListener(listener);
        InstanceMark someMark = new InstanceMark(Color.RED);
        this.objectToTest.mark(someMark); // trigger change event
        verify(listener).onChange();
        verifyNoMoreInteractions(listener);
        this.objectToTest.removeListener(listener);
        this.objectToTest.unmark(someMark); // trigger another change
    }

    /**
     * Tests the getEffectiveMark method.
     */
    @Test
    public void testEffectiveMark() {
        InstanceMark mark1 = new InstanceMark(Color.RED);
        mark1.setStroke(InstanceStroke.DASHES);
        InstanceMark mark2 = new InstanceMark(Color.GREEN);
        this.objectToTest.mark(mark1);
        this.objectToTest.mark(mark2);
        InstanceMark effectiveMark = this.objectToTest.getEffectiveMark();

        Assertions.assertTrue(effectiveMark.getShape().isEmpty());

        Assertions.assertFalse(effectiveMark.getColor().isEmpty());
        Assertions.assertEquals(effectiveMark.getColor().get(), Color.GREEN);

        Assertions.assertFalse(effectiveMark.getStroke().isEmpty());
        Assertions.assertEquals(effectiveMark.getStroke().get(),
                InstanceStroke.DASHES);
    }

    /**
     * Tests the getValues method.
     */
    @Test
    public void testValues() {
        this.type.getAllValues().forEach(this.type::activateValue);
        int sizeValues = this.type.getAllValues().size();
        int sizeRelations = 0; // in this test case there are no relations
        int sizeTypes = 1; // always 1 entry, even for multiple types;
        Assertions.assertEquals(this.objectToTest.getValues().size(),
                sizeTypes + sizeValues + sizeRelations);

    }
}
