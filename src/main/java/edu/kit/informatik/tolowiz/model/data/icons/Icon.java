/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.icons;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.apache.jena.util.FileUtils;

import edu.kit.informatik.tolowiz.model.ontology.InstanceType;

/**
 * Default implementation for the StandaloneIconInterface.
 *
 * @author Tobias Klumpp
 *
 */
class Icon implements IconInterface {
    /**
     * Serial version uid because icons (paths to icons) are serialized as part of
     * the configuration.
     */
    private static final long serialVersionUID = 5872145993769143132L;
    /**
     * the database used for storing icons
     */
    private static IconDatabase database;
    /**
     * The filetype.
     */
    private String filetype;

    /**
     * The content of the file.
     */
    private byte[] content;

    /**
     * the path which will be omitted if saved
     */
    private transient Path path = null;
    /**
     * the type the image is associated with
     */
    private final InstanceType instanceType;

    /**
     * Constructs an icon using path to the location in the configuration directory.
     * This constructor should only be called by the IconDatabase class
     *
     * @param path the path
     * @param type the instance type the icon is associated with
     */
    Icon(Path path, InstanceType type) {
        this.instanceType = type;
        this.path = path;
    }

    /**
     * Constructs an icon using only a path to the location in the configuration
     * directory. This constructor should only be called by the IconDatabase class
     *
     * @param path the path
     */
    Icon(Path path) {
        this.instanceType = null;
        this.path = path;
    }

    // XXX this static method is quite ugly but needed because icons are not
    // linked to an IconDatabase after deserialization
    /**
     * Sets the icon database where the icons are stored
     *
     * @param database the icon database
     */
    static void setIconDatabase(IconDatabase database) {
        Icon.database = database;
    }

    /**
     * Return the byte contents of the icon
     *
     * @return the content of the icon in raw format
     * @throws IconDatabaseException if an internal error occurs
     */
    byte[] getContent() throws IconDatabaseException {
        if (this.content == null) {
            try {
                this.content = Files.readAllBytes(this.path);
            } catch (IOException e) {
                throw new IconDatabaseException(e);
            }
        }
        return this.content;
    }

    /**
     * returns the file type for this icon
     *
     * @return the file type as a String which is determined by the commonly used
     *         file ending for this type
     */
    String getFileType() {
        if (this.filetype == null) {
            this.filetype = FileUtils.getFilenameExt(this.path.getFileName().toString());
        }
        return this.filetype;
    }

    @Override
    public Path getPath() throws IconDatabaseException {
        return this.path;
    }

    @Override
    public int hashCode() {
        try {
            return Arrays.hashCode(this.getContent());
        } catch (@SuppressWarnings("unused") IconDatabaseException e) {
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Icon other = (Icon) obj;
        try {
            if (Arrays.equals(this.getContent(), other.getContent())
                    && this.getInstanceType().equals(other.getInstanceType())) {
                return true;
            }
        } catch (@SuppressWarnings("unused") IconDatabaseException e) {
            return false;
        }
        return false;
    }

    /**
     * Returns the instance type this icon is associated with
     *
     * @return the instance type
     */
    InstanceType getInstanceType() {
        return this.instanceType;
    }

    private void readObject(ObjectInputStream input) throws ClassNotFoundException, IOException {
        input.defaultReadObject();
        try {
            this.path = Icon.database.importIcon(this);
        } catch (@SuppressWarnings("unused") IconDatabaseException e) {
            // nothing here
        }
    }

    private void writeObject(ObjectOutputStream output) throws IOException {
        try {
            this.content = this.getContent();
        } catch (@SuppressWarnings("unused") IconDatabaseException e) {
            // nothing here
        }
        this.filetype = this.getFileType();
        output.defaultWriteObject();
    }
}
