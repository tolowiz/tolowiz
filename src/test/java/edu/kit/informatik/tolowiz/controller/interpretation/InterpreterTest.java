/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.controller.interpretation;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.codehaus.plexus.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.informatik.tolowiz.controller.ApplicationController;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;

public class InterpreterTest {
    private static final String ONTOLOGY = "testOntologyMedium.rdf";
    private static final String BROKEN_URI_ONTOLOGY = "testOntologyBrokenUri.rdf";
    private static final String NO_INSTANCE_ONTOLOGY = "testOntologyNoInstance.owl";
    private ApplicationController cont = new ApplicationController(null, null, null, null, null);
    private Path dir = Paths.get(System.getProperty("user.home")).resolve(".tolowiz");
    private Path onto = this.dir.resolve(InterpreterTest.ONTOLOGY);
    private Path brokenUriOnto = this.dir.resolve(InterpreterTest.BROKEN_URI_ONTOLOGY);
    private Path noInstanceOnto = this.dir.resolve(InterpreterTest.NO_INSTANCE_ONTOLOGY);

    /**
     * Guarantees that all Tests have the same prerequisite
     * 
     * @throws Exception if file system problems occured
     */
    @BeforeEach
    void setup() throws Exception {
        if (Files.exists(this.dir)) {
            FileUtils.deleteDirectory(this.dir.toFile());

        }
        Files.createDirectory(this.dir);
        try (InputStream in1 = this.getClass().getResourceAsStream(InterpreterTest.ONTOLOGY);
                OutputStream out1 = Files.newOutputStream(this.onto);) {
            IOUtils.copy(in1, out1);
        }
        try (InputStream in2 = this.getClass().getResourceAsStream(InterpreterTest.BROKEN_URI_ONTOLOGY);
                OutputStream out2 = Files.newOutputStream(this.brokenUriOnto);) {
            IOUtils.copy(in2, out2);
        }
        try (InputStream in3 = this.getClass().getResourceAsStream(InterpreterTest.NO_INSTANCE_ONTOLOGY);
                OutputStream out3 = Files.newOutputStream(this.noInstanceOnto);) {
            IOUtils.copy(in3, out3);
        }
    }

    /**
     * tests if it is possible to initialize an OntModel Object
     */
    @Test
    void testMakeModel() {
        OntModel ontol = ModelFactory.createOntologyModel();
    }

    /**
     * tests if it is possible to generate an OntModel from an input Stream.
     * 
     * @throws FileNotFoundException if there are problems with the input file itself
     */
    @Test
    void testReadRDFFile() throws FileNotFoundException {
        OntModel ontol = ModelFactory.createOntologyModel();
        InputStream stream = this.getClass().getResourceAsStream(InterpreterTest.ONTOLOGY);
        ontol.read(stream, null);
    }

    /**
     * tests if an ontology file without any instances is interpreted correctly
     */
    @Test
    void testNoInstance() {
        Interpreter inti = new Interpreter(this.noInstanceOnto, this.cont);
        try {
            inti.buildOntology();
        } catch (OntologyFileException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * tests if an interpreter is able to build an ontology from a filepath
     */
    @Test
    void testReadOntology() {
        try {
            Interpreter inti = new Interpreter(this.onto, this.cont);
            inti.buildOntology();
        } catch (OntologyFileException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * tests whether the interpretation of an ontology file is always an equal ontology object
     */
    @Test
    void testOntologyDeterminism() {
        Ontology onto1 = null;
        Ontology onto2 = null;
        try {
            Interpreter inti1 = new Interpreter(this.onto, this.cont);
            onto1 = inti1.buildOntology();

            Interpreter inti2 = new Interpreter(this.onto, this.cont);
            onto2 = inti2.buildOntology();
        } catch (OntologyFileException | FileNotFoundException e) {
            e.printStackTrace();
        }
        Assertions.assertTrue(onto1.equals(onto2) && (onto1.hashCode() == onto2.hashCode()));
    }

    /**
     * cleans up after tests
     * 
     * @throws Exception if file system problems occured
     */
    @AfterEach
    void tearDown() throws Exception {
        FileUtils.deleteDirectory(this.dir.toFile());
    }

}
