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
 * Represents a style for relations.<br>
 * <br>
 * Since Relations can not be modified directly, they will be applied to a
 * {@link RelationTypeConfiguration}.<br>
 * However, Types can not be shown directly. Querying still needs to happen at
 * the {@link RelationConfiguration} level.
 *
 * @author Fabian Palitza
 * @version 1.0
 * @see RelationTypeConfiguration
 * @see RelationConfiguration#getCurrentStyle()
 */
public class RelationStyle implements Serializable {

    private static final long serialVersionUID = 8453357214620817877L;

    /**
     * The color associated with this style.
     *
     * @serial
     */
    private Color color;

    /**
     * The shape associated with this style.
     *
     * @serial
     */
    private ArrowShape shape;

    /**
     * The stroke associated with this style.
     *
     * @serial
     */
    private RelationStroke stroke;

    /**
     * Whether this style is applied from the other end
     *
     * @serial
     */
    private Boolean reversed;

    /**
     * Creates a new style to apply to relations without any parameters. It has to
     * be filled by the setter methods.
     *
     */
    public RelationStyle() {
        this.reversed = false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.color, this.reversed, this.shape, this.stroke);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RelationStyle)) {
            return false;
        }
        RelationStyle other = (RelationStyle) obj;
        return Objects.equals(this.color, other.color) && Objects.equals(this.reversed, other.reversed)
                && (this.shape == other.shape) && (this.stroke == other.stroke);
    }

    /**
     * Getter for the color of the RelationConfiguration.
     *
     * @return An Optional with the current color
     */
    public Optional<Color> getColor() {
        return Optional.ofNullable(this.color);
    }

    /**
     * Setter for the color of the RelationConfiguration.
     *
     * @param color the color to use
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Getter for the shape of the arrow at the end of the relation.
     *
     * @return an Optional with an ArrowShape representing the shape
     */
    public Optional<ArrowShape> getShape() {
        return Optional.ofNullable(this.shape);
    }

    /**
     * Setter for the shape of the arrow at the end of the relation.
     *
     * @param shape the ArrowShape to use
     */
    public void setShape(ArrowShape shape) {
        this.shape = shape;
    }

    /**
     * Getter for the stroke of the RelationConfiguration.
     *
     * @return An Optional with the stroke that is currently displayed
     */
    public Optional<RelationStroke> getStroke() {
        return Optional.ofNullable(this.stroke);
    }

    /**
     * Setter for the stroke of the RelationConfiguration.
     *
     * @param stroke the stroke that should be displayed
     */
    public void setStroke(RelationStroke stroke) {
        this.stroke = stroke;
    }

    /**
     * Queries if the direction of this Relation should be reversed.
     *
     * @return if this relation is reversed
     */
    public Boolean isReversed() {
        return this.reversed;
    }

    /**
     * Sets that this direction should be displayed reversed, with the arrowhead on
     * the other side. Defaults to {@code false}.
     *
     * @param reversed true if it should be reversed, false for regular relations.
     */
    public void reverse(Boolean reversed) {
        this.reversed = reversed;
    }

    /**
     * Represents the different shapes a relation can have.
     *
     * @author Fabian Palitza
     * @version 1.0
     * @see RelationStyle
     */
    public enum ArrowShape {

        /**
         * Indicates that no end should be displayed for this Relation.
         */
        NONE,

        /**
         * Indicates that the end of this relation should be an arrow.
         */
        ARROW,

        /**
         * Indicates that the end of this relation should be a filled triangle.
         */
        TRIANGLE,

        /**
         * Indicates that the end of this relation should be an filled diamond (two
         * triangles).
         */
        DIAMOND,

        /**
         * Indicates that the end of this relation should be a filled circle.
         */
        CIRCLE,

        /**
         * Indicates that the end of this relation should be half a circle, with the
         * opening towards the end.
         */
        HALF_CIRCLE_CONCAVE,

        /**
         * Indicates that the end of this relation should be half a circle, with the
         * opening towards the line.
         */
        HALF_CIRCLE_CONVEX;
    }

    /**
     * Represents the different kinds of strokes a relation can have.
     *
     * @author Fabian Palitza
     * @version 1.0
     * @see RelationStyle
     */
    public enum RelationStroke {

        /**
         * Indicates that no connection should be painted for this Relation.
         */
        NONE,

        /**
         * Indicates that plain strokes should be used when painting the connection of
         * this Relation.
         */
        PLAIN,
        /**
         * Indicates that dashed strokes should be used when painting the connection of
         * this Relation.
         */
        DASHES,
        /**
         * Indicates that plain strokes should be used when painting the connection of
         * this Relation.
         */
        DOTS;
    }

}
