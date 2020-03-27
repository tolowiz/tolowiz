/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import java.io.Serializable;

/**
 * Defines a point in the graph
 *
 * @author Tobias Klumpp
 *
 */
public class Point implements Serializable, Cloneable {
    /**
     * serial version uid
     */
    private static final long serialVersionUID = -3629679119234811155L;

    private double x;

    private double y;

    /**
     * Generates a new point at the specified location
     *
     * @param x the x direction
     * @param y the y direction
     */
    public Point(double x, double y) {
        super();
        this.x = x;
        this.y = y;
    }

    /**
     * Default constructor without parameters
     */
    public Point() {
        this(0, 0);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(this.x);
        result = (prime * result) + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y);
        result = (prime * result) + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point other = (Point) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        return true;
    }

    @Override
    public Point clone() {
        Object otherObject = null;
        try {
            otherObject = super.clone();
        } catch (CloneNotSupportedException e) {
            assert (false);
        }
        Point other = (Point) otherObject;
        other.x = this.x;
        other.y = this.y;
        return other;
    }

    /**
     * Returns the y direction of the point.
     *
     * @return the y direction
     */
    public double getY() {
        return this.y;
    }

    /**
     * Sets the y direction of the point.
     *
     * @param y the y direction to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Returns the x direction of the point.
     *
     * @return the x direction
     */
    public double getX() {
        return this.x;
    }

    /**
     * Sets the x direction of the point.
     *
     * @param x the x direction to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Sets the location of the point.
     *
     * @param x the x position
     * @param y the y position
     */
    public void setLocation(double x, double y) {
        this.setX(x);
        this.setY(y);

    }

}
