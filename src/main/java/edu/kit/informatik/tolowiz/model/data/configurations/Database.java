/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.configurations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import edu.kit.informatik.tolowiz.model.data.file.FileSaverInterface;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;

/**
 * Database class for storing program data. This implementation internally uses
 * the FileSaver interface to store the configurations in a configuration
 * directory.
 *
 * @author Tobias Klumpp
 *
 */
public class Database implements DatabaseInterface {
    /**
     * The directory where the configurations are stored
     */
    private static final String STORAGE_DIRECTORY = "storage"; //$NON-NLS-1$

    /**
     * The FileSaver class to be used for saving the files.
     */
    private FileSaverInterface filesaver;

    /**
     * The directory of the database.
     */
    private Path directory;

    /**
     * Default constructor.
     *
     * @param filesaver  the file saver to be used to store the database data
     * @param programdir the directory where the program stores its internal data
     * @throws InternalDatabaseException if an internal database error occurs
     */
    public Database(FileSaverInterface filesaver, Path programdir) throws InternalDatabaseException {
        this.filesaver = filesaver;
        this.directory = programdir.resolve(Database.STORAGE_DIRECTORY); // $NON-NLS-1$
        if (!Files.exists(this.directory)) {
            try {
                Files.createDirectory(this.directory);
            } catch (IOException e) {
                throw new InternalDatabaseException(e);
            }
        }

    }

    @Override
    public StorageInterface createStorage(Ontology onto) throws InternalDatabaseException {
        return new Storage(this.filesaver, onto, this.directory);
    }

    @Override
    public List<String> getStorageIDs() throws InternalDatabaseException {
        try (var stream = Files.newDirectoryStream(this.directory)) {
            Iterator<Path> it = stream.iterator();
            List<String> result = new LinkedList<>();
            while (it.hasNext()) {
                Path storage = it.next();
                if (Files.isDirectory(storage)) {
                    result.add(this.getNameForDirectory(storage).getValue0());
                }
            }
            return result;
        } catch (IOException e) {
            throw new InternalDatabaseException(e);
        }
    }

    private Pair<String, String> getNameForDirectory(Path storageDir) throws InternalDatabaseException {
        Path textfile = this.directory.resolve(storageDir.getFileName().toString() + ".txt"); //$NON-NLS-1$
        String encoded;
        try {
            encoded = Files.readString(textfile);
        } catch (IOException e) {
            throw new InternalDatabaseException(e);
        }
        return Storage.decodeDirectory(encoded);
    }

    @Override
    public List<String> getStorageNames() throws InternalDatabaseException {
        try (var stream = Files.newDirectoryStream(this.directory)) {
            Iterator<Path> it = stream.iterator();
            List<String> result = new LinkedList<>();
            while (it.hasNext()) {
                Path storage = it.next();
                if (Files.isDirectory(storage)) {
                    result.add(this.getNameForDirectory(storage).getValue1());
                }
            }
            return result;
        } catch (IOException e) {
            throw new InternalDatabaseException(e);
        }
    }

    @Override
    public StorageInterface getStorageByID(String id) throws InternalDatabaseException, NoSuchEntryException {
        try (var stream = Files.newDirectoryStream(this.directory)) {
            Iterator<Path> it = stream.iterator();
            while (it.hasNext()) {
                Path storage = it.next();

                if (Files.isDirectory(storage)) {
                    if (this.getNameForDirectory(storage).getValue0().equals(id)) {
                        return new Storage(this.filesaver, storage);
                    }
                }
            }
            throw new NoSuchEntryException();
        } catch (IOException e) {
            throw new InternalDatabaseException(e);
        }
    }

    @Override
    public StorageInterface getStorageByName(String name) throws NoSuchEntryException, InternalDatabaseException {
        try (var stream = Files.newDirectoryStream(this.directory)) {
            Iterator<Path> it = stream.iterator();
            while (it.hasNext()) {
                Path storage = it.next();
                if (Files.isDirectory(storage)) {
                    if (this.getNameForDirectory(storage).getValue1().equals(name)) {
                        return new Storage(this.filesaver, storage);
                    }
                }
            }
            throw new NoSuchEntryException();
        } catch (IOException e) {
            throw new InternalDatabaseException(e);
        }
    }

}
