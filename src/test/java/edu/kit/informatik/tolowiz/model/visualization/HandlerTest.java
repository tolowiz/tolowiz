/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.informatik.tolowiz.model.ontology.Instance;
import edu.kit.informatik.tolowiz.model.ontology.InstanceType;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;
import edu.kit.informatik.tolowiz.model.ontology.RelationType;

public class HandlerTest {

    private Configuration conf;

    /**
     * Generates a new OTT for each test.
     */
    @BeforeEach
    public void setUp() {
        Ontology ont = new Ontology("someIRI", "Hi Anjoa");
        InstanceType t1 = ont.addType("type 1", new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(),
                "iriT1");
        HashSet<InstanceType> types1 = new HashSet<>();
        types1.add(t1);
        InstanceType t2 = ont.addType("type 2", new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(),
                "iriT2");
        HashSet<InstanceType> types2 = new HashSet<>();
        types2.add(t2);

        Instance i1 = ont.addInstance("instance 1", "uri instance 1", types1, new HashSet<>(), new HashSet<>());
        Instance i2 = ont.addInstance("instance 2", "uri instance 2", types2, new HashSet<>(), new HashSet<>());
        Instance i3 = ont.addInstance("instance 3", "uri instance 3", types1, new HashSet<>(), new HashSet<>());

        RelationType r1 = ont.addRelationType("Relation 1", "R1URI");
        ont.addRelation("R1Instance1", r1, i1, i2);
        ont.addRelation("R1Instance2", r1, i2, i3);

        this.conf = new Configuration(ont, null);
        this.conf.getInstances().forEach(i -> {
            switch (i.getName()) {
            case "instance 1":
                i.setPosition(new Point(0, 0));
                break;
            case "instance 2":
                i.setPosition(new Point(100, 0));
                break;
            case "instance 3":
                i.setPosition(new Point(200, 0));
                break;
            default:
                break;
            }
        });
    }

    /**
     * Tests the position for a Default Handler.
     */
    @Test
    public void testDefaultPosition() {
        this.conf.getInstances().forEach(instance -> {
            Point p = new Point(-1000 * Math.random(), -1000 * Math.random());
            instance.setPosition(p);
            Assertions.assertEquals(p, instance.getPosition().get());
        });
    }

    /**
     * Tests the position for an Interface Handler.
     */
    @Test
    public void testInterfacePosition() {
        InstanceTypeConfiguration type = this.conf.getTypeByIRI("iriT2");
        Assertions.assertNotNull(type);
        InstanceConfiguration instance = type.getMembers().stream().findAny().orElseThrow();
        InterfaceHandler han = (InterfaceHandler) this.conf.getHandlers().stream()
                .filter(h -> h instanceof InterfaceHandler).findAny().orElseThrow();
        type.setHandler(han);
        Optional<Point> result = instance.getPosition();
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(new Point(0.1, 0), result.get());
        Assertions.assertEquals(new Point(0.1, 0).hashCode(), result.get().hashCode());
    }

    /**
     * Tests if the InterfaceHandler throws an Exception if used on a wrong type.
     */
    @Test
    public void testInterfaceIllegal() {
        InstanceTypeConfiguration type = this.conf.getTypeByIRI("iriT1");
        Assertions.assertNotNull(type);
        InstanceConfiguration instance = type.getMembers().stream().findAny().orElseThrow();
        InterfaceHandler han = (InterfaceHandler) this.conf.getHandlers().stream()
                .filter(h -> h instanceof InterfaceHandler).findAny().orElseThrow();
        Assertions.assertFalse(han.checkLegality(type));
        type.setHandler(han);
        Assertions.assertThrows(IllegalArgumentException.class, instance::getPosition);
    }

}
