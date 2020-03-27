/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.informatik.tolowiz.model.visualization.InstanceMark.InstanceShape;
import edu.kit.informatik.tolowiz.model.visualization.InstanceMark.InstanceStroke;

public class MarkTest {

    private InstanceMark objectToTest;

    /**
     * Generates a new OTT for each test.
     */
    @BeforeEach
    void setUp() {
        this.objectToTest = new InstanceMark();
    }

    /**
     * Tests if the color is stored correctly.
     */
    @Test
    void testColor() {
        Color[] colorsToTest = {Color.WHITE, Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY, Color.BLACK, Color.RED,
                Color.PINK, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.CYAN, Color.BLUE };

        // empty object should have no value
        Assertions.assertTrue(this.objectToTest.getColor().isEmpty());

        // check all colors
        for (Color c : colorsToTest) {
            this.objectToTest.setColor(c);
            Assertions.assertEquals(this.objectToTest.getColor().get(), c);
        }
    }

    /**
     * Tests if the stroke is stored correctly.
     */
    @Test
    void testStroke() {

        // empty object should have no value
        Assertions.assertTrue(this.objectToTest.getStroke().isEmpty());

        // check all strokes
        for (InstanceStroke stroke : InstanceMark.InstanceStroke.values()) {
            this.objectToTest.setStroke(stroke);
            Assertions.assertEquals(this.objectToTest.getStroke().get(), stroke);
        }
    }

    /**
     * Tests if the shape is stored correctly.
     */
    @Test
    void testShape() {

        // empty object should have no value
        Assertions.assertTrue(this.objectToTest.getShape().isEmpty());

        // check all strokes
        for (InstanceShape shape : InstanceMark.InstanceShape.values()) {
            this.objectToTest.setShape(shape);
            Assertions.assertEquals(this.objectToTest.getShape().get(), shape);
        }
    }
}
