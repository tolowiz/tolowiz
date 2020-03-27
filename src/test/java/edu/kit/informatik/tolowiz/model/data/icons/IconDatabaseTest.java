/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.icons;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.FileUtils;
import org.javatuples.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.informatik.tolowiz.model.ontology.Instance;
import edu.kit.informatik.tolowiz.model.ontology.InstanceType;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;
import edu.kit.informatik.tolowiz.model.ontology.ValueType;

/**
 * @author Tobias Klumpp
 *
 */
@SuppressWarnings({ "nls", "unused" })
class IconDatabaseTest {
    /**
     * The program directory
     */
    private Path dir = Paths.get(System.getProperty("user.home")).resolve(".tolowiz");
    /** The parent path where all icons are stored */
    private Path parent = this.dir.resolve("icons");
    /**
     * The path the icon is loaded from
     */
    private Path src = this.dir.resolve("icons_test");
    /** the path of the icon to be tested */
    private Path path = this.src.resolve("icon.png");
    /** the path of the second icon to be tested */
    private Path path2 = this.src.resolve("icon2.png");
    /**
     * Instance type for which the icons will be set
     */
    private InstanceType type;
    /**
     * The database to be tested
     */
    private IconDatabase database;
    private HashSet<Pair<String, Pair<byte[], String>>> defaultIcons;

    /**
     * @throws java.lang.Exception if an error occurs
     */
    @BeforeEach
    void setUp() throws Exception {
        FileUtils.deleteDirectory(this.dir.toFile());
        Files.createDirectory(this.dir);
        Files.createDirectory(this.src);
        try (InputStream in = this.getClass().getResourceAsStream("tolowiz_hat.png");
                OutputStream out = Files.newOutputStream(this.path);) {
            IOUtils.copy(in, out);
        }

        try (InputStream in = this.getClass().getResourceAsStream("tolowiz_hat.png");
                OutputStream out = Files.newOutputStream(this.path2);) {
            IOUtils.copy(in, out);
        }
        Ontology onto = new Ontology("Test ID", "hi");

        onto.addType("Typ", new TreeSet<ValueType>(), null, new TreeSet<InstanceType>(), new TreeSet<Instance>(),
                "iri\0");
        this.type = onto.getTypes().iterator().next();
        this.defaultIcons = new HashSet<>();
        this.defaultIcons.add(new Pair<>("pc", new Pair<>( //$NON-NLS-1$
                this.getClass().getResourceAsStream("tolowiz_hat.png") //$NON-NLS-1$
                        .readAllBytes(),
                "png")));

        this.database = new IconDatabase(this.dir,
                this.getClass().getResourceAsStream("tolowiz_hat.png").readAllBytes(), "png", this.defaultIcons);

    }

    /**
     * @throws java.lang.Exception if an error occurs
     */
    @AfterEach
    void tearDown() throws Exception {
        FileUtils.deleteDirectory(this.dir.toFile());
    }

    /**
     * Test method constructor
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testIconDatabase() throws Exception {
        Assertions.assertNotNull(new IconDatabase(this.dir,
                this.getClass().getResourceAsStream("tolowiz_hat.png").readAllBytes(), "png", this.defaultIcons));
        FileUtils.deleteDirectory(this.dir.toFile());
        Assertions.assertThrows(IconDatabaseException.class, () -> new IconDatabase(this.dir,
                this.getClass().getResourceAsStream("tolowiz_hat.png").readAllBytes(), "png", this.defaultIcons));

    }

    /**
     * Test method for {@link IconDatabase#getByType(InstanceType)}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testGetByType() throws Exception {
        this.database.setForType(this.type, this.path);
        Assertions.assertNotNull(this.database.getByType(this.type).iterator().next().getPath());
        FileUtils.deleteDirectory(this.parent.toFile());
        Assertions.assertThrows(IconDatabaseException.class,
                () -> this.database.getByType(this.type).iterator().next().getPath());
    }

    /**
     * @throws Exception if an internal error occurs
     */
    @Test
    void testGetDefaultIcon() throws Exception {
        Assertions.assertNotNull(this.database.getDefaultIcon(this.type).getPath());
        FileUtils.deleteDirectory(this.parent.toFile());
        Assertions.assertThrows(IconDatabaseException.class, () -> this.database.getDefaultIcon(this.type).getPath());
    }

    /**
     * @throws Exception if an internal error occurs
     */
    @Test
    void testSetDefaultIcon() throws Exception {

        this.database.setDefaultIcon(this.path2);
        Assertions.assertTrue(Arrays.equals(new Icon(this.path2).getContent(),
                new Icon(this.database.getDefaultIcon().getPath()).getContent()));
        FileUtils.deleteDirectory(this.parent.toFile());
        Assertions.assertThrows(IconDatabaseException.class, () -> this.database.setDefaultIcon(this.path2));
    }

    /**
     * Test method for {@link IconDatabase#setForType(InstanceType, Path)}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testSetForType() throws Exception {
        this.database.setForType(this.type, this.path);
        Assertions.assertNotNull(this.database.getByType(this.type).iterator().next().getPath());
        FileUtils.deleteDirectory(this.parent.toFile());
        Assertions.assertThrows(IconDatabaseException.class, () -> this.database.setForType(this.type, this.path));
    }

}
