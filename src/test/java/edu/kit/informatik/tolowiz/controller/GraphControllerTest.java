/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.controller;

import java.io.IOException;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import edu.kit.informatik.tolowiz.controller.interpretation.RDFInterpreterFactory;
import edu.kit.informatik.tolowiz.model.data.configurations.Database;
import edu.kit.informatik.tolowiz.model.data.file.FileSaver;
import edu.kit.informatik.tolowiz.model.data.icons.IconDatabase;
import edu.kit.informatik.tolowiz.model.data.icons.IconInterface;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;
import edu.kit.informatik.tolowiz.model.ontology.ValueType;
import edu.kit.informatik.tolowiz.model.visualization.Color;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;
import edu.kit.informatik.tolowiz.model.visualization.DefaultHandler;
import edu.kit.informatik.tolowiz.model.visualization.Group;
import edu.kit.informatik.tolowiz.model.visualization.InstanceConfiguration;
import edu.kit.informatik.tolowiz.model.visualization.InstanceMark;
import edu.kit.informatik.tolowiz.model.visualization.InstanceMark.InstanceShape;
import edu.kit.informatik.tolowiz.model.visualization.InstanceMark.InstanceStroke;
import edu.kit.informatik.tolowiz.model.visualization.InstanceTypeConfiguration;
import edu.kit.informatik.tolowiz.model.visualization.InstanceTypeConfiguration.TypeVisibility;
import edu.kit.informatik.tolowiz.model.visualization.InterfaceHandler;
import edu.kit.informatik.tolowiz.model.visualization.RelationStyle;
import edu.kit.informatik.tolowiz.model.visualization.RelationStyle.ArrowShape;
import edu.kit.informatik.tolowiz.model.visualization.RelationTypeConfiguration;
import edu.kit.informatik.tolowiz.view.graph.GraphControllerInterface;
import edu.kit.informatik.tolowiz.view.graph.GraphInterface;
import edu.kit.informatik.tolowiz.view.gui.TabControllerInterface;
import edu.kit.informatik.tolowiz.view.gui.TabInterface;
import edu.kit.informatik.tolowiz.view.gui.ViewInterface;

public class GraphControllerTest {
    private ViewInterface viewMockup;
    private Path dir = Paths.get(System.getProperty("user.home")).resolve(".tolowiz");
    private ApplicationController cont;
    private RDFInterpreterFactory interpreter;
    private Database database;
    private IconDatabase icons;
    private FileSaver fileSaver;
    private TabController tabCont;
    private Ontology ontoOnto;
    private TabInterface tab;
    private GraphInterface graph;
    private Path onto = this.dir.resolve("testOntologyAnne.rdf");
    private Configuration config;
    private GraphController objectToTest;

    /**
     * Generates a new OTT for each test.
     *
     * @throws Exception When a component can not be initialized
     */
    @BeforeEach
    void setUp() throws Exception {
        FileUtils.deleteDirectory(this.dir.toFile());
        Files.createDirectory(this.dir);
        try (InputStream in = this.getClass().getResourceAsStream("testOntologyAnne.rdf");
                OutputStream out = Files.newOutputStream(this.onto);) {
            IOUtils.copy(in, out);
        }
        this.ontoOnto = new RDFInterpreterFactory().getInterpreter(this.onto, this.cont).buildOntology();
        this.fileSaver = new FileSaver();
        this.viewMockup = Mockito.mock(ViewInterface.class);
        this.tab = Mockito.mock(TabInterface.class);
        this.graph = Mockito.mock(GraphInterface.class);
        Mockito.when((this.viewMockup).createTab(ArgumentMatchers.isA(TabControllerInterface.class),
                ArgumentMatchers.isA(Configuration.class), ArgumentMatchers.isA(GraphControllerInterface.class),
                ArgumentMatchers.isA(String.class))).thenReturn(this.tab);
        Mockito.when(this.tab.getGraph()).thenReturn(this.graph);
        this.database = new Database(this.fileSaver, this.dir);
        this.icons = new IconDatabase(this.dir, this.getClass().getResourceAsStream("tolowiz_hat.png").readAllBytes(),
                "png", new HashSet<>());
        this.cont = new ApplicationController(this.viewMockup, this.fileSaver, this.database, this.interpreter,
                this.icons);
        this.tabCont = new TabController(this.cont, this.viewMockup, this.fileSaver, this.database, this.icons,
                this.ontoOnto, "Test Tab");
        this.config = this.tabCont.getConfig();

        this.objectToTest = new GraphController(this.tabCont);
    }

    /**
     * Tests if passing Illegal Arguments to the methods throws Exceptions.
     */
    @Test
    public void testIllegalArguments() {
        Group mockGroup = Mockito.mock(Group.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.objectToTest.deleteGroup(mockGroup));

        RelationTypeConfiguration mockRTC = Mockito.mock(RelationTypeConfiguration.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.objectToTest.showRelationType(mockRTC));

        InstanceConfiguration mockInstance = Mockito.mock(InstanceConfiguration.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.objectToTest.showInstance(mockInstance));

        InstanceTypeConfiguration mockITC = Mockito.mock(InstanceTypeConfiguration.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.objectToTest.showInstanceType(mockITC));

        ValueType mockValue = Mockito.mock(ValueType.class);
        this.config.getInstanceTypes().forEach(itc -> Assertions.assertThrows(IllegalArgumentException.class,
                () -> this.objectToTest.activateValue(itc, mockValue)));

    }

    /**
     * Tests if setting the visibility for types works correctly.
     */
    @Test
    void testTypeVisibility() {
        this.config.getInstanceTypes().forEach(itc -> {
            Assertions.assertEquals(itc.isVisible(), TypeVisibility.NO);
            this.objectToTest.showInstanceType(itc);
            Assertions.assertEquals(itc.isVisible(), TypeVisibility.YES);
            this.objectToTest.hideInstanceType(itc);
            Assertions.assertEquals(itc.isVisible(), TypeVisibility.NO);
        });
    }

    /**
     * Tests if setting the visibility for instances works correctly.
     */
    @Test
    void testInstanceVisibility() {
        this.config.getInstances().forEach(ic -> {
            Assertions.assertFalse(ic.isVisible());
            this.objectToTest.showInstance(ic);
            Assertions.assertTrue(ic.isVisible());
            this.objectToTest.hideInstance(ic);
            Assertions.assertFalse(ic.isVisible());
        });
    }

    /**
     * Tests if working with groups is working correctly.
     */
    @Test
    void testGroups() {
        Assertions.assertEquals(this.config.getGroups().size(), 0);
        this.objectToTest.createNewGroup("Group 1", (InstanceConfiguration) null);
        this.objectToTest.createNewGroup("Group 2", this.config.getInstances().stream().findAny().orElse(null));
        this.objectToTest.createNewGroup("Full House", new HashSet<>(this.config.getInstances()));
        Assertions.assertEquals(this.config.getGroups().size(), 3);

        this.config.getGroups().forEach(g -> {
            this.objectToTest.showGroup(g);
            Assertions.assertTrue(g.getInstances().stream().allMatch(InstanceConfiguration::isVisible));
            this.objectToTest.hideGroup(g);
            Assertions.assertTrue(g.getInstances().stream().noneMatch(InstanceConfiguration::isVisible));
        });
        Group g = this.config.getGroups().stream().findFirst().orElse(null);
        g.setMark(new InstanceMark(Color.LIGHT_GRAY));
        this.config.getInstances().forEach(i -> {
            this.objectToTest.addToGroup(g, i);
            Assertions.assertEquals(g.getMark(), i.getEffectiveMark());
            this.objectToTest.removeFromGroup(g, i);
        });
        this.objectToTest.deleteGroup(g);
        Assertions.assertEquals(this.config.getGroups().size(), 2);
    }

    /**
     * Tests creating a group with a null set.
     */
    @Test
    public void testGroupsIllegal() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> this.objectToTest.createNewGroup("name", (Set<InstanceConfiguration>) null));
    }

    /**
     * Tests if using ValueTypes is working correctly.
     */
    @Test
    void testValues() {
        InstanceTypeConfiguration itc = this.config.getInstanceTypes().stream().findAny().orElse(null);
        itc.getAllValues().forEach(vt -> {
            Assertions.assertFalse(itc.getActiveValues().contains(vt));
            this.objectToTest.activateValue(itc, vt);
            Assertions.assertTrue(itc.getActiveValues().contains(vt));
            this.objectToTest.deactivateValue(itc, vt);
            Assertions.assertFalse(itc.getActiveValues().contains(vt));
        });
    }

    /**
     * Tests if both of the setters for attributes are working correctly.
     *
     * @param n Integer Values to set
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 6999, 3})
    void testSetters(int n) {
        this.objectToTest.setNumberOfParallelRelations(n);
        Assertions.assertEquals(this.config.getmaxParallelRelations(), n);

        this.objectToTest.setRelationDepth(n);
        Assertions.assertEquals(this.config.getDepth(), n);

    }

    /**
     * Tests if both of the setters for attributes throw Exceptions if fed a wrong
     * argument.
     */
    @Test
    public void testSettersIllegal() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> this.objectToTest.setNumberOfParallelRelations(0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.objectToTest.setRelationDepth(-1));
    }

    /**
     * Tests setting a handler for types.
     */
    @Test
    public void testSetHandler() {
        DefaultHandler han = new DefaultHandler(this.config);
        this.config.getInstanceTypes().forEach(t -> {
            Assertions.assertTrue(han.checkLegality(t));
            this.objectToTest.setHandlerForType(t, han);
        });
    }

    /**
     * Tests setting an Illegal handler for a type
     */
    @Test
    public void testSetHandlerIllegal() {
        DefaultHandler han = new InterfaceHandler(this.config);
        this.config.getInstanceTypes().forEach(t -> {
            if (!han.checkLegality(t)) {
                Assertions.assertThrows(IllegalArgumentException.class,
                        () -> this.objectToTest.setHandlerForType(t, han));
            }

        });
    }

    /**
     * Tests if showing and hiding Relation types works.
     */
    @Test
    public void testRelations() {
        this.config.getRelationTypes().forEach(rt -> Assertions.assertFalse(rt.isVisible()));
        this.objectToTest.showAllRelations();
        this.config.getRelationTypes().forEach(rt -> Assertions.assertTrue(rt.isVisible()));
        this.objectToTest.hideAllRelations();
        this.config.getRelationTypes().forEach(rt -> {
            Assertions.assertFalse(rt.isVisible());
            this.objectToTest.showRelationType(rt);
            Assertions.assertTrue(rt.isVisible());
            this.objectToTest.hideRelationType(rt);
            Assertions.assertFalse(rt.isVisible());
        });
    }

    /**
     * Tests the relation styles.
     */
    @Test
    public void testRelationTypeSymbol() {
        RelationStyle style = new RelationStyle();
        style.setShape(ArrowShape.ARROW);
        this.config.getRelationTypes().forEach(rt -> {
            this.objectToTest.changeRelationTypeSymbol(rt, style);
            Assertions.assertEquals(style, rt.getStyle());
        });
    }

    /**
     * Tests the icons for InstanceTyps
     */
    @Test
    public void testIcons() {
        IconInterface icon = Mockito.mock(IconInterface.class);
        this.config.getInstanceTypes().forEach(it -> {
            this.objectToTest.changeInstanceTypeSymbol(it, icon);
            Assertions.assertEquals(icon, it.getIcon());
        });
    }

    /**
     * Tests if the getConfiguration Method works correctly.
     */
    @Test
    public void testGetConfig() {
        Assertions.assertEquals(this.config, this.objectToTest.getConfiguration());
    }

    /**
     * Tests marking, unmarking and clearing instances.
     */
    @Test
    public void testMarks() {
        InstanceMark im1 = new InstanceMark(InstanceStroke.DOTS).setColor(Color.BLUE).setShape(InstanceShape.CROSS);
        InstanceMark im2 = new InstanceMark(Color.YELLOW);
        this.config.getInstances().forEach(i -> {
            this.objectToTest.markInstance(i, im1);
            Assertions.assertEquals(im1, i.getEffectiveMark());
            this.objectToTest.unmarkInstance(i, im1);
            Assertions.assertEquals(0, i.getMarks().size());
            this.objectToTest.markInstance(i, im1);
            this.objectToTest.markInstance(i, im2);
            this.objectToTest.clearInstance(i);
            Assertions.assertEquals(0, i.getMarks().size());

        });
    }

    /**
     * Tests the ValueTypes of InstanceTypes.
     */
    @Test
    public void testValueTypes() {
        this.config.getRelationTypes().forEach(rt -> Assertions.assertFalse(rt.isVisible()));
        this.objectToTest.showAllRelations();
        this.config.getRelationTypes().forEach(rt -> Assertions.assertTrue(rt.isVisible()));
        this.objectToTest.hideAllRelations();
        this.config.getRelationTypes().forEach(rt -> {
            Assertions.assertFalse(rt.isVisible());
            this.objectToTest.showRelationType(rt);
            Assertions.assertTrue(rt.isVisible());
            this.objectToTest.hideRelationType(rt);
            Assertions.assertFalse(rt.isVisible());
        });

        this.config.getInstanceTypes().forEach(itc -> {
            itc.getAllValues().forEach(v -> Assertions.assertFalse(itc.getActiveValues().contains(v)));
            this.objectToTest.activateAllValues(itc);
            itc.getAllValues().forEach(v -> Assertions.assertTrue(itc.getActiveValues().contains(v)));
            this.objectToTest.deactivateAllValues(itc);

            itc.getAllValues().forEach(v -> {
                Assertions.assertFalse(itc.getActiveValues().contains(v));
                this.objectToTest.activateValue(itc, v);
                Assertions.assertTrue(itc.getActiveValues().contains(v));
                this.objectToTest.deactivateValue(itc, v);
                Assertions.assertFalse(itc.getActiveValues().contains(v));
            });

        });
    }

    /**
     * Tests restoring the standard alignment. (Hides everything and resets to
     * default position).
     */
    @Test
    public void testRestoreAlignment() {
        this.config.getInstanceTypes().forEach(this.objectToTest::showInstanceType);
        this.config.getRelationTypes().forEach(this.objectToTest::showRelationType);
        this.config.getInstances()
                .forEach(i -> this.objectToTest.moveNodeTo(i, Math.random() * 1000, Math.random() * -1000));
        this.objectToTest.restoreStandardAlignment();
        this.config.getInstanceTypes().forEach(itc -> Assertions.assertEquals(TypeVisibility.NO, itc.isVisible()));
        this.config.getRelationTypes().forEach(rtc -> Assertions.assertFalse(rtc.isVisible()));
        this.config.getInstances().forEach(i -> Assertions.assertNull(i.getStoredPosition()));
    }

    /**
     * Tests restoring the standard view. (Removes every style and mark, everything
     * visual)
     */
    @Test
    public void testRestoreView() {
        InstanceMark im = new InstanceMark(InstanceShape.BOX);
        this.config.getInstances().forEach(i -> this.objectToTest.markInstance(i, im));
        RelationStyle rs = new RelationStyle();
        rs.setShape(ArrowShape.CIRCLE);
        this.config.getRelationTypes().forEach(r -> this.objectToTest.changeRelationTypeSymbol(r, rs));

        this.objectToTest.restoreStandardView();
        this.config.getInstances().forEach(i -> Assertions.assertEquals(0, i.getMarks().size()));
        this.config.getRelationTypes().forEach(r -> Assertions.assertEquals(new RelationStyle(), r.getStyle()));
    }

    /**
     * @throws IOException if an error occurs
     */
    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(this.dir.toFile());
    }

}
