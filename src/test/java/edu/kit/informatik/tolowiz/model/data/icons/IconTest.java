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

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.informatik.tolowiz.model.ontology.InstanceType;

/**
 * @author Tobias Klumpp
 *
 */
@SuppressWarnings({ "nls", "unused" })
class IconTest {
    /**
     * The program directory
     */
    private Path dir = Paths.get(System.getProperty("user.home")) //$NON-NLS-1$
            .resolve(".tolowiz"); //$NON-NLS-1$
    private InstanceType type = null;

    /**
     * the icon to be tested
     */
    private byte[] icon;
    /** The parent path where all icons are stored */
    private Path parent = this.dir.resolve("icons_test");
    /** the path of the icon to be tested */
    private Path path = this.parent.resolve("icon.png");

    /**
     *
     * @throws java.lang.Exception if an error occurs
     */
    @BeforeEach
    void setUp() throws Exception {
        FileUtils.deleteDirectory(this.dir.toFile());
        Files.createDirectory(this.dir);
        Files.createDirectory(this.parent);
        try (InputStream in = this.getClass().getResourceAsStream("tolowiz_hat.png");
                OutputStream out = Files.newOutputStream(this.path);) {
            IOUtils.copy(in, out);
        }
        this.icon = new Icon(this.path, this.type).getContent();
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
     *
     * @throws java.lang.Exception if an error occurs
     */
    @Test
    void testIcon() throws Exception {
        Assertions.assertNotNull(new Icon(this.path, this.type).getContent());
        Files.delete(this.path);
        Assertions.assertThrows(IconDatabaseException.class, () -> new Icon(this.path, this.type).getContent());

    }
    
    /**
     * Test method for
     *
     * @throws java.lang.Exception if an error occurs
     */
    @Test
    void testIcon2() throws Exception {
        Assertions.assertNotNull(new Icon(this.path).getContent());
        Files.delete(this.path);
        Assertions.assertThrows(IconDatabaseException.class, () -> new Icon(this.path).getContent());

    }

    /**
     * Test method for hashcode
     *
     * @throws java.lang.Exception if an error occurs
     */
    @Test
    void testHashCode() throws Exception {
        Assertions.assertEquals(new Icon(this.path, this.type).hashCode(), new Icon(this.path, this.type).hashCode());
        Files.delete(this.path);
        Assertions.assertEquals(0, new Icon(this.path, this.type).hashCode());

    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.model.data.icons.Icon#getFileType()}.
     *
     * @throws java.lang.Exception if an error occurs
     */
    @Test
    void testGetType() throws Exception {
        String filetype = new Icon(this.path, this.type).getFileType();
        String filetypeTest = FileUtils.extension(this.path.getFileName().toString());
        Assertions.assertTrue(filetype.contentEquals(filetypeTest));
    }

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.model.data.icons.Icon#getContent()}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testGetContent() throws Exception {
        byte[] content = new Icon(this.path, this.type).getContent();
        byte[] contentTest = Files.readAllBytes(this.path);
        for (int i = 0; i < content.length; i++) {
            if (content[i] != contentTest[i]) {
                Assertions.fail();
            }
        }
    }
    

    /**
     * Test method for
     * {@link edu.kit.informatik.tolowiz.model.data.icons.Icon#equals}.
     *
     * @throws Exception if an error occurs
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    void testEquals() throws Exception {
        Icon icon1 = new Icon(this.path, this.type);
        Assertions.assertTrue(icon1.equals(icon1));
        Assertions.assertFalse(icon1.equals(null));
        Assertions.assertFalse(icon1.equals("Hallo"));


    }

}
