/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.informatik.tolowiz.model.visualization.RelationStyle.ArrowShape;
import edu.kit.informatik.tolowiz.model.visualization.RelationStyle.RelationStroke;

public class StyleTest {

    private RelationStyle objectToTest;

    /**
     * Generates a new OTT for each test.
     */
    @BeforeEach
    void setUp() {
        this.objectToTest = new RelationStyle();
    }

    /**
     * Tests if the color is stored correctly.
     */
    @Test
    void testColor() {
        Color[] colorsToTest = {Color.WHITE, Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY, Color.BLACK, Color.RED,
                Color.PINK, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.CYAN, Color.BLUE};

        // empty object should have no value
        Assertions.assertTrue(this.objectToTest.getColor().isEmpty());

        // check all colors
        for (Color c : colorsToTest) {
            this.objectToTest.setColor(c);
            Assertions.assertEquals(this.objectToTest.getColor().get(), c);
            Assertions.assertEquals(this.objectToTest.getColor().get().hashCode(), c.hashCode());
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
        for (RelationStroke stroke : RelationStyle.RelationStroke.values()) {
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
        for (ArrowShape shape : RelationStyle.ArrowShape.values()) {
            this.objectToTest.setShape(shape);
            Assertions.assertEquals(this.objectToTest.getShape().get(), shape);
        }
    }

    /**
     * Tests the boolean values.
     */
    @Test
    void testReversed() {
        Assertions.assertFalse(this.objectToTest.isReversed());
        this.objectToTest.reverse(true);
        Assertions.assertTrue(this.objectToTest.isReversed());
        this.objectToTest.reverse(false);
        Assertions.assertFalse(this.objectToTest.isReversed());
    }
}
