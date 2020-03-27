/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.ontology;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.informatik.tolowiz.controller.ApplicationController;
import edu.kit.informatik.tolowiz.controller.interpretation.InterpreterInterface;
import edu.kit.informatik.tolowiz.controller.interpretation.OntologyFileException;
import edu.kit.informatik.tolowiz.controller.interpretation.RDFInterpreterFactory;

public class OntologyTest {
    private static final String ONTOLOGY = "testOntologyMedium.rdf";
    private Path dir = Paths.get(System.getProperty("user.home")).resolve(".tolowiz");
    private Path onto = this.dir.resolve(OntologyTest.ONTOLOGY);
    private ApplicationController cont = new ApplicationController(null, null, null, null, null);

    /**
     * Setup methods
     *
     * @throws Exception if a file system problem occurs
     */
    @BeforeEach
    void setup() throws Exception {
        if (Files.exists(this.dir)) {
            FileUtils.deleteDirectory(this.dir.toFile());

        }
        Files.createDirectory(this.dir);
        try (InputStream in1 = this.getClass().getResourceAsStream(OntologyTest.ONTOLOGY);
                OutputStream out1 = Files.newOutputStream(this.onto);) {
            IOUtils.copy(in1, out1);
        }
    }

    /**
     * tests if value types are added correctly
     */
    @Test
    void testValueTypes() {
        Ontology onto = new Ontology("uri.test.Ontology", "onto");
        onto.addValueType("val1", "uri.test.Ontology#val1");
        Set<ValueType> vals = new HashSet<>();
        vals.add(new ValueType("val1", "uri.test.Ontology#val1"));
        Assertions.assertTrue(vals.equals(onto.getValueTypes()));
    }

    /**
     * tests equals emthod
     */
    @Test
    void testEquals() {
        Ontology onto = null;
        try {
            InterpreterInterface inti = new RDFInterpreterFactory().getInterpreter(this.onto, this.cont);
            onto = inti.buildOntology();
        } catch (FileNotFoundException | OntologyFileException e) {
            e.printStackTrace();
        }
        Assertions.assertTrue(!onto.equals(null));
        Assertions.assertTrue(onto.equals(onto));

    }

    /**
     * tests hashCode method
     */
    @Test
    void testHashCode() {
        Ontology onto = new Ontology(null, "hi");
        Assertions.assertTrue(onto.hashCode() == 0);
    }

    /**
     * cleans up after tests
     *
     * @throws Exception in case of file system problems
     */
    @AfterEach
    void tearDown() throws Exception {
        FileUtils.deleteDirectory(this.dir.toFile());
    }
}
