/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import java.io.Serializable;

/**
 * @author Tobias Klumpp, Fabian Palitza
 *
 */
public class DefaultHandler implements Serializable {

    private static final long serialVersionUID = -4834969588783588817L;

    /**
     * The configuration this handler is based on. It can be used by subclasses
     * to determine a better position for certain Instances.
     *
     * @serial
     */
    protected Configuration configuration;

    /**
     * The name that was assigned to this handler.
     *
     * @serial
     */
    private final String name;

    /**
     * The priority of this handler. Accepts Integer Values, a higher value
     * means it is more likely to be used.
     *
     * @serial
     */
    private final int priority;

    /**
     * Creates a new DefaultHandler with the default priority.
     *
     * @param conf The Configuration this handler is for.
     */
    public DefaultHandler(Configuration conf) {
        this(conf, 0, "Default Display");
    }

    /**
     * Creates a new DefaultHandler with the given priority.
     *
     * @param conf The Configuration this handler is for.
     * @param prio an integer representing the priority. Higher values lead to a
     * higher priority.
     * @param name a name for this handler
     */
    protected DefaultHandler(Configuration conf, int prio, String name) {
        this.priority = prio;
        this.configuration = conf;
        this.name = name;
        conf.getHandlers().add(this);
    }

    /**
     * Calculates a position for an Instance based on the implementation of this
     * handler. <br>
     * <br>
     * This method only handles the argument checking and can not be overridden.
     * It then forwards the call to {@link #calculatePosition}, which should be
     * overridden.
     *
     * @param instance The InstanceConfiguration to calculate the position for.
     * @throws IllegalArgumentException if none of the types of the instance are
     * legal types for this handler.
     * @return A value for the position, or {@code null} to indicate to use any
     * position.
     * @see #calculatePosition(InstanceConfiguration)
     *
     */
    public final Point getPosition(InstanceConfiguration instance)
            throws IllegalArgumentException {
        if (instance.getTypes().stream().noneMatch(this::checkLegality)) {
            throw new IllegalArgumentException(
                    "It's not possible to calculate a position for the instance "
                            + instance.getURI() + ". The handler " + this.name
                            + " of type " + this.getClass().getName()
                            + " is not legal for any types of the instance. ("
                            + instance.getTypes().size() + " types checked).");
        }
        return this.calculatePosition(instance);
    }

    /**
     * Actual calculation of the position. This assumes the argument was already
     * checked.
     *
     * @param instance The instance to calculate the postion for.
     * @return a Point representing the position, or {@code null}.
     * @implNote The Default Implementation just uses the position stored in the
     * Instance. Subclasses should override this method and implement more
     * sophisticated calculations.
     */
    protected Point calculatePosition(InstanceConfiguration instance) {
        return instance.getStoredPosition();
    }

    /**
     * Gets a name to display for this handler. <br>
     * <br>
     * This could be the same for all Handlers of this class, but subclasses
     * might implement different handlers with different arguments (and names).
     *
     * @return The name of this Handler instance.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Getter for the priority of this Handler.<br>
     * <br>
     * It is used to compare this against other handlers, sorting these with a
     * higher priority first.
     *
     * @return an integer representing the priority of this Handler.
     */
    protected final int getPriority() {
        return this.priority;
    }

    /**
     * Used to determine priority order between handlers.
     * It is designed to be similar to compareTo, in that it returns a negative value,
     * zero, or a positive value if this is less than, equal, or more than the other.
     * @param other the other DefaultHandler to compare this against.
     * @return an order, that is a negative, zero, or positive integer.
     */
    public final int getPriorityOverOther(DefaultHandler other) {
        return other.getPriority() - this.getPriority();
    }

    /**
     * Checks whether it is legal to assign this handler to this type. <br>
     * <br>
     * If it is attempted to use
     * {@link #calculatePosition(InstanceConfiguration)} with an invalid type,
     * an IllegalArgumentException will be thrown. It is generally better to use
     * this method to fail fast, instead of failing later with the illegal
     * call.<br>
     * <br>
     * <b>Implementations should override this to deduce legality based on the
     * type or instances of the type.</b>
     *
     * @param instanceType the type to check
     * @return if it's allowed to use this handler on Instances of this Type.
     */
    public boolean checkLegality(InstanceTypeConfiguration instanceType) {
        return this.configuration.getInstanceTypes().contains(instanceType);
    }
}
