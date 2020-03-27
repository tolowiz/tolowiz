/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.file;

import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.informatik.tolowiz.controller.ApplicationController;
import edu.kit.informatik.tolowiz.controller.interpretation.RDFInterpreterFactory;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;

/**
 * Test for {@link ConfigurationFile}
 *
 * @author Tobias Klumpp
 *
 */
@SuppressWarnings({ "nls", "unused" })
class ConfigurationFileTest {
    private static final String ONTOLOGY = "testOntologyAnne.rdf";
    /**
     * The program directory
     */
    private Path dir = Paths.get(System.getProperty("user.home")).resolve(".tolowiz");
    /**
     * The location where the file will be stored.
     */
    private Path loc = this.dir.resolve("test_export.tolo");

    /**
     * the configuration file used
     */
    private ConfigurationFile configFile;
    /**
     * The configuration to be used
     */
    private Configuration conf;
    private Path onto = this.dir.resolve(ConfigurationFileTest.ONTOLOGY);

    private ApplicationController cont = new ApplicationController(null, null, null, null, null);

    /**
     * @throws java.lang.Exception if an error occurs
     */
    @BeforeEach
    void setUp() throws Exception {
        Files.createDirectory(this.dir);
        try (InputStream in = this.getClass().getResourceAsStream(ConfigurationFileTest.ONTOLOGY);
                OutputStream out = Files.newOutputStream(this.onto);) {
            IOUtils.copy(in, out);
        }
        this.configFile = new ConfigurationFile(this.loc);
        this.conf = new Configuration(new RDFInterpreterFactory().getInterpreter(this.onto, this.cont).buildOntology(),
                null);
    }

    /**
     * @throws java.lang.Exception if an error occurs
     */
    @AfterEach
    void tearDown() throws Exception {
        FileUtils.deleteDirectory(this.dir.toFile());
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.model.data.file.ConfigurationFile#ConfigurationFile(java.nio.file.Path)}.
     */
    @Test
    void testConfigurationFile() {
        new ConfigurationFile(this.loc.resolve("example.tolo"));
    }

    /**
     * Test method for {@link ConfigurationFile#exportConfiguration(Configuration)}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testExportConfiguration() throws Exception {
        this.configFile.exportConfiguration(this.conf);
        Assertions.assertNotNull(this.configFile.importConfiguration());
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.model.data.file.ConfigurationFile#importConfiguration()}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testImportConfiguration() throws Exception {
        this.testExportConfiguration();
        Files.deleteIfExists(this.loc);
        Assertions.assertThrows(NoSuchFileException.class, () -> this.configFile.importConfiguration());

        try (InputStream in = this.getClass().getResourceAsStream("bad_serial.bin");
                OutputStream out = Files.newOutputStream(this.loc);) {
            IOUtils.copy(in, out);
        }
        Assertions.assertThrows(FileTypeException.class, () -> this.configFile.importConfiguration());
        try (ObjectOutputStream oout = new ObjectOutputStream(Files.newOutputStream(this.loc))) {
            oout.writeObject(new RDFInterpreterFactory().getInterpreter(this.onto, this.cont).buildOntology());
        }
        Assertions.assertThrows(FileTypeException.class, () -> this.configFile.importConfiguration());

    }

}
