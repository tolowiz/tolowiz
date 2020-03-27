/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import edu.kit.informatik.tolowiz.model.data.icons.IconInterface;
import edu.kit.informatik.tolowiz.model.ontology.Instance;
import edu.kit.informatik.tolowiz.model.ontology.ValueType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.javatuples.Pair;

/**
 * Represents the configuration of one Instance in the visualization of the
 * Ontology.
 *
 * @author Fabian
 * @version 1.0
 * @see edu.kit.informatik.tolowiz.model.ontology.Instance
 */
public class InstanceConfiguration
implements Serializable, Comparable<InstanceConfiguration>, Cloneable {

    private static final long serialVersionUID = 5813085925562553738L;

    /**
     * The Instance this Configuration is set up to mirror.
     *
     * @serial
     */
    private Instance instance;

    /**
     * The name of the associated Instance.
     *
     * @serial
     */
    private String name;

    /**
     * The visibility of this Instance in the current configuration. Accepts
     * boolean values which represent "visible" if true, "invisible" if false.
     *
     * @serial
     */
    private boolean visible;

    /**
     * The {@link InstanceTypeConfiguration type}s this Instance belongs to.
     * Only the direct supertypes are stored here. It holds information about
     * all Instances of this type, such as the icon.
     *
     * @serial
     */
    private SortedSet<InstanceTypeConfiguration> types;

    /**
     * The position of this Instance. Can be {@code null} to indicate the
     * standard position.
     *
     * @serial
     */
    private Point pos;

    /**
     * The groups this InstanceConfiguration is in.
     *
     * @serial
     */
    private Set<Group> groups;

    /**
     * The marks that were applied to this InstanceConfiguration
     *
     * @serial
     */
    private LinkedList<InstanceMark> marks;

    /**
     * The list of listeners that observe this object.
     *
     * @transient
     */
    private transient List<InstanceListenerInterface> listeners;
    private transient List<RelationConfiguration> relations;
    private Point defPos;

    /**
     * Generates a new InstanceConfiguration based on an instance and a
     * supertype.
     *
     * @param base The Instance to base this InstanceConfiguration on
     * @param instanceTypes The other InstanceTypeConfigurations
     */
    InstanceConfiguration(Instance base,
            Set<InstanceTypeConfiguration> instanceTypes) {
        this.instance = base;
        this.name = base.getName();

        this.types = new TreeSet<>(); // for each known type, check if it's a
        // supertype for the base instance
        instanceTypes.stream()
        .filter(itc -> base.getType().stream()
                .filter(it -> it.getIRI().equals(itc.getIRI()))
                .findFirst().orElse(null) != null)
        .forEach(itc -> { // and add it if it is
            LinkedList<InstanceTypeConfiguration> supertypes = new LinkedList<>();
            supertypes.add(itc);
            while (!supertypes.isEmpty()) {
                InstanceTypeConfiguration t = supertypes.pop();
                t.getMembers().add(this);
                supertypes.addAll(t.getSuperTypes());
            }
            this.types.add(itc);
        });
        this.visible = false;
        this.groups = new HashSet<>();
        this.marks = new LinkedList<>();
        this.relations = new LinkedList<>();
        this.listeners = new LinkedList<>();
    }

    /**
     * Clones an InstanceConfiguration. It is necessary to know all the types in
     * the type hierarchy of it.
     *
     * @param instanceTypes a set of InstanceTypeConfigurations needed to create
     * the copy
     * @return a new InstanceConfiguration which will be equal to, but not the
     * same object as this.
     */
    InstanceConfiguration cloneWithTypes(
            Set<InstanceTypeConfiguration> instanceTypes) {
        Object otherObject = null;
        try {
            otherObject = super.clone();
        } catch (CloneNotSupportedException e) {
            assert (false);
        }
        InstanceConfiguration other = (InstanceConfiguration) otherObject;
        other.instance = this.instance;
        other.name = this.name;
        other.types = new TreeSet<>(); // find the known types which are also
        // types of the this object
        instanceTypes.stream().filter(itc -> this.types.contains(itc))
        .forEach(itc -> {
            LinkedList<InstanceTypeConfiguration> supertypes = new LinkedList<>();
            supertypes.add(itc);
            while (!supertypes.isEmpty()) {
                InstanceTypeConfiguration t = supertypes.pop();
                t.getMembers().add(other);
                supertypes.addAll(t.getSuperTypes());
            }
            other.types.add(itc);
        });
        other.visible = this.visible;
        other.groups = new HashSet<>();
        other.marks = new LinkedList<>(this.marks);
        other.listeners = new LinkedList<>();
        other.relations = new LinkedList<>();
        other.defPos = (this.defPos != null ? this.defPos.clone() : null);
        other.pos = (this.pos != null ? this.pos.clone() : null);
        return other;
    }

    @Override
    public int compareTo(InstanceConfiguration o) {
        if (this.name.compareTo(o.name) != 0) {
            return this.name.compareTo(o.name);
        } else {
            return this.hashCode() - o.hashCode();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.instance, this.name, this.types);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InstanceConfiguration)) {
            return false;
        }
        InstanceConfiguration other = (InstanceConfiguration) obj;
        return Objects.equals(this.instance, other.instance)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.types, other.types);
    }

    /**
     * Gets the name of this Instance
     *
     * @return the name of this InstanceConfiguration, which is the name of the
     * underlying Instance.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets an URI for this InstanceConfiguration. It is based on the underlying
     * Instance.
     *
     * @return an unique idenfier for this Instance in this configuration.
     */
    public String getURI() {
        return this.instance.getURI();
    }

    /**
     * Gets the types of this Instance
     *
     * @return the InstanceTypeConfigurations this is a member of.
     */
    public SortedSet<InstanceTypeConfiguration> getTypes() {
        return this.types;
    }

    /**
     * Adds this Instance to a {@link Group}. An Instance can be part of
     * multiple groups at a time.
     *
     * @param g the group to add this Instance to
     * @see #removeFromGroup
     * @see #getGroups
     */
    public void addToGroup(Group g) {
        g.addInstance(this);
        this.groups.add(g);
    }

    /**
     * If this Instance is part of the group, remove it from there. If not, this
     * method does nothing.
     *
     * @param g the group to remove this Instance from
     * @see #addToGroup
     * @see #getGroups
     */
    public void removeFromGroup(Group g) {
        g.removeInstance(this);
        this.groups.remove(g);
    }

    /**
     * Checks the groups this Instance is currently part of. If there is no
     * associated {@link Group}, an empty collection is returned.
     *
     * @return a collection of groups this Instance is part of. Can be empty.
     * @see #addToGroup
     * @see #removeFromGroup
     */
    public Set<Group> getGroups() {
        return this.groups;
    }

    /**
     * Shows this Instance. For Instances in a configuration, visibility is
     * determined at Instance level, rather than InstanceType level. This is to
     * support hiding and showing single instances in addition to using types.
     *
     * @see #hide
     * @see #isVisible
     */
    public void show() {
        if (!this.visible) {
            this.visible = true;
            this.changed();
        }
    }

    /**
     * Hides this Instance. For Instances in a configuration, visibility is
     * determined at Instance level, rather than InstanceType level. This is to
     * support hiding and showing single instances in addition to using types.
     *
     * @see #show
     * @see #isVisible
     */
    public void hide() {
        if (this.visible) {
            this.visible = false;
            this.changed();
        }
    }

    /**
     * Checks the visibility of this Instance in the current configuration.
     * Instances can either be shown or hidden, for the visibility of types, see
     * {@link InstanceTypeConfiguration.TypeVisibility}
     *
     * @return {@code True} if visible, {@code False} if invisible.
     *
     *
     * @see #show
     * @see #hide
     * @see InstanceTypeConfiguration#isVisible
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Requests the {@link edu.kit.informatik.tolowiz.model.ontology.ValueType
     * values} for this Instance. The returned object will not have any
     * references to the value-classes, but will be already parsed as a set of
     * pairs (String, String).
     *
     * @return a set of name-value-pairs, containing the active values for this
     * configuration
     * @see edu.kit.informatik.tolowiz.model.ontology.ValueType
     */
    public List<Pair<String, String>> getValues() {
        List<Pair<String, String>> results = new LinkedList<>();

        results.add(new Pair<>("Types", this.types.stream()
                .map(t -> t.getName()).collect(Collectors.joining(", "))));

        SortedSet<Pair<ValueType, String>> sortedTypes = new TreeSet<>(
                (a, b) -> a.getValue0().compareTo(b.getValue0()));
        sortedTypes.addAll(this.instance.getValues());
        for (var v : sortedTypes) { // add it if any type has the value set as
            // active
            if (this.types.stream().anyMatch(
                    itc -> itc.getActiveValues().contains(v.getValue0()))) {
                results.add(new Pair<>(v.getValue0().getName(), v.getValue1()));
            }
        }

        SortedSet<RelationConfiguration> sortedRelations = new TreeSet<>(
                (a, b) -> a.getRelationType().getName()
                .compareTo(b.getRelationType().getName()));

        this.relations.stream()
        .filter(rel -> rel.getOrigin().getName().equals(this.getName()))
        .forEach(sortedRelations::add);
        for (var r : sortedRelations) {
            results.add(new Pair<>(r.getRelationType().getName(),
                    r.getDestination().getName()));
        }

        return results;
    }

    /**
     * Gets the {@link IconInterface icon} for this Instance in this
     * configuration. It is stored on the InstanceType level, but returned at
     * the Instance level as well.
     *
     * @return a reference to the icon which should be displayed for this
     * Instance in this configuration
     */
    public IconInterface getIcon() {
        InstanceTypeConfiguration any = this.types.stream().findFirst()
                .orElse(null); // TODO this just picks the first
        return (any == null) ? null : any.getIcon();
    }

    /**
     * Gets the marks for this Instance.
     *
     * @return a list of InstanceMarks, in order of appliance.
     * @see #getEffectiveMark()
     */
    public List<InstanceMark> getMarks() {
        return this.marks;
    }

    /**
     * Calculates an effective mark for this Instance. <br>
     * <br>
     * This is a convenience method, combining all applied marks in order of
     * appliance.
     *
     * @return the single mark that should be active on this Instance
     */
    public InstanceMark getEffectiveMark() {
        InstanceMark im = new InstanceMark();
        for (InstanceMark current : this.marks) {
            if (!current.getColor().isEmpty()) {
                im.setColor(current.getColor().get());
            }
            if (!current.getStroke().isEmpty()) {
                im.setStroke(current.getStroke().get());
            }
            if (!current.getShape().isEmpty()) {
                im.setShape(current.getShape().get());
            }
        }
        return im;
    }

    /**
     * Marks this Instance with the given mark.<br>
     * <br>
     * It will override all the other marks. If some values are not set in one
     * mark, they will fall back to the other ones. <br>
     * If this mark is already present, it will be pushed to the front of the
     * list.
     *
     *
     * @param style the mark to apply
     */
    public void mark(InstanceMark style) {
        this.marks.remove(style);
        this.marks.addLast(style);
        this.changed();
    }

    /**
     * Removes a certain mark from this Instance.
     *
     * @param style the mark to remove
     */
    public void unmark(InstanceMark style) {
        this.marks.remove(style);
        this.changed();
    }

    /**
     * Looks up the stored position of this instance. This is not necessarily
     * the displayed position.<br>
     * <b>It is not guaranteed that this is a non-null object!</b>
     *
     * @return a Point representing the position of this instance.
     * @see InstanceConfiguration#getPosition
     */
    public Point getStoredPosition() {
        return this.pos;
    }

    /**
     * Restores the default position for this Instance. <br>
     * <br>
     * A handler could still display a different position.
     */
    public void restoreDefaultPosition() {
        this.pos = this.defPos;
        this.changed();
    }

    /**
     * Stores the default position of this instance. It is used to later reset
     * to this position when the Graph changes. <br>
     * Keep in mind that a handler may choose to display a different position
     * with {@link #getPosition} instead.
     *
     * @param point the point to be set as default position
     */
    public void setDefaultPosition(Point point) {
        this.defPos = point.clone();
        this.pos = point;
    }

    /**
     * Gets the position of this instance in this configuration.<br>
     * <br>
     * Positions are in relative units, that means 1 Unit is the standard size
     * of a Node in the Graph Framework.<br>
     * A {@link Handler} is used to calculate positions for Instances.
     *
     * @return an Optional which could contain a point, or {@code null} if there
     * is no position.
     */
    public Optional<Point> getPosition() {
        DefaultHandler handler = null;
        for (InstanceTypeConfiguration x : this.types) {
            if (handler == null) {
                handler = x.getHandler();
            } else {
                DefaultHandler curhandler = x.getHandler();
                if (curhandler.getPriorityOverOther(handler) < 0) {
                    handler = curhandler;
                }
            }
        }
        assert (handler != null);
        return Optional.ofNullable(handler.getPosition(this));
    }

    /**
     * Sets the position of this instance in this configuration.<br>
     * <br>
     * Positions are in relative units, that means 1 Unit is the standard size
     * of a Node in the Graph Framework.
     *
     * @param p the point where to place this Instance. Accepts {@code null} as
     * argument to indicate the standard position should be used.
     */
    public void setPosition(Point p) {
        this.pos = p;
        this.changed();
    }

    /**
     * Adds a ChangeListener that is notified when properties of this Instance
     * change. <br>
     * Ideally, the listener would call this method with itself, for example
     * {@code instance.addListener(this)}, but it is also permitted to add other
     * listeners.
     *
     * @param listener an object implementing the InstanceListenerInterface
     * which wants to be notified of changes
     * @see #changed
     * @see #removeListener
     */
    public void addListener(InstanceListenerInterface listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a ChangeListener from this Relation. <br>
     *
     * @param listener the listener to be removed
     * @see #addListener
     * @see #changed
     */
    public void removeListener(InstanceListenerInterface listener) {
        this.listeners.remove(listener);
    }

    /**
     * Indicates that this object was changed.<br>
     * This could be called from this class or from
     * {@link InstanceTypeConfiguration#changed()}. All registered listeners
     * will be notified of the change via
     * {@link InstanceListenerInterface#onChange()}.
     */
    protected void changed() {
        this.listeners.forEach(InstanceListenerInterface::onChange);
        assert (this.relations != null);
        this.relations.forEach(RelationConfiguration::changed);
    }

    /**
     * Adds a relation to the list of all relations concerning this instance.
     *
     * @param relation The RelationConfiguration to add.
     */

    void addRelation(RelationConfiguration relation) {
        this.relations.add(relation);
    }

    private void readObject(ObjectInputStream in)
            throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.listeners = new LinkedList<>();
        this.relations = new LinkedList<>();
    }

}
