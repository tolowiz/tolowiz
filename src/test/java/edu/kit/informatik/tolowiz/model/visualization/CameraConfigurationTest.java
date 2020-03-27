/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @deprecated The test class is not needed because the
 *             {@link CameraConfiguration} itself is deprecated.
 */
@Deprecated
public class CameraConfigurationTest {
    private CameraConfiguration objectToTest;

    /**
     * Generates a new OTT for every test.
     */
    @BeforeEach
    public void setUp() {
        this.objectToTest = new CameraConfiguration();
    }

    /**
     * Tests the width getter and setter
     */
    @Test
    public void testWidth() {
        assertEquals(80, this.objectToTest.getWidth());
        this.objectToTest.setWidth(0);
        assertEquals(80, this.objectToTest.getWidth());
        this.objectToTest.setWidth(10);
        assertEquals(10, this.objectToTest.getWidth());
    }

    /**
     * Tests the height getter and setter
     */
    @Test
    public void testHeight() {
        assertEquals(45, this.objectToTest.getHeight());
        this.objectToTest.setHeight(0);
        assertEquals(45, this.objectToTest.getHeight());
        this.objectToTest.setHeight(10);
        assertEquals(10, this.objectToTest.getHeight());
    }

    /**
     * Tests the center getter and setter
     */
    @Test
    public void testCenter() {
        Point arg = new Point();
        assertEquals(new Point(0, 0), this.objectToTest.getCenter());
        arg.setLocation(this.objectToTest.getWidth(), 0);
        this.objectToTest.setCenter(arg); // out of bounds
        assertNotEquals(arg, this.objectToTest.getCenter());
        arg.setLocation(this.objectToTest.getHeight(), this.objectToTest.getHeight());
        this.objectToTest.setCenter(arg); // out of bounds
        assertNotEquals(arg, this.objectToTest.getCenter());
        arg.setLocation(5, 15); // inside bounds
        this.objectToTest.setCenter(arg);
        assertEquals(arg, this.objectToTest.getCenter());
    }

    /**
     * Tests the center setter with a null pointer
     */
    @Test
    public void testCenterException() {
        assertThrows(NullPointerException.class, () -> this.objectToTest.setCenter(null));
    }

    /**
     * Tests the reset method. Grid and Zoom are changed, then reset.
     */
    @Test
    public void testReset() {
        CameraConfiguration other = new CameraConfiguration();
        this.objectToTest.setGridActive(true);
        this.objectToTest.setZoom(500);
        this.objectToTest.reset();
        assertFalse(this.objectToTest.isGridActive());
        assertEquals(this.objectToTest.getZoom(), 100); // check zoom and grid, then compare to base object.
        assertEquals(this.objectToTest, other);
    }

    /**
     * Tests if adding listeners and listening for change works as expected.
     */
    @Test
    public void testListeners() {
        CameraListenerInterface listener = mock(CameraListenerInterface.class);
        this.objectToTest.addListener(listener);
        this.objectToTest.setHeight(1); // trigger change event
        verify(listener).onCameraConfigurationChange();
        verifyNoMoreInteractions(listener);
        this.objectToTest.removeListener(listener);
        this.objectToTest.setWidth(1); // trigger another change
    }
}
