/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.FileUtils;
import org.javatuples.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import edu.kit.informatik.tolowiz.controller.interpretation.RDFInterpreterFactory;
import edu.kit.informatik.tolowiz.model.data.configurations.Database;
import edu.kit.informatik.tolowiz.model.data.configurations.StorageInterface;
import edu.kit.informatik.tolowiz.model.data.file.FileSaver;
import edu.kit.informatik.tolowiz.model.data.icons.IconDatabase;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;
import edu.kit.informatik.tolowiz.view.graph.GraphControllerInterface;
import edu.kit.informatik.tolowiz.view.graph.GraphInterface;
import edu.kit.informatik.tolowiz.view.graph.ImageInterface;
import edu.kit.informatik.tolowiz.view.graph.ImageWriteException;
import edu.kit.informatik.tolowiz.view.gui.TabControllerInterface;
import edu.kit.informatik.tolowiz.view.gui.TabInterface;
import edu.kit.informatik.tolowiz.view.gui.ViewInterface;

/**
 * Test class for tab controller.
 *
 * @author Tobias Klumpp
 *
 */
@SuppressWarnings({ "nls", "unused" })
class TabControllerTest {
    private static final String ONTOLOGY = "testOntologyAnne.rdf";
    private static final String ONTOLOGY_2 = "testOntologyMedium.rdf";
    private ViewInterface viewMockup;
    private Path dir = Paths.get(System.getProperty("user.home")).resolve(".tolowiz");
    private Path export = this.dir.resolve("Export.tolo");
    private Path exportNoTolo = this.dir.resolve("Export2");
    private Path imageExport = this.dir.resolve("Export.png");
    private Path imageExportNoPNG = this.dir.resolve("Export2");
    private Path docExport = this.dir.resolve("Export.pdf");
    private Path docExportNoPDF = this.dir.resolve("Export2");
    private ApplicationController cont;
    private TabController tabCont;
    private RDFInterpreterFactory interpreter;
    private Database database;
    private IconDatabase icons;
    private FileSaver fileSaver;
    private Configuration conf;
    private Ontology ontoOnto;
    private StorageInterface storage;
    private TabInterface tab;
    private GraphInterface graph;
    private ImageInterface screenImage;
    private Path vectorExport = this.dir.resolve("Export.svg");
    private Path vectorExportNoSVG = this.dir.resolve("Export2");
    private Path onto = this.dir.resolve(TabControllerTest.ONTOLOGY);
    private Path onto2 = this.dir.resolve(TabControllerTest.ONTOLOGY_2);
    private HashSet<Pair<String, Pair<byte[], String>>> defaultIcons;
    private GraphControllerInterface graphCont;
    private ImageInterface imageMock;

    /**
     * @throws Exception if an error occurs
     */
    @BeforeEach
    void setUp() throws Exception {
        FileUtils.deleteDirectory(this.dir.toFile());
        Files.createDirectory(this.dir);
        try (InputStream in = this.getClass().getResourceAsStream(TabControllerTest.ONTOLOGY);
                OutputStream out = Files.newOutputStream(this.onto);) {
            IOUtils.copy(in, out);
        }
        try (InputStream in = this.getClass().getResourceAsStream(TabControllerTest.ONTOLOGY_2);
                OutputStream out = Files.newOutputStream(this.onto2);) {
            IOUtils.copy(in, out);
        }
        this.ontoOnto = new RDFInterpreterFactory().getInterpreter(this.onto, this.cont).buildOntology();
        this.fileSaver = new FileSaver();
        this.viewMockup = Mockito.mock(ViewInterface.class);
        this.tab = Mockito.mock(TabInterface.class);
        this.screenImage = Mockito.mock(ImageInterface.class);
        this.graph = Mockito.mock(GraphInterface.class);
        Mockito.when((this.viewMockup).createTab(ArgumentMatchers.isA(TabControllerInterface.class),
                ArgumentMatchers.isA(Configuration.class), ArgumentMatchers.isA(GraphControllerInterface.class),
                ArgumentMatchers.isA(String.class))).thenReturn(this.tab);
        Mockito.when(this.tab.getGraph()).thenReturn(this.graph);
        Mockito.when(this.graph.getImage()).thenReturn(this.screenImage);
        this.database = new Database(this.fileSaver, this.dir);
        this.defaultIcons = new HashSet<>();
        // this.defaultIcons.add(new Pair<>("pc", new Pair<>( //$NON-NLS-1$
        // this.getClass().getResourceAsStream("tolowiz_hat.png").readAllBytes(),
        // "png"))); //$NON-NLS-1$

        this.icons = new IconDatabase(this.dir, this.getClass().getResourceAsStream("tolowiz_hat.png").readAllBytes(),
                "png", this.defaultIcons);
        this.conf = new Configuration(this.ontoOnto, this.icons);
        this.cont = new ApplicationController(this.viewMockup, this.fileSaver, this.database, this.interpreter,
                this.icons);
        this.storage = this.database.createStorage(this.ontoOnto);
        this.tabCont = new TabController(this.cont, this.viewMockup, this.fileSaver, this.database, this.icons,
                this.ontoOnto, "Test Tab");
        this.imageMock = Mockito.mock(ImageInterface.class);
        Mockito.when(this.graph.getImage()).thenReturn(this.imageMock);

    }

    /**
     * @throws java.lang.Exception if an error occurs
     */
    @AfterEach
    void tearDown() throws Exception {
        FileUtils.deleteDirectory(this.dir.toFile());
    }

    /**
     * Test method for constructor
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testTabController() throws Exception {
        this.tabCont = new TabController(this.cont, this.viewMockup, this.fileSaver, this.database, this.icons,
                this.ontoOnto, "2. Tab");
        Assertions.assertNotNull(this.tabCont);
        FileUtils.deleteDirectory(this.dir.toFile());
        this.tabCont = new TabController(this.cont, this.viewMockup, this.fileSaver, this.database, this.icons,
                this.ontoOnto, "3. Tab");
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.controller.TabController#closeOntology()}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testCloseOntology() throws Exception {
        FileUtils.deleteDirectory(this.dir.toFile());

        this.tabCont.closeOntology();

    }

    /**
     * Error Test method for
     * {@link edu.kit.informatik.tolowiz.controller.TabController#closeOntology()}.
     */
    @Test
    void testCloseOntologyError() {

        this.tabCont.closeOntology();

    }

    /**
     * Test method for {@link TabController#doOperation()}.
     *
     * @throws Exception if an error occurs
     *
     */
    @Test
    void testDoOperation() throws Exception {
        this.tabCont.doOperation();
        Ontology secondOnt = new RDFInterpreterFactory().getInterpreter(this.onto2, this.cont).buildOntology();
        Configuration second = new Configuration(secondOnt, this.icons);
        this.tab.showGraph(second);
        this.tabCont.undo();
        this.tabCont.exportConfiguration(this.export, true);
        Configuration loaded = this.fileSaver.generateFile(this.export).importConfiguration();

        Assertions.assertTrue(loaded.getOntology().getIRI().equals(this.conf.getOntology().getIRI()));
        Assertions.assertTrue(!loaded.getOntology().getIRI().equals(second.getOntology().getIRI()));
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.controller.TabController#exportConfiguration(java.nio.file.Path, boolean)}.
     *
     * @throws Exception if an error occurs
     */
    @SuppressWarnings("boxing")
    @Test
    void testExportConfiguration() throws Exception {
        Mockito.when(this.tab.askForOverwrite(this.export)).thenReturn(Boolean.valueOf(true));
        this.tabCont.exportConfiguration(this.export, false);
        this.tabCont.exportConfiguration(this.export, false);
        this.tabCont.exportConfiguration(this.export, true);
        this.tabCont.exportConfiguration(this.exportNoTolo, true);
        Mockito.when(this.tab.askForOverwrite(this.export)).thenReturn(Boolean.valueOf(false));
        this.tabCont.exportConfiguration(this.export, false);
        this.tabCont.exportConfiguration(this.export, false);
        this.tabCont.exportConfiguration(this.export, true);
        FileUtils.deleteDirectory(this.dir.toFile());
        this.tabCont.exportConfiguration(this.export, true);
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.controller.TabController#exportVisualizationDocument(java.nio.file.Path)}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testExportVisualizationDocument() throws Exception {
        this.tabCont.exportVisualizationDocument(this.docExport);
        this.tabCont.exportVisualizationDocument(this.docExportNoPDF);
        FileUtils.deleteDirectory(this.dir.toFile());
        Mockito.doThrow(ImageWriteException.class).when(this.imageMock).exportPDF(ArgumentMatchers.isA(Path.class));
        this.tabCont.exportVisualizationDocument(this.docExport);
    }

    /**
     * Test method for
     * {@link TabController#exportVisualizationImage(Path, ImageFiletype)}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testExportVisualizationImage() throws Exception {
        this.tabCont.exportVisualizationImage(this.imageExport, ImageFiletype.PNG);
        this.tabCont.exportVisualizationImage(this.imageExportNoPNG, ImageFiletype.PNG);
        FileUtils.deleteDirectory(this.dir.toFile());
        Mockito.doThrow(ImageWriteException.class).when(this.imageMock).export(ArgumentMatchers.isA(Path.class),
                ArgumentMatchers.isA(ImageFiletype.class));
        this.tabCont.exportVisualizationImage(this.imageExport, ImageFiletype.PNG);
        Mockito.when(this.graph.getImage()).thenReturn(null);
        Assertions.assertThrows(AssertionError.class,
                () -> this.tabCont.exportVisualizationImage(this.imageExport, ImageFiletype.PNG));
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.controller.TabController#exportVisualizationVector(java.nio.file.Path)}.
     */
    @Test
    void testExportVisualizationVector() {
        this.tabCont.exportVisualizationVector(this.vectorExport);
        this.tabCont.exportVisualizationVector(this.vectorExportNoSVG);
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.controller.TabController#importConfiguration(java.nio.file.Path)}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testImportConfiguration() throws Exception {
        this.fileSaver.generateFile(this.export).exportConfiguration(this.conf);
        this.tabCont.importConfiguration(this.export);

        this.fileSaver.generateFile(this.export).exportConfiguration(new Configuration(this.ontoOnto, this.icons));

        this.tabCont.importConfiguration(this.export);

        try (InputStream in = this.getClass().getResourceAsStream("example.rdf");
                OutputStream out = Files.newOutputStream(this.export);) {
            IOUtils.copy(in, out);
        }
        this.tabCont.importConfiguration(this.export);
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.controller.TabController#loadAutosaveConfiguration()}.
     */
    @Test
    void testLoadAutosaveConfiguration() {
        this.tabCont.loadAutosaveConfiguration();
        this.tabCont.closeOntology();
        this.tabCont.loadAutosaveConfiguration();
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.controller.TabController#loadConfiguration(java.lang.String)}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testLoadConfiguration() throws Exception {
        this.storage.addEntry("Example Configuration", this.conf);
        this.tabCont.loadConfiguration("Example Configuration");
        this.tabCont.loadConfiguration("Missing Configuration");
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.controller.TabController#printOntology()}.
     *
     * @throws Exception if an error occurs
     *
     */
    @Test
    void testPrintOntology() throws Exception {
        this.tabCont.printOntology();
        Mockito.doThrow(ImageWriteException.class).when(this.imageMock).print();
        this.tabCont.printOntology();
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.controller.TabController#redo()}.
     *
     * @throws Exception if an error occurs
     */
    @Disabled
    @Test
    void testRedo() throws Exception {
        this.tabCont.doOperation();
        Assertions.assertTrue(!this.tabCont.getConfig().getInstances().iterator().next().isVisible());
        this.tabCont.undo();
        this.tabCont.redo();
        this.tabCont.exportConfiguration(this.export, true);

        Configuration loaded = this.fileSaver.generateFile(this.export).importConfiguration();

        Assertions.assertTrue(!loaded.getOntology().getIRI().equals(this.conf.getOntology().getIRI()));
        Assertions.assertTrue(this.tabCont.getConfig().getInstances().iterator().next().isVisible());
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.controller.TabController#saveConfiguration(java.lang.String)}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testSaveConfiguration() throws Exception {
        this.tabCont.saveConfiguration("Example Configuration");
        Assertions.assertNotNull(this.storage.getEntry("Example Configuration"));
        FileUtils.deleteDirectory(this.dir.toFile());
        this.tabCont.saveConfiguration("Example Configuration");
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.controller.TabController#undo()}.
     *
     * @throws Exception if an error occurs
     */
    @Disabled
    @Test
    void testUndo() throws Exception {
        this.tabCont.doOperation();
        Ontology secondOnt = new RDFInterpreterFactory().getInterpreter(this.onto2, this.cont).buildOntology();
        Configuration second = new Configuration(secondOnt, this.icons);
        this.tab.showGraph(second);
        this.tabCont.exportConfiguration(this.export, true);
        Configuration loaded = this.fileSaver.generateFile(this.export).importConfiguration();
        System.out.println(loaded.getOntology().getIRI());
        System.out.println(second.getOntology().getIRI());
        Assertions.assertTrue(loaded.getOntology().getIRI().equals(second.getOntology().getIRI()));
        this.tabCont.undo();
        this.tabCont.exportConfiguration(this.export, true);
        loaded = this.fileSaver.generateFile(this.export).importConfiguration();

        Assertions.assertTrue(loaded.getOntology().getIRI().equals(this.conf.getOntology().getIRI()));
        Assertions.assertTrue(!loaded.getOntology().getIRI().equals(second.getOntology().getIRI()));
    }

    /**
     *
     */
    @Test
    void testGetConfig() {
        Assertions.assertNotNull(this.tabCont.getConfig());
    }

    /**
     *
     */
    @Test
    void testIsUndoPossible() {
        Assertions.assertFalse(this.tabCont.isUndoPossible());
    }

    /**
     *
     */
    @Test
    void isRedoPossible() {
        Assertions.assertFalse(this.tabCont.isRedoPossible());
    }

    /**
     */
    @Test
    void testGetDefaultIcon() {
        Assertions.assertNotNull(this.tabCont.getDefaultIcon(this.conf.getInstanceTypes().iterator().next()));
    }

}
