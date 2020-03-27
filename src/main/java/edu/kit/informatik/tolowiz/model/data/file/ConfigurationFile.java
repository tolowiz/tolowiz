/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.file;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.kit.informatik.tolowiz.model.data.icons.IconDatabaseException;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;

/**
 * Implementation for file interface used by filesaver.
 *
 * @author Tobias Klumpp
 *
 */
class ConfigurationFile implements ConfigurationFileInterface {
    /**
     * The path to the file.
     */
    private Path path;

    /**
     * Constructor creating a file from a Path. Should only be called by FileSaver
     * class. As mentioned in the interface operations on the file system are only
     * performed if either the export or the import method is called.
     *
     * @param path the path to the file
     */
    ConfigurationFile(Path path) {
        this.path = path;
    }

    @Override
    public void exportConfiguration(Configuration conf) throws IOException, IconDatabaseException {
        try (ObjectOutputStream oout = new ObjectOutputStream(Files.newOutputStream(this.path))) {
            oout.writeObject(conf);
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public Configuration importConfiguration() throws FileTypeException, IOException {
        Configuration result;
        try (ObjectInputStream oout = new ObjectInputStream(Files.newInputStream(this.path))) {
            result = (Configuration) oout.readObject();
        } catch (IOException e) {
            if (e instanceof InvalidClassException || e instanceof java.io.StreamCorruptedException) {
                throw new FileTypeException(e);
            }
            throw e;
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new FileTypeException(e);
        }
        return result;
    }

}
