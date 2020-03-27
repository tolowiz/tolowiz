/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Tobias Klumpp, Fabian Palitza
 *
 */
public class InterfaceHandler extends DefaultHandler {

    private static final long serialVersionUID = 2550800512078716993L;

    private static final double POSITION_OFFSET = 0.1;

    /**
     * Creates a new InterfaceHandler with the default priority (1000).
     *
     * @param conf The Configuration this is for
     */
    public InterfaceHandler(Configuration conf) {
        this(conf, 1000, "Display as Interface");
    }

    /**
     * Creates a new InterfaceHandler with the given priority.
     *
     * @param conf the Configuration this is for
     * @param prio The priority to use
     * @param name The name of this Handler
     */
    public InterfaceHandler(Configuration conf, int prio, String name) {
        super(conf, prio, name);
    }

    @Override
    public Point calculatePosition(InstanceConfiguration instance) {
        Set<RelationConfiguration> relations = this.configuration.getRelations();
        Point origin = relations.stream().filter(rel -> rel.getDestination().equals(instance))
                // finds the (single) relation with target instance
                .findFirst().orElseThrow().getOrigin().getStoredPosition();
        // and gets the origin position.

        Point destination = relations.stream().filter(rel -> rel.getOrigin().equals(instance))
                .map(rel -> rel.getDestination().getStoredPosition()).collect(PointAverage.average());

        double dx = (origin.getX() - destination.getX());
        double dy = (origin.getY() - destination.getY());
        double length = Math.sqrt((dx * dx) + (dy * dy));

        Point result = new Point();
        result.setX(origin.getX() - ((dx / length) * InterfaceHandler.POSITION_OFFSET));
        result.setY(origin.getY() - ((dy / length) * InterfaceHandler.POSITION_OFFSET));
        return result;

    }

    /**
     * This is only legal when every instance of the type has only one Relation
     * coming in.
     */
    @Override
    public boolean checkLegality(InstanceTypeConfiguration instanceType) {
        if (!super.checkLegality(instanceType)) {
            return false;
        }
        Boolean incomingRelationsMatch = instanceType.getDirectMembers().stream() // finds all Relations ending in each
                                                                                  // instance
                .map(instance -> this.configuration.getRelations().stream()
                        .filter(rel -> rel.getDestination().equals(instance)).collect(Collectors.toList()))
                .allMatch(list -> (list.size() == 1) && !list.get(0).getOrigin().getTypes().contains(instanceType));
        if (!incomingRelationsMatch) {
            return false;
        }
        // other checks?

        return true;
    }

    /**
     * I know you'll hate me for this, but I love streams :( Look how easy it is to
     * combine a list of points into their average!
     *
     * @author Fabian Palitza
     * @version 1.0
     */
    static final class PointAverage implements Collector<Point, PointAverage, Point> {
        private double sumX;
        private double sumY;
        private int count;

        private PointAverage() {
            this.sumX = 0;
            this.sumY = 0;
            this.count = 0;
        }

        /**
         * Gets a new PointAverage instance. <br>
         * <br>
         * The intended use is {@code Stream<point>.collect(PointAverage.average());}
         *
         * @return an implementation of a {@code Collector<Point, ?, Point>}.
         */
        public static PointAverage average() {
            return new PointAverage();
        }

        @Override
        public Supplier<PointAverage> supplier() {
            return PointAverage::new;
        }

        @Override
        public BiConsumer<PointAverage, Point> accumulator() {
            return ((avg, p) -> {
                avg.sumX += p.getX();
                avg.sumY += p.getY();
                avg.count++;
            });
        }

        @Override
        public BinaryOperator<PointAverage> combiner() {
            return ((avg1, avg2) -> {
                avg1.sumX += avg2.sumX;
                avg1.sumY += avg2.sumY;
                avg1.count += avg2.count;
                return avg1;
            });
        }

        @Override
        public Function<PointAverage, Point> finisher() {
            return (avg -> {
                return new Point(avg.sumX / avg.count, avg.sumY / avg.count);
            });
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of(Characteristics.CONCURRENT, Characteristics.UNORDERED); // CH_CONCURRENT_NOID
        }
    }
}
