/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import edu.kit.informatik.tolowiz.model.data.icons.IconInterface;
import edu.kit.informatik.tolowiz.model.ontology.InstanceType;
import edu.kit.informatik.tolowiz.model.ontology.ValueType;

import java.io.Serializable;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Represents the current configuration for a type in the ontology. It can be
 * used to retrieve additional information, such as the associated icon or the
 * current state.
 *
 * @author Fabian
 * @version 1.0
 * @see edu.kit.informatik.tolowiz.model.ontology.InstanceType
 */
public class InstanceTypeConfiguration implements Serializable,
        Comparable<InstanceTypeConfiguration>, Cloneable {

    private static final long serialVersionUID = 500253127295513988L;

    /**
     * The backing InstanceType.
     *
     * @serial
     */
    private InstanceType instanceType;

    /**
     * The supertype this InstanceType is a subtype of or {@code null} if there
     * is no supertype.
     *
     * @serial
     */
    private SortedSet<InstanceTypeConfiguration> supertypes;

    /**
     * A set of subtypes of this InstanceType. Can be empty.
     *
     * @serial
     */
    private SortedSet<InstanceTypeConfiguration> subtypes;

    /**
     * A set of members. It includes all Instances of this type as well as all
     * of it's subtypes.
     *
     * @serial
     */
    private SortedSet<InstanceConfiguration> members;

    /**
     * The types of Values that are currently active. Subset of all types.
     *
     * @serial
     */
    private TreeSet<ValueType> activeTypes;

    /**
     * The types of Values that are possible.
     *
     * @serial
     */
    private TreeSet<ValueType> allTypes;

    /**
     * The IRI this InstanceType has. It can be used to reconnect the
     * InstanceType with its configuration.
     *
     * @serial
     */
    private String iri;

    /**
     * The name of this InstanceTypeConfiguration, i.e. the name of the
     * InstanceType.
     *
     * @serial
     */
    private String name;

    /**
     * The icon this type displays.
     *
     * @serial
     */
    private IconInterface icon;

    /**
     * A DefaultHandler or subtype representing the handler to use
     *
     * @serial
     */
    private DefaultHandler handler;

    /**
     * Constructs a new InstanceTypeConfiguration for a type. It will have
     * standard values.
     *
     * @param base the InstanceType this is based on.
     */
    InstanceTypeConfiguration(InstanceType base) {
        this.instanceType = base;
        this.name = base.getName();
        this.iri = base.getIRI();
        this.activeTypes = new TreeSet<>(); // no types are active
        this.allTypes = new TreeSet<>(this.instanceType.getValues());
        this.icon = null;
        this.handler = null;
        this.supertypes = new TreeSet<>();
        this.members = new TreeSet<>();
        this.subtypes = new TreeSet<>();
    }

    /**
     * Clones an InstanceTypeConfiguration by creating deep copies of the
     * defining properties. The listeners and transient properties are
     * initialized empty.
     *
     * @return A copy of this object. It will be equal to, but not the same
     * object.
     */
    @Override
    public InstanceTypeConfiguration clone() {
        Object otherObject = null;
        try {
            otherObject = super.clone();
        } catch (CloneNotSupportedException e) {
            assert (false);
        }
        InstanceTypeConfiguration other = (InstanceTypeConfiguration) otherObject;
        other.instanceType = this.instanceType;
        other.name = this.name;
        other.iri = this.iri;

        other.allTypes = new TreeSet<>(this.allTypes);
        other.activeTypes = new TreeSet<>(this.activeTypes);

        other.icon = this.icon;
        other.handler = this.handler;

        other.supertypes = new TreeSet<>();
        // construct member set, fill it later
        other.members = new TreeSet<>();

        // construct subtypes and add them
        other.subtypes = new TreeSet<>();
        return other;
    }

    @Override
    public int hashCode() {
        return this.iri.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InstanceTypeConfiguration)) {
            return false;
        }
        InstanceTypeConfiguration other = (InstanceTypeConfiguration) obj;
        return this.iri.equals(other.iri);
    }

    /**
     * Compares this to another InstanceTypeConfiguration. This only uses
     * lexicographic ordering, so it has a natural ordering that is
     * <b>inconsistent with equals</b>.
     */
    @Override
    public int compareTo(InstanceTypeConfiguration o) {
        return this.name.compareTo(o.name);
    }

    /**
     * Used to create the type hierarchy. It can not be set using the
     * constructors, since we need a full set of all types to actually
     * initialize the hierarchy.
     *
     * @param otherTypes A set of all InstanceTypeConfigurations
     */
    void initHierarchy(Set<InstanceTypeConfiguration> otherTypes) {
        this.instanceType.getSuperTypes().stream() // transforms the supertypes
                // set into the instanceTypes
                .map(it -> otherTypes.stream()
                        .filter(itc -> itc.getIRI().equals(it.getIRI()))
                        .findFirst().orElse(null))
                .filter(a -> a != null).forEach(a -> { // and adds them if they
                    // are not null
                    this.supertypes.add(a);
                    a.subtypes.add(this);
                });
    }

    /**
     * Gets the subtypes of this type.
     *
     * @return a collection of InstanceTypeConfigurations that includes all the
     * subtypes of this Type.
     */
    public SortedSet<InstanceTypeConfiguration> getSubTypes() {
        return this.subtypes;
    }

    /**
     * Gets the members of this type. Includes members of all subtypes.
     *
     * @return a collection of InstanceConfigurations of this type or it's
     * subtypes.
     * @see #getDirectMembers()
     */
    public SortedSet<InstanceConfiguration> getMembers() {
        return this.members;
    }

    /**
     * Gets the direct members of this type. It is possible that there are no
     * direct members, only subtypes and members of subtypes. In that case, use
     * {@link #getMembers()}.
     *
     * @return a collection of InstanceConfiguration of this type.
     * @see #getMembers()
     */
    public SortedSet<InstanceConfiguration> getDirectMembers() {
        return this.members.stream().filter(a -> a.getTypes().contains(this))
                .collect(Collectors
                        .toCollection(TreeSet<InstanceConfiguration>::new));
    }

    /**
     * @return The visiblity of this type. A type is considered visible if all
     * members are visible, partially visible if some members are visible, and
     * invisible is no members are visible.
     */
    public TypeVisibility isVisible() {
        int visibleMembers = (int) this.members.stream()
                .filter(InstanceConfiguration::isVisible).count();

        if (visibleMembers == this.members.size()) {
            return TypeVisibility.YES;
        } else if (visibleMembers == 0) {
            return TypeVisibility.NO;
        } else {
            return TypeVisibility.PARTIAL;
        }
    }

    /**
     * Returns the IRI of this type.
     *
     * @return the IRI of this InstanceTypeConfiguration, used to identify both
     * the InstanceType and the correspondig Configuration.
     */
    public String getIRI() {
        return this.iri;
    }

    /**
     * Returns the name of this type.
     *
     * @return the name of this InstanceType (Configuration).
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for the position handler of this InstanceType. <br>
     * <br>
     * It can be used to derive a new position for Instances of this type.
     * However, this method should rarely be called from the outside. If you
     * only want the position, use {@link #getPosition()}.<br>
     * Since Instances can have more than one type. it is not guaranteed that
     * all members of this type will use the associated handler.
     *
     * @return a DefaultHandler that is used to handle positions for Instances
     * of this type.
     * @see #InstanceTypeConfiguration#getPosition()
     */
    public DefaultHandler getHandler() {
        return this.handler;
    }

    /**
     * Setter for the position handler of this InstanceType.
     *
     * @param handler The handler to set.
     */
    public void setHandler(DefaultHandler handler) {
        this.handler = handler;
        this.changed();
    }

    /**
     * Gets the supertypes of this configuration.
     *
     * @return a set of InstanceTypeConfigurations that includes all supertypes
     * of this type, or no types if there are no supertypes.
     */
    public SortedSet<InstanceTypeConfiguration> getSuperTypes() {
        return this.supertypes;
    }

    /**
     * Gets the underlying InstanceType of this Configuration.
     *
     * @return The InstanceType that was used to create this
     * InstanceTypeConfiguration
     */
    public InstanceType getInstanceType() {
        return this.instanceType;
    }

    /**
     * Shows this type of Instances. This overwrites the visibility of all
     * Instances of this type and its subtypes. <br>
     * This also calls the {@link InstanceListenerInterface ChangeListeners} of
     * all members that were not already visible.
     *
     * @see #isVisible()
     * @see #hide()
     * @see InstanceConfiguration#isVisible()
     */
    public void show() {
        this.members.forEach(InstanceConfiguration::show);
    }

    /**
     * Hides this type of Instances. This overwrites the visibility of all
     * Instances of this type and its subtypes.<br>
     * This also calls the {@link InstanceListenerInterface ChangeListeners} of
     * all members that were not already invisible.
     *
     * @see #isVisible()
     * @see #show()
     * @see InstanceConfiguration#isVisible()
     */
    public void hide() {
        this.members.forEach(InstanceConfiguration::hide);
    }

    /**
     * Activates a certain type of Values to show on Instances of this type.
     * <br>
     * <br>
     *
     * @param type The ValueType to activate
     */
    public void activateValue(ValueType type) {
        if (this.instanceType.getValues().contains(type)) {
            this.activeTypes.add(type);
        }
        this.changed();
    }

    /**
     * Deactivates a certain type of Values to no longer show on Instances of
     * this type. <br>
     * <br>
     *
     * @param type The ValueType to deactivate
     */
    public void deactivateValue(ValueType type) {
        this.activeTypes.remove(type);
        this.changed();
    }

    /**
     * Queries which values are currently active for this type in this
     * configuration.
     *
     * @return a set of {@link ValueType} which includes all selected values.
     */
    public SortedSet<ValueType> getActiveValues() {
        return this.activeTypes;
    }

    /**
     * Queries which values are possible for the current type.
     *
     * @return a set of {@link ValueType} which includes all possible values.
     */
    public SortedSet<ValueType> getAllValues() {
        return this.allTypes;
    }

    /**
     * Sets a new icon for Instances of this Type.
     *
     * @param icon the icon to be used.
     */
    public void setIcon(IconInterface icon) {
        this.icon = icon;
        this.changed();
    }

    /**
     * Gets the currently active icon for Instances of this Type.
     *
     * @return an {@link IconInterface} representing the active icon.
     */
    public IconInterface getIcon() {
        return this.icon;
    }

    /**
     * Indicates that this object was changed. <br>
     * <br>
     * This means that all members could potentially have been changed, and
     * their {@link InstanceListenerInterface ChangeListeners} need to be
     * notified.
     *
     * @see InstanceConfiguration#changed()
     */
    private void changed() {
        this.members.forEach(InstanceConfiguration::changed);
    }

    /**
     * Represents the possible values for the visibility of an
     * {@link InstanceTypeConfiguration}. It has the values <b>YES</b>,
     * <b>NO</b> or <b>PARTIAL</b>.
     *
     * <ul>
     * <li>YES: The type is considered visible if all Instances are
     * visible.</li>
     * <li>NO: The type is considered invisible if all Instances are
     * invisible.</li>
     * <li>PARTIAL: The type is considered partially visible if the Instances
     * don't share the same visibility anymore.</li>
     * </ul>
     *
     * @author Anja, Fabian
     * @version 1.0
     * @see InstanceTypeConfiguration
     */
    public enum TypeVisibility {
        /**
         * YES: The type is considered visible if all Instances are visible.
         */
        YES,

        /**
         * NO: The type is considered invisible if all Instances are invisible.
         */
        NO,

        /**
         * PARTIAL: The type is considered partially visible if the Instances
         * don't share the same visibility anymore.
         */
        PARTIAL,
        /**
         * INDEFINITE: It is not possible to say if the type is visible or not
         * as the type has no objects which can save the state.
         */
        INDEFINITE;
    }

    /**
     * Returns the overall visibility of this type
     *
     * @return the visibility
     */
    public TypeVisibility getAllValuesShown() {
        boolean yes = true;
        boolean no = true;
        for (var x : this.getAllValues()) {
            if (this.activeTypes.contains(x)) {
                no = false;
            } else {
                yes = false;
            }
        }

        if (yes && !no) {
            return TypeVisibility.YES;
        } else if (no && !yes) {
            return TypeVisibility.NO;
        } else if (!no && !yes) {
            return TypeVisibility.PARTIAL;
        } else {
            return TypeVisibility.INDEFINITE;
        }
    }
}
