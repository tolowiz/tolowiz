/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.controller;

import edu.kit.informatik.tolowiz.controller.interpretation.RDFInterpreterFactory;
import edu.kit.informatik.tolowiz.model.data.configurations.Database;
import edu.kit.informatik.tolowiz.model.data.file.FileSaver;
import edu.kit.informatik.tolowiz.model.data.file.FileSaverInterface;
import edu.kit.informatik.tolowiz.model.data.icons.IconDatabase;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;
import edu.kit.informatik.tolowiz.view.graph.GraphControllerInterface;
import edu.kit.informatik.tolowiz.view.graph.GraphInterface;
import edu.kit.informatik.tolowiz.view.gui.TabControllerInterface;
import edu.kit.informatik.tolowiz.view.gui.TabInterface;
import edu.kit.informatik.tolowiz.view.gui.ViewInterface;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.FileUtils;
import org.javatuples.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author Tobias Klumpp
 *
 */
@SuppressWarnings({ "nls", "unused", "unchecked" })
class ApplicationControllerTest {
    private ViewInterface viewMockup;
    private Path dir = Paths.get(System.getProperty("user.home"))
            .resolve(".tolowiz");
    private ApplicationController cont;
    private Path onto = this.dir.resolve("testOntologyAnne.rdf");
    private Path wrongConf = this.dir.resolve("conf.tolo");
    private Path newConf = this.dir.resolve("newconf.tolo");
    private Path noConf = this.dir.resolve("noconf.tolo");
    private TabInterface tabMockup;
    private HashSet<Pair<String, Pair<byte[], String>>> defaultIcons;
    private GraphInterface graph;

    /**
     * @throws Exception if an error occurs
     */
    @SuppressWarnings("rawtypes")
    @BeforeEach
    void setup() throws Exception {
        if (!Files.exists(this.dir)) {
            Files.createDirectory(this.dir);
        }
        try (InputStream in = this.getClass()
                .getResourceAsStream("testOntologyAnne.rdf");
                OutputStream out = Files.newOutputStream(this.onto);) {
            IOUtils.copy(in, out);
        }
        try (InputStream in = this.getClass().getResourceAsStream("conf.tolo");
                OutputStream out = Files.newOutputStream(this.wrongConf);) {
            IOUtils.copy(in, out);
        }

        try (InputStream in = this.getClass()
                .getResourceAsStream("newconf.tolo");
                OutputStream out = Files.newOutputStream(this.newConf);) {
            IOUtils.copy(in, out);
        }
        FileSaverInterface fileSaver = new FileSaver();
        this.viewMockup = Mockito.mock(ViewInterface.class);
        this.tabMockup = Mockito.mock(TabInterface.class);
        Mockito.when(this.viewMockup.createTab(
                ArgumentMatchers.isA(TabControllerInterface.class),
                ArgumentMatchers.isA(Configuration.class),
                ArgumentMatchers.isA(GraphControllerInterface.class),
                ArgumentMatchers.isA(String.class))).thenReturn(this.tabMockup);
        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ((Runnable) args[0]).run();
                return null;
            }
        }).when(viewMockup).runLater(Mockito.any(Runnable.class));
        this.graph = Mockito.mock(GraphInterface.class);
        Mockito.when(this.tabMockup.getGraph()).thenReturn(this.graph);

        this.defaultIcons = new HashSet<>();
        this.defaultIcons.add(new Pair<>("pc", new Pair<>( //$NON-NLS-1$
                this.getClass().getResourceAsStream("tolowiz_hat.png") //$NON-NLS-1$
                        .readAllBytes(),
                "png")));

        this.cont = new ApplicationController(this.viewMockup, fileSaver,
                new Database(fileSaver, this.dir), new RDFInterpreterFactory(),
                new IconDatabase(this.dir, this.getClass()
                        .getResourceAsStream("tolowiz_hat.png").readAllBytes(),
                        "png", this.defaultIcons));
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    void testApplicationController() throws Exception {
        FileSaverInterface fileSaver = new FileSaver();
        this.viewMockup = Mockito.mock(ViewInterface.class);
        new ApplicationController(this.viewMockup, fileSaver,
                new Database(fileSaver, this.dir), new RDFInterpreterFactory(),
                new IconDatabase(this.dir, this.getClass()
                        .getResourceAsStream("tolowiz_hat.png").readAllBytes(),
                        "png", this.defaultIcons));
    }

    /**
     * @throws java.lang.Exception if an error occurs
     */
    @AfterEach
    void tearDown() throws Exception {
        FileUtils.deleteDirectory(this.dir.toFile());
    }

    /**
     *
     */
    @Test
    void testExitProgram() {
        this.cont.openOntology(this.onto);
        this.cont.exitProgram();
    }

    /**
     *
     */
    @Test
    void testOpenOntology() {
        this.cont.openOntology(this.onto);
        this.cont.openOntology(this.newConf);
        this.cont.openOntology(this.noConf);
    }

    /**
     *
     */
    @Test
    void testImportConfiguration() {
        this.cont.importConfiguration(this.onto);
        this.cont.importConfiguration(this.wrongConf);
        this.cont.importConfiguration(this.newConf);
        this.cont.importConfiguration(this.noConf);
    }

    /**
     * @throws Exception if an error occurs
     *
     */
    @Test
    void testLoadConfiguration() throws Exception {
        this.cont.openOntology(this.onto);

        java.lang.reflect.Field tabsField = this.cont.getClass()
                .getDeclaredField("tabs");

        tabsField.setAccessible(true);

        List<TabController> tabs = (List<TabController>) tabsField
                .get(this.cont);
        TabController tabCont = tabs.iterator().next();
        tabCont.saveConfiguration("testconf");

        this.cont.loadConfiguration(tabCont.getConfig().getOntology().getIRI(),
                "testconf");
        this.cont.loadConfiguration(tabCont.getConfig().getOntology().getIRI(),
                "no@conf");
    }

    /**
     * @throws Exception if an error occurs
     *
     */
    @Test
    void testLoadAutosave() throws Exception {
        this.cont.openOntology(this.onto);

        java.lang.reflect.Field tabsField = this.cont.getClass()
                .getDeclaredField("tabs");

        tabsField.setAccessible(true);

        List<TabController> tabs = (List<TabController>) tabsField
                .get(this.cont);
        TabController tabCont = tabs.iterator().next();
        tabCont.closeOntology();

        this.cont.loadAutosave(tabCont.getConfig().getOntology().getIRI());
    }

}
