/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.kit.informatik.tolowiz.model.visualization.InstanceMark.InstanceShape;

public class GroupTest {

    private Group objectToTest;

    /**
     * Generates a new OTT for each test.
     */
    @BeforeEach
    void setUp() {
        this.objectToTest = new Group("test" + System.currentTimeMillis());
    }

    /**
     * Tests the {@link Group#addInstance(InstanceConfiguration)} method by adding
     * one InstanceConfiguration and asserting it is there.
     */
    @Test
    void testAdd() {
        InstanceConfiguration config = Mockito.mock(InstanceConfiguration.class);
        this.objectToTest.addInstance(config);
        Assertions.assertTrue(this.objectToTest.getInstances().contains(config));
    }

    /**
     * Tests the {@link Group#removeInstance(InstanceConfiguration)} method by
     * adding and removing one InstanceConfiguration and asserting it is gone.
     */
    @Test
    void testRemove() {
        InstanceConfiguration config = Mockito.mock(InstanceConfiguration.class);
        this.objectToTest.addInstance(config);
        this.objectToTest.removeInstance(config);
        Assertions.assertFalse(this.objectToTest.getInstances().contains(config));
    }

    /**
     * Tests the {@link Group#getName()} and {@link Group#setName()} methods by
     * adding some text to the original name.
     */
    @Test
    void testName() {
        String additionalString = "new Name_randomCharacters * +-/";
        String originalName = this.objectToTest.getName();
        this.objectToTest.setName(originalName + additionalString);
        Assertions.assertNotEquals(this.objectToTest.getName(), originalName);
        Assertions.assertEquals(this.objectToTest.getName(), originalName + additionalString);
    }

    /**
     * Tests if setting and getting a mark works correctly.
     */
    @Test
    public void testMarks() {
        InstanceMark m = new InstanceMark(Color.RED);
        this.objectToTest.setMark(m);
        Assertions.assertEquals(m, this.objectToTest.getMark());
        InstanceMark m2 = new InstanceMark(InstanceShape.DIAMOND);
        this.objectToTest.setMark(m2);
        Assertions.assertNotEquals(m, this.objectToTest.getMark());
        m2.setColor(Color.RED);
        Assertions.assertEquals(m2, this.objectToTest.getMark());

    }

    /**
     * Tests the clone and equals methods
     */
    @Test
    public void testCloneAndEquals() {
        Group other = this.objectToTest.clone();
        Assertions.assertEquals(other, this.objectToTest);
    }
}
