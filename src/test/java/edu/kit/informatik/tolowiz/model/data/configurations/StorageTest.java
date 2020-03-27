/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.configurations;

import edu.kit.informatik.tolowiz.controller.ApplicationController;
import edu.kit.informatik.tolowiz.controller.interpretation.RDFInterpreterFactory;
import edu.kit.informatik.tolowiz.model.data.file.FileSaver;
import edu.kit.informatik.tolowiz.model.data.file.FileSaverInterface;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;
import edu.kit.informatik.tolowiz.view.gui.ApplicationControllerInterface;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Storage}
 *
 * @author Tobias Klumpp
 *
 */
@SuppressWarnings({ "unused" })
class StorageTest {
    /**
     * The program directory
     */
    private Path dir = Paths.get(System.getProperty("user.home"))
            .resolve(".tolowiz");
    /**
     * The ontology
     */
    private Ontology onto;
    /**
     * The storage associated with this ontology
     */
    private Storage storage;
    /**
     * the parent directory of the directory used for storing
     */
    private Path parent = this.dir.resolve("storage");
    /**
     * The configuration used
     */
    private Configuration conf;

    private ApplicationControllerInterface cont = new ApplicationController(
            null, null, null, null, null);

    /**
     * @throws Exception if an exception occurs
     */
    @BeforeEach
    void setUp() throws Exception {
        FileUtils.deleteDirectory(this.dir.toFile());
        Files.createDirectory(this.dir);
        Files.createDirectory(this.parent);
        try (InputStream in = this.getClass()
                .getResourceAsStream("testOntologyAnne.rdf");
                OutputStream out = Files.newOutputStream(
                        this.dir.resolve("testOntologyAnne.rdf"));) {
            IOUtils.copy(in, out);
        }
        this.onto = new RDFInterpreterFactory()
                .getInterpreter(this.dir.resolve("testOntologyAnne.rdf"),
                        this.cont)
                .buildOntology();
        this.conf = new Configuration(this.onto, null);
        this.storage = new Storage(new FileSaver(), this.onto, this.parent);
    }

    /**
     * @throws Exception if an exception occurs
     */
    @AfterEach
    void tearDown() throws Exception {
        FileUtils.deleteDirectory(this.dir.toFile());
    }

    /**
     * Test method for {@link Storage#addEntry(String, Configuration)}.
     *
     * @throws Exception if an exception occurs
     */
    @Test
    void testAddEntry() throws Exception {
        this.storage.addEntry("Konf$^#%&!\\", this.conf);
        Assertions.assertNotNull(this.storage.getEntry("Konf$^#%&!\\"));
        FileUtils.deleteDirectory(this.parent.toFile());
        Assertions.assertThrows(InternalDatabaseException.class,
                () -> this.storage.addEntry("Konf$^#%&!\\", this.conf));
    }

    /**
     * Test method for {@link Storage#addEntry(String, Configuration)}.
     *
     * @throws Exception if an exception occurs
     */
    @Test
    void testAddEntryUmlaut() throws Exception {
        this.storage.addEntry("ä", this.conf);
        Assertions.assertTrue(this.storage.getEntrys().contains("ä"));
    }

    /**
     * Test method for {@link Storage#addEntry(String, Configuration)}.
     *
     * @throws Exception if an exception occurs
     */
    @Test
    void testGetEntryUmlaut() throws Exception {
        this.storage.addEntry("ä", this.conf);
        Assertions.assertNotNull(this.storage.getEntry("ä"));
    }

    /**
     * Test method for {@link Storage#autosave(Configuration)}.
     *
     * @throws Exception if an exception occurs
     *
     */
    @Test
    void testAutosave() throws Exception {
        this.storage.autosave(this.conf);
        Assertions.assertNotNull(this.storage.getAutosaveEntry());
        FileUtils.deleteDirectory(this.parent.toFile());
        Assertions.assertThrows(InternalDatabaseException.class,
                () -> this.storage.autosave(this.conf));
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.model.data.configurations.Storage#deleteEntry(java.lang.String)}.
     *
     * @throws Exception if an exception occurs
     */
    @Test
    void testDeleteEntry() throws Exception {
        this.storage.addEntry("Example", this.conf);
        this.storage.deleteEntry("Example");
        Assertions.assertThrows(NoSuchEntryException.class,
                () -> this.storage.getEntry("Example"));
        Assertions.assertThrows(NoSuchEntryException.class,
                () -> this.storage.deleteEntry("Example2"));
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.model.data.configurations.Storage#getAutosaveEntry()}.
     *
     * @throws Exception if an exception occurs
     */
    @Test
    void testGetAutosaveEntry() throws Exception {
        Assertions.assertThrows(NoSuchEntryException.class,
                () -> this.storage.getAutosaveEntry());
        this.storage.autosave(this.conf);
        Assertions.assertNotNull(this.storage.getAutosaveEntry());
        Path autosave = this.parent.resolve(Paths.get("0"))
                .resolve("autosave.bin");
        Files.delete(autosave);
        Files.createFile(autosave);
        Assertions.assertThrows(InternalDatabaseException.class,
                () -> this.storage.getAutosaveEntry());
        FileUtils.deleteDirectory(this.parent.toFile());
        Assertions.assertThrows(InternalDatabaseException.class,
                () -> this.storage.getAutosaveEntry());
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.model.data.configurations.Storage#getEntry(java.lang.String)}.
     *
     * @throws Exception if an exception occurs
     */
    @Test
    void testGetEntry() throws Exception {
        this.storage.addEntry("Example", this.conf);
        Assertions.assertNotNull(this.storage.getEntry("Example"));
        Assertions.assertThrows(NoSuchEntryException.class,
                () -> this.storage.getEntry("Missing"));

        Path entry = this.parent.resolve(Paths.get("0"))
                .resolve("conf_4578616d706c65.bin");
        Files.delete(entry);
        Files.createFile(entry);
        Assertions.assertThrows(InternalDatabaseException.class,
                () -> this.storage.getEntry("Example"));
        FileUtils.deleteDirectory(this.parent.toFile());
        Assertions.assertThrows(InternalDatabaseException.class,
                () -> this.storage.getEntry("Example"));
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.model.data.configurations.Storage#getEntrys()}.
     *
     * @throws Exception if an exception occurs
     */
    @Test
    void testGetEntrys() throws Exception {
        this.storage.addEntry("Example", this.conf);
        List<String> list = this.storage.getEntrys();
        Assertions.assertEquals(list.size(), 1);
        Assertions.assertTrue(list.iterator().next().equals("Example"));
        FileUtils.deleteDirectory(this.parent.toFile());
        Assertions.assertThrows(InternalDatabaseException.class, () -> {
            this.storage.getEntrys();
        });
    }

    /**
     * @throws Exception if an error occurs
     *
     */
    @Test
    static void testFileEncode() throws Exception {
        String expEnc = "746573744f6e746f6c6f6779416e6e65";
        String expDec = "testOntologyAnne";
        String encoded = Storage.fileEncode(expDec);
        String decoded = Storage.fileDecode(expEnc);
        String redec = Storage.fileEncode(encoded);
        Assertions.assertEquals(expEnc, encoded);
        Assertions.assertEquals(expDec, decoded);
        // assertEquals(expDec, redec);
        String expDec2 = "";
        String encoded2 = Storage.fileEncode(expDec2);
        String decoded2 = Storage.fileDecode(encoded2);
        Assertions.assertEquals(expDec2, decoded2);
        String expDec3 = "testOntol";
        String encoded3 = Storage.fileEncode(expDec3);
        String decoded3 = Storage.fileDecode(encoded3);
        Assertions.assertEquals(expDec3, decoded3);
        String expDec4 = "testOntolo";
        String encoded4 = Storage.fileEncode(expDec4);
        String decoded4 = Storage.fileDecode(encoded4);
        Assertions.assertEquals(expDec4, decoded4);

    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.model.data.configurations.Storage#renameEntry(String, String)}.
     *
     * @throws Exception if an exception occurs
     */
    @Test
    void testRenameEntry() throws Exception {
        this.storage.addEntry("Example", this.conf);
        this.storage.renameEntry("Example", "Example New^");
        Assertions.assertThrows(NoSuchEntryException.class,
                () -> this.storage.renameEntry("ExampleNo", "Example New^"));
        Assertions.assertThrows(NoSuchEntryException.class,
                () -> this.storage.getEntry("Example"));
        Assertions.assertNotNull(this.storage.getEntry("Example New^"));
        Assertions.assertThrows(NoSuchEntryException.class,
                () -> this.storage.deleteEntry("Example2"));
    }

    /**
     * Test method for
     * {@link Storage#Storage(FileSaverInterface, Ontology, Path)}.
     *
     * @throws Exception if an exception occurs
     */
    @Test
    void testStorage() throws Exception {
        new Storage(new FileSaver(), this.onto, this.parent);
        FileUtils.deleteDirectory(this.parent.toFile());
        Assertions.assertThrows(InternalDatabaseException.class, () -> {
            Storage toTest = new Storage(new FileSaver(), this.onto,
                    this.parent);
        });
    }

    /**
     * Tests the name of the ontology
     * @throws Exception if something goes wrong
     */
    @Test
    void testGetOntologyName() throws Exception {
        Storage strg = new Storage(new FileSaver(), this.onto, this.parent);
        Assertions
        .assertTrue(strg.getOntologyName().equals(this.onto.getName()));
    }

}
