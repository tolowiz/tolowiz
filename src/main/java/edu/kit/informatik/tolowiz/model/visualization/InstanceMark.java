/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a mark for Instances in a configuration. It can have a
 * <b>Color</b>, <b>InstanceStroke</b>, and <b>Shape</b> associated. Since
 * Instances can have multiple marks, later marks override each other.
 *
 * @author Fabian Palitza
 * @version 1.0
 * @see InstanceConfiguration
 */
public class InstanceMark implements Serializable {

    private static final long serialVersionUID = 3189102020504570970L;

    /**
     * The color of this Mark, or {@code null}.
     *
     * @serial
     */
    private Color color;

    /**
     * The stroke of this Mark, or {@code null}.
     */
    private InstanceStroke stroke;

    /**
     * The shape of this Mark, or {@code null}.
     */
    private InstanceShape shape;

    /**
     * Creates an InstanceMark without any properties.
     */
    public InstanceMark() {

    }

    /**
     * Creates an InstanceMark with the given color.
     *
     * @param c The color of this mark
     */
    public InstanceMark(Color c) {
        this.color = c;
    }

    /**
     * Creates an InstanceMark with the given stroke.
     *
     * @param stroke The stroke of this mark
     */
    public InstanceMark(InstanceStroke stroke) {
        this.stroke = stroke;
    }

    /**
     * Creates an InstanceMark with the given shape.
     *
     * @param shape The shape of this mark
     */
    public InstanceMark(InstanceShape shape) {
        this.shape = shape;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.color, this.shape, this.stroke);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InstanceMark)) {
            return false;
        }
        InstanceMark other = (InstanceMark) obj;
        return Objects.equals(this.color, other.color) && (this.shape == other.shape) && (this.stroke == other.stroke);
    }

    /**
     * Gets the color of this mark.<br>
     * <br>
     * Keep in mind that this does not need to be the actual color used by the Graph
     * Framework, since it may be overridden by other marks or configuration values.
     *
     * @return a Color Optional that is associated with this Mark.
     */
    public Optional<Color> getColor() {
        return Optional.ofNullable(this.color);

    }

    /**
     * Sets the color of this mark.<br>
     * <br>
     * Keep in mind that this does not need to be the actual color used by the Graph
     * Framework, since it may be overridden by other marks or configuration values.
     *
     * @param c a Color object that should be associated with this Mark. Use
     *          {@code null} to indicate no color should be set.
     * @return this updated InstanceMark object to allow chaining calls like
     *         InstanceMark.setColor(...).setShape(...)
     */
    public InstanceMark setColor(Color c) {
        this.color = c;
        return this;
    }

    /**
     * Gets the currently used stroke of this mark. <br>
     * <br>
     * Keep in mind that this does not need to be the actual stroke used by the
     * Graph Framework, since it may be overridden by other marks or configuration
     * values.<br>
     * <i>May be unsupported in some {@link edu.kit.informatik.tolowiz.view.graph
     * Graph} implementations.</i>
     *
     * @return an InstanceStroke Optional that is associated with this Mark.
     */
    public Optional<InstanceStroke> getStroke() {
        return Optional.ofNullable(this.stroke);
    }

    /**
     * Sets the stroke of this mark.<br>
     * <br>
     * Keep in mind that this does not need to be the actual stroke used by the
     * Graph Framework, since it may be overridden by other marks or configuration
     * values.<br>
     * <i>May be unsupported in some {@link edu.kit.informatik.tolowiz.view.graph
     * Graph} implementations.</i>
     *
     * @param s the InstanceStroke that should be associated with this Mark. Use
     *          {@code null} to indicate no stroke should be set.
     * @return this updated InstanceMark object to allow chaining calls like
     *         InstanceMark.setStroke(...).setColor(...)
     */
    public InstanceMark setStroke(InstanceStroke s) {
        this.stroke = s;
        return this;
    }

    /**
     * Gets the shape of this mark.<br>
     * <br>
     * Keep in mind that this does not need to be the actual shape used by the Graph
     * Framework, since it may be overridden by other marks or configuration
     * values.<br>
     * <i>May be unsupported in some {@link edu.kit.informatik.tolowiz.view.graph
     * Graph} implementations.</i>
     *
     * @return an InstanceShape Optional that is associated with this Mark.
     */
    public Optional<InstanceShape> getShape() {
        return Optional.ofNullable(this.shape);
    }

    /**
     * Sets the shape of this mark.<br>
     * <br>
     * Keep in mind that this does not need to be the actual shape used by the Graph
     * Framework, since it may be overridden by other marks or configuration
     * values.<br>
     * <i>May be unsupported in some {@link edu.kit.informatik.tolowiz.view.graph
     * Graph} implementations.</i>
     *
     * @param s the InstanceShape that should be associated with this Mark. Use
     *          {@code null} to indicate no shape should be set.
     * @return this updated InstanceMark object to allow chaining calls like
     *         InstanceMark.setShape(...).setStroke(...)
     */
    public InstanceMark setShape(InstanceShape s) {
        this.shape = s;
        return this;
    }

    /**
     * Representing the possible values for strokes.<br>
     * <br>
     * If unsupported by the Graph, it can be ignored.
     *
     * @author Fabian Palitza
     * @version 1.0
     * @see InstanceMark
     */
    public enum InstanceStroke implements Serializable {

        /**
         * Indicates that no outline should be painted for this Instance.
         */
        NONE,

        /**
         * Indicates that plain strokes should be used when painting the outline of this
         * Instance.
         */
        PLAIN,

        /**
         * Indicates that dashed strokes should be used when painting the outline of
         * this Instance.
         */
        DASHES,

        /**
         * Indicates that dotted strokes should be used when painting the outline of
         * this Instance.
         */
        DOTS;
    }

    /**
     * Representing the possible values for shapes.<br>
     * <br>
     * If unsupported by the Graph, it can be ignored.
     *
     * @author Fabian Palitza
     * @version 1.0
     * @see InstanceMark
     */
    public enum InstanceShape implements Serializable {

        /**
         * Indicates that the shape of this Instance should be a circle.
         */
        CIRCLE,

        /**
         * Indicates that the shape of this Instance should be a triangle.
         */
        // TRIANGLE,

        /**
         * Indicates that the shape of this Instance should be a square.
         */
        // SQUARE,

        /**
         * Indicates that the shape of this Instance should be a rectangle.
         */
        // RECTANGLE;
        BOX, ROUNDED_BOX, DIAMOND, CROSS

    }

}
