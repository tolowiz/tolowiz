/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import edu.kit.informatik.tolowiz.controller.ApplicationController;
import edu.kit.informatik.tolowiz.controller.interpretation.OntologyFileException;
import edu.kit.informatik.tolowiz.controller.interpretation.RDFInterpreterFactory;
import edu.kit.informatik.tolowiz.model.data.icons.IconDatabase;
import edu.kit.informatik.tolowiz.model.data.icons.IconDatabaseException;
import edu.kit.informatik.tolowiz.model.ontology.Instance;
import edu.kit.informatik.tolowiz.model.ontology.InstanceType;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;
import edu.kit.informatik.tolowiz.model.ontology.RelationType;

public class ConfigurationTest {
    private static final String ONTOLOGY = "testOntologyMedium.rdf";
    private Path dir = Paths.get(System.getProperty("user.home")).resolve(".tolowiz");
    private Path onto = this.dir.resolve(ConfigurationTest.ONTOLOGY);

    private ApplicationController cont = new ApplicationController(null, null, null, null, null);

    private IconDatabase idb;
    private Ontology ontology;
    private Configuration objectToTest;

    /**
     * Creates a new Configuration for each test.
     */
    @BeforeEach
    void setup() {
        try {
            if (Files.exists(this.dir)) {
                FileUtils.deleteDirectory(this.dir.toFile());
            }
            Files.createDirectory(this.dir);
            InputStream in = this.getClass().getResourceAsStream(ConfigurationTest.ONTOLOGY);
            OutputStream out = Files.newOutputStream(this.onto);
            IOUtils.copy(in, out);
            this.idb = Mockito.mock(IconDatabase.class);
            Mockito.when(this.idb.getDefaultIcon(ArgumentMatchers.isA(InstanceType.class))).thenReturn(null);
            this.ontology = new RDFInterpreterFactory().getInterpreter(this.onto, this.cont).buildOntology();
        } catch (IOException | OntologyFileException | IconDatabaseException e) {
            Assertions.fail(e.getMessage());
            e.printStackTrace();
        }
        this.objectToTest = new Configuration(this.ontology, this.idb);
    }

    /**
     * Tests if all the types of the configuration are contained in the ontology and
     * have the same number of instances.
     */
    @Test
    public void testTypes() {
        Assertions.assertEquals(this.objectToTest.getInstanceTypes().size(), this.ontology.getTypes().size());
        this.objectToTest.getInstanceTypes().forEach(type -> {
            Optional<InstanceType> baseType = this.ontology.getTypes().stream()
                    .filter(a -> a.equals(type.getInstanceType())).findFirst();
            Assertions.assertFalse(baseType.isEmpty());
            Assertions.assertEquals(baseType.get().getInstances().size(), type.getMembers().size());
        });
    }

    /**
     * Tests if all the Instances of the configuration are contained in the ontology
     * and they have the same number of types.
     */
    @Test
    public void testInstances() {
        Assertions.assertEquals(this.objectToTest.getInstances().size(), this.ontology.getInstances().size());
        this.objectToTest.getInstances().forEach(inst -> {
            Optional<Instance> base = this.ontology.getInstances().stream()
                    .filter(a -> a.getURI().equals(inst.getURI())).findFirst();
            Assertions.assertFalse(base.isEmpty());
            Assertions.assertEquals(inst.getTypes().size(), base.get().getType().size());
        });

    }

    /**
     * Tests if all the RelationTypes of the configuration are contained in the
     * ontology and they have the same number of relations.
     */
    @Test
    public void testRelationTypes() {
        Assertions.assertEquals(this.objectToTest.getRelationTypes().size(), this.ontology.getRelationTypes().size());
        this.objectToTest.getRelationTypes().forEach(rt -> {
            Optional<RelationType> relationType = this.ontology.getRelationTypes().stream()
                    .filter(a -> a.getName().equals(rt.getName())).findFirst();
            Assertions.assertFalse(relationType.isEmpty());
            Assertions.assertEquals(relationType.get().getRelations().size(), rt.getMembers().size());
        });
    }

    /**
     * Tests if all the Relations of the configuration are contained in the
     * ontology.
     */
    @Test
    public void testRelations() {
        Assertions.assertEquals(this.objectToTest.getRelations().size(), this.ontology.getRelations().size());
        this.objectToTest.getRelations().forEach(r -> {
            Assertions.assertTrue(this.ontology.getRelations().stream().anyMatch(a -> a.getURI().equals(r.getURI())));
        });
    }

    /**
     * Tests the getters of the Configuration: getX by Name, IRI, URI etc.
     */
    @Test
    public void testGetters() {
        this.objectToTest.getInstanceTypes().stream().flatMap(itc -> itc.getAllValues().stream()).forEach(value -> {
            Assertions.assertEquals(this.objectToTest.getValueTypeByURI(value.getURI()), value); // getValueType
        });

        this.objectToTest.getRelationTypes().forEach(rtc -> {
            Assertions.assertEquals(this.objectToTest.getRelationTypeByName(rtc.getName()), rtc); // getRelationType
        });

        this.objectToTest.getInstanceTypes().forEach(itc -> {
            Assertions.assertEquals(this.objectToTest.getTypeByIRI(itc.getIRI()), itc); // getType
        });
    }

    /**
     * Tests the base type.
     */
    @Test
    public void testBaseType() {
        InstanceTypeConfiguration baseType = this.objectToTest.getRootType();
        Assertions.assertNotNull(baseType);
        Assertions.assertTrue(baseType.getSuperTypes().isEmpty());
        Assertions.assertEquals(baseType.getName(), "owl:Thing");
    }

    /**
     * Tests if adding listeners and listening for change works as expected.
     */
    @Test
    public void testListeners() {
        ConfigurationListenerInterface listener = mock(ConfigurationListenerInterface.class);
        this.objectToTest.addListener(listener);
        Method change;
        try {
            change = Configuration.class.getDeclaredMethod("changed");
            change.setAccessible(true);
            change.invoke(this.objectToTest); // trigger change event
            verify(listener).onFullChange();
            verifyNoMoreInteractions(listener);
            this.objectToTest.removeListener(listener);
            change.invoke(this.objectToTest); // trigger another change
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
            Assertions.fail(e);
        }

    }

    /**
     * Tests cloning and equals.
     */
    @Test
    public void testCloneAndEquals() {
        Configuration other = this.objectToTest.clone();
        Assertions.assertEquals(this.objectToTest, other);

    }

    /**
     * Deletes the test ontology after every test.
     *
     * @throws IOException if an error occurs
     */
    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(this.dir.toFile());
    }

}
