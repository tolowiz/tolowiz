/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.ontology;

import java.util.HashSet;
import java.util.Set;

import org.javatuples.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InstanceTest {
    private RelationType rt;
    private Set<InstanceType> it;
    private ValueType vt;
    private Instance i1;

    /**
     * Setup methods
     */
    @BeforeEach
    public void buildSurroundings() {
        this.vt = new ValueType("value", "uri");
        this.rt = new RelationType("related", "uri");
        Set<ValueType> vtSet = new HashSet<>();
        vtSet.add(this.vt);
        this.it = new HashSet<>();
        this.it.add(new InstanceType("instanceA", vtSet, null, null, null, "some.instance.yay"));
        this.i1 = new Instance("i1", "uri", this.it, null, null);
    }

    
    /**
     * Tests the constructor
     */
    @Test
    public void testSimpleContructor() {
        Instance i = new Instance("i", "uri", this.it, null, null);
        Assertions.assertTrue(i.getName().equals("i"));
        Assertions.assertTrue(i.getType().equals(this.it));
    }

    /**
     * tests if adding a value works correctly
     */
    @Test
    public void testAddValue() {
        Instance i = new Instance("i", "uri", this.it, null, null);
        Pair<ValueType, String> valueEntry = new Pair<>(this.vt, "droelf");
        i.addValue(this.vt, "droelf");
        Assertions.assertTrue(i.getValues().size() == 1);
        Assertions.assertTrue(i.getValues().contains(valueEntry));
    }

    /**
     * tests if adding a relation works correctly
     */
    @Test
    public void testAddRelation() {
        Instance i = new Instance("i", "uri", this.it, null, null);
        Relation r = new Relation("uri", this.rt, i, this.i1);
        i.addRelation(r);
        Assertions.assertTrue(i.getRelations().contains(r));
        Assertions.assertTrue(i.getRelations().size() == 1);
    }

}
