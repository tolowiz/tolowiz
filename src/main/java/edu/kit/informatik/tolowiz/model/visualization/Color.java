/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import java.io.Serializable;

/**
 * Class for representing a color. This class is independent of any graphics
 * framework.
 *
 * @author Tobias Klumpp
 *
 */
public class Color implements Serializable, Cloneable {
    /**
     * The color white.
     */
    public static final Color WHITE = new Color(255, 255, 255);

    /**
     * The color light gray.
     */
    public static final Color LIGHT_GRAY = new Color(192, 192, 192);

    /**
     * The color gray.
     */
    public static final Color GRAY = new Color(128, 128, 128);

    /**
     * The color dark gray.
     */
    public static final Color DARK_GRAY = new Color(64, 64, 64);

    /**
     * The color black.
     */
    public static final Color BLACK = new Color(0, 0, 0);

    /**
     * The color red.
     */
    public static final Color RED = new Color(255, 0, 0);

    /**
     * The color pink.
     */
    public static final Color PINK = new Color(255, 175, 175);

    /**
     * The color orange.
     */
    public static final Color ORANGE = new Color(255, 200, 0);

    /**
     * The color yellow.
     */
    public static final Color YELLOW = new Color(255, 255, 0);

    /**
     * The color green.
     */
    public static final Color GREEN = new Color(0, 255, 0);

    /**
     * The color magenta.
     */
    public static final Color MAGENTA = new Color(255, 0, 255);

    /**
     * The color cyan.
     */
    public static final Color CYAN = new Color(0, 255, 255);

    /**
     * The color blue.
     */
    public static final Color BLUE = new Color(0, 0, 255);

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -1094549460620345872L;

    /**
     * green value
     */
    private int green;
    /**
     * red value
     */
    private int red;
    /**
     * blue value
     */
    private int blue;

    /**
     * Simple Constructor using double values
     *
     * @param red   the red value
     * @param green the green value
     * @param blue  the blue value
     */
    public Color(int red, int green, int blue) {
        this.setRed(red);
        this.setGreen(green);
        this.setBlue(blue);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + this.blue;
        result = (prime * result) + this.green;
        result = (prime * result) + this.red;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Color)) {
            return false;
        }
        Color other = (Color) obj;
        if (this.blue != other.blue) {
            return false;
        }
        if (this.green != other.green) {
            return false;
        }
        if (this.red != other.red) {
            return false;
        }
        return true;
    }

    @Override
    public Color clone() {
        Object otherObject = null;
        try {
            otherObject = super.clone();
        } catch (CloneNotSupportedException e) {
            assert (false);
        }
        Color other = (Color) otherObject;
        other.red = this.red;
        other.green = this.green;
        other.blue = this.blue;
        return other;
    }

    /**
     * Returns the green value of this color
     *
     * @return the green value
     */
    public int getGreen() {
        return this.green;
    }

    /**
     * Returns the red value of this color
     *
     * @return the red value
     */
    public int getRed() {
        return this.red;
    }

    /**
     * Returns the blue value of this color
     *
     * @return the blue value
     */
    public int getBlue() {
        return this.blue;
    }

    /**
     * Sets the blue value
     *
     * @param blue the blue to set
     */
    public void setBlue(int blue) {
        this.blue = blue;
    }

    /**
     * Sets the red value
     *
     * @param red the red value to set
     */
    public void setRed(int red) {
        this.red = red;
    }

    /**
     * Sets the green value
     *
     * @param green the green value to set
     */
    public void setGreen(int green) {
        this.green = green;
    }

}
