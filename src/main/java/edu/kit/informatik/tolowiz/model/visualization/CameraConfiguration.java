/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the configuration of a camera in a complete {@link Configuration}.
 * <br>
 * <br>
 * The coordinates are all in relative units, where 1 Unit is the standard size
 * of a Node. This ensures that Configurations will look the same across
 * different screens.
 *
 * @author Fabian Palitza
 * @version 1.0
 * @deprecated We found it is not necessary to store camera-related information
 *             in a configuration. Therefore the class is not needed anymore.
 */
@Deprecated
public class CameraConfiguration implements Serializable, Cloneable {

    /**
     * {@code public static final int MAX_ZOOM = 10000}<br>
     * <br>
     * The maximum zoom factor that could reasonably be implemented.<br>
     * <br>
     * Since the standard zoom factor is 100, a factor of 10000 represents a 100x
     * zoom. The minimal zoom factor is 1.
     */
    public static final int MAX_ZOOM = 10000;

    private static final long serialVersionUID = 1077150999270269238L;

    /**
     * @serial The central point the camera is looking at. Defaults to (0|0)
     */
    private Point center;

    /**
     * @serial Whether the grid is enabled for this configuration.
     */
    private Boolean grid; // Optional

    /**
     * @serial The zoom factor of the camera. Accepts values from 1 to
     *         {@link MAX_ZOOM}
     */
    private int zoom;

    /**
     * @serial The current width of the canvas.
     */
    private int width;

    /**
     * @serial The current height of the canvas.
     */
    private int height;

    private transient List<CameraListenerInterface> listeners;

    /**
     * Creates a new CameraConfiguration. All values are set to their standards:
     * <ul>
     * <li>Height: 45</li>
     * <li>Width: 80</li>
     * <li>Zoom: 100</li>
     * <li>Grid: false</li>
     * <li>Center: (0|0)</li>
     * </ul>
     */
    CameraConfiguration() {
        this.height = 45;
        this.width = 80;
        this.zoom = 100;
        this.grid = false;
        this.center = new Point(0, 0);
        this.listeners = new LinkedList<>();
    }

    /**
     * Resets this Camera to the standard values:
     * <ul>
     * <li>Height: 45</li>
     * <li>Width: 80</li>
     * <li>Zoom: 100</li>
     * <li>Grid: false</li>
     * <li>Center: (0|0)</li>
     * </ul>
     *
     */
    public void reset() {
        this.height = 45;
        this.width = 80;
        this.zoom = 100;
        this.grid = false;
        this.center = new Point(0, 0);
        this.changed();
    }

    @Override
    public CameraConfiguration clone() {
        Object otherObject = null;
        try {
            otherObject = super.clone();
        } catch (CloneNotSupportedException e) {
            assert (false);
        }
        CameraConfiguration other = (CameraConfiguration) otherObject;
        other.height = this.height;
        other.width = this.width;
        other.zoom = this.zoom;
        other.grid = this.grid;
        other.center = new Point(this.center.getX(), this.center.getY());
        other.listeners = new LinkedList<>();
        return other;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.center, this.grid, this.height, this.width, this.zoom);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CameraConfiguration)) {
            return false;
        }

        CameraConfiguration other = (CameraConfiguration) obj;
        return Objects.equals(this.center, other.center) && Objects.equals(this.grid, other.grid)
                && (this.height == other.height) && (this.width == other.width) && (this.zoom == other.zoom);
    }

    /**
     * Gets the current width of the canvas. Relative Units.
     *
     * @return an integer representing how many standard nodes would fit next to
     *         each other.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Sets the new width of the canvas. Relative Units. <br>
     * <br>
     * If the argument is not positive, it is not set. No change listener is called
     * in this case.
     *
     * @param width the new width of the canvas, indicating how many standard nodes
     *              should fit next to each other.
     */
    public void setWidth(int width) {
        if (width > 0) {
            this.width = width;
            this.changed();
        }
    }

    /**
     * Gets the current height of the canvas. Relative Units.
     *
     * @return an integer representing how many standard nodes would fit on top of
     *         each other.
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Sets the new height of the canvas. Relative Units.<br>
     * <br>
     * If the argument is not positive, it is not set. No change listener is called
     * in this case.
     *
     * @param height the new height of the canvas, indicating how many standard
     *               nodes should fit on top of each other.
     */
    public void setHeight(int height) {
        if (height > 0) {
            this.height = height;
            this.changed();
        }
    }

    /**
     * Gets the current center of the camera. Relative Units.
     *
     * @return a point with {@code -getWidth/2 < x < +getWidth/2} and
     *         {@code -getHeight/2 < y < +getHeight/2}
     */
    public Point getCenter() {
        return this.center;
    }

    /**
     * Sets the center point of the camera. Relative Units. <br>
     * <br>
     * If the point is outside of the canvas, the closest point on the canvas is set
     * instead.
     *
     * @param center a point on the canvas that needs to be focused.
     * @throws NullPointerException if no center point is given
     */
    public void setCenter(Point center) throws NullPointerException {
        if (center == null) {
            throw new NullPointerException("Center point must not be null");
        }
        this.center = new Point(center.getX(), center.getY());
        if (center.getX() > (this.width / 2.0)) {
            this.center.setLocation(this.width / 2d, this.center.getY());
        } else if (center.getX() < (-this.width / 2.0)) {
            this.center.setLocation(-this.width / 2d, this.center.getY());
        }
        if (center.getY() > (this.height / 2.0)) {
            this.center.setLocation(this.center.getX(), this.height / 2d);
        } else if (center.getY() < (-this.height / 2.0)) {
            this.center.setLocation(this.center.getX(), -this.height / 2d);
        }
        this.changed();
    }

    /**
     * Gets the current zoom factor.
     *
     * @return the zoom factor with values from 1 to {@link MAX_ZOOM}
     */
    public int getZoom() {
        return this.zoom;
    }

    /**
     * Sets the zoom factor. <br>
     * <br>
     * If the argument is not positive or larger than {@link MAX_ZOOM}, it is not
     * set.
     *
     * @param zoom an integer representing the zoom
     */
    public void setZoom(int zoom) {
        if ((0 < zoom) && (zoom <= CameraConfiguration.MAX_ZOOM)) {
            this.zoom = zoom;
            this.changed();
        }
    }

    /**
     * Checks if the grid is currently activated.
     *
     * @return a boolean indicating whether the grid is active or not.
     */
    public Boolean isGridActive() {
        return this.grid;
    }

    /**
     * Activates or deactivates the grid.
     *
     * @param grid If the grid should be active
     */
    public void setGridActive(Boolean grid) {
        if (this.grid.equals(grid)) {
            this.grid = grid;
            this.changed();
        }
    }

    /**
     * Adds a {@link CameraListenerInterface ChangeListener} that wishes to be
     * notified when the camera configuration changes.
     *
     * @param listener an object implementing the {@link CameraListenerInterface}
     *                 which wants to be informed of changes.
     * @see CameraListenerInterface
     * @see #removeListener
     * @see #changed
     */
    public void addListener(CameraListenerInterface listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a previously registered listener.
     *
     * @param listener The listener to be removed
     *
     * @see CameraListenerInterface
     * @see #addListener
     * @see #changed
     */
    public void removeListener(CameraListenerInterface listener) {
        this.listeners.remove(listener);
    }

    /**
     * Indicates that this object was changed and notifies all of the registered
     * listeners.
     *
     * @see CameraListenerInterface
     * @see #addListener
     * @see #removeListener
     */
    private void changed() {
        this.listeners.forEach(CameraListenerInterface::onCameraConfigurationChange);
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.listeners = new LinkedList<>();
    }
}
