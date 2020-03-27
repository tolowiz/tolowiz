/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.configurations;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.informatik.tolowiz.model.data.file.FileSaver;
import edu.kit.informatik.tolowiz.model.data.file.FileSaverInterface;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;

/**
 * Test for {@link Database}
 *
 * @author Tobias Klumpp
 *
 */
@SuppressWarnings({ "unused", "nls" })
class DatabaseTest {
    /**
     * the home directory
     */
    private String home = System.getProperty("user.home");
    /**
     * The test program directory
     */
    private Path dir = Paths.get(this.home).resolve(".tolowiz");
    private Database database;

    /**
     * @throws java.lang.Exception if an error occurs
     */
    @BeforeEach
    void setUp() throws Exception {
        Files.createDirectory(this.dir);
        this.database = new Database(new FileSaver(), this.dir);
    }

    /**
     * @throws java.lang.Exception if an error occurs
     */
    @AfterEach
    void tearDown() throws Exception {

        FileUtils.deleteDirectory(this.dir.toFile());
    }

    /**
     * Test method for {@link Database#Database(FileSaverInterface, Path)}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testDatabase() throws Exception {
        new Database(new FileSaver(), this.dir);
        new Database(new FileSaver(), this.dir);

        FileUtils.deleteDirectory(this.dir.toFile());
        Assertions.assertThrows(InternalDatabaseException.class, () -> new Database(new FileSaver(), this.dir));

    }

    /**
     * test method for {@link Database#createStorage(Ontology)}
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testCreateStorage() throws Exception {
        new Database(new FileSaver(), this.dir).createStorage(new Ontology("Test ID", "hi"));
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    void testGetStorageIDs() throws Exception {
        List<String> ids = this.database.getStorageIDs();
        Assertions.assertTrue(ids.isEmpty());
        this.database.createStorage(new Ontology("iri1", "nmame$1"));

        ids = this.database.getStorageIDs();
        Assertions.assertTrue(ids.size() == 1);
        FileUtils.deleteDirectory(this.dir.toFile());
        Assertions.assertThrows(InternalDatabaseException.class, () -> this.database.getStorageIDs());
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    void testGetStorageNames() throws Exception {
        List<String> ids = this.database.getStorageNames();
        Assertions.assertTrue(ids.isEmpty());
        this.database.createStorage(new Ontology("iri1", "nmame$1"));

        ids = this.database.getStorageNames();
        Assertions.assertTrue(ids.size() == 1);
        FileUtils.deleteDirectory(this.dir.toFile());
        Assertions.assertThrows(InternalDatabaseException.class, () -> this.database.getStorageNames());
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    void testGetStorageByID() throws Exception {
        this.database.createStorage(new Ontology("iri1", "nmame$1"));
        Assertions.assertNotNull(this.database.getStorageByID("iri1"));
        Assertions.assertThrows(NoSuchEntryException.class, () -> this.database.getStorageByID("missing"));

        FileUtils.deleteDirectory(this.dir.toFile());
        Assertions.assertThrows(InternalDatabaseException.class, () -> this.database.getStorageByID("iri1"));
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    void testGetStorageByName() throws Exception {
        this.database.createStorage(new Ontology("iri1", "nmame$1"));
        Assertions.assertNotNull(this.database.getStorageByName("nmame$1"));
        Assertions.assertThrows(NoSuchEntryException.class, () -> this.database.getStorageByName("missing"));
        FileUtils.deleteDirectory(this.dir.toFile());
        Assertions.assertThrows(InternalDatabaseException.class, () -> this.database.getStorageByName("nmame$1"));
    }

}
