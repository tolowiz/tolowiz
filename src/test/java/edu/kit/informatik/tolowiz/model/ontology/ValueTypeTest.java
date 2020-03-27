/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.ontology;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValueTypeTest {

    /**
     * tests valueType constructor
     */
    @Test
    public void testCreateValueType() {
        ValueType vt = new ValueType("A", "uri");
        Assertions.assertEquals("A", vt.getName());
    }

}
