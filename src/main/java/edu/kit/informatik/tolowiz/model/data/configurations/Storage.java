/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.configurations;

import edu.kit.informatik.tolowiz.model.data.file.FileSaverInterface;
import edu.kit.informatik.tolowiz.model.data.file.FileTypeException;
import edu.kit.informatik.tolowiz.model.data.icons.IconDatabaseException;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FilenameUtils;
import org.javatuples.Pair;

/**
 * Class implementing Storage interface with default database.
 *
 * @author Tobias Klumpp
 *
 */
class Storage implements StorageInterface {
    /**
     * The file ending of all files
     */
    private static final String CONF_SUFFIX = ".bin"; //$NON-NLS-1$
    /**
     * The prefix of the configuration files
     */
    private static final String CONF_PREFIX = "conf_"; //$NON-NLS-1$
    /**
     * The name of the autosave file
     */
    private static final String AUTO_NAME = "autosave"; //$NON-NLS-1$
    /**
     * The filesaver class to be used for saving files.
     *
     */
    private FileSaverInterface filesaver;

    /**
     * The directory where all configuration files of this ontology are located.
     */
    private Path directory;

    /**
     * Constructor creating Storage with filesaver object.
     *
     * @param filesaver the filesaver object
     * @param onto the ontology linked to this storage
     * @param parent the parent directory this directory is located
     * @throws InternalDatabaseException if an internal database error occurs
     */
    Storage(FileSaverInterface filesaver, Ontology onto, Path parent)
            throws InternalDatabaseException {
        this(filesaver, parent.resolve(Storage.getDirectoryForName(parent,
                onto.getIRI(), onto.getName())));
    }

    /**
     * Constructor creating Storage with filesaver object.
     *
     * @param filesaver the filesaver object
     * @param directory the directory
     * @throws InternalDatabaseException if an internal database error occurs
     */
    Storage(FileSaverInterface filesaver, Path directory)
            throws InternalDatabaseException {
        this.filesaver = filesaver;
        this.directory = directory;
        if (!Files.exists(this.directory)) {
            try {
                Files.createDirectory(this.directory);
            } catch (IOException e) {
                throw new InternalDatabaseException(e);
            }
        }
    }

    /**
     * Returns the directory for the name of an ontology
     *
     * @param parent the parent directory
     * @param iri the iri
     * @param name the name of the ontology
     * @return the directory
     * @throws InternalDatabaseException if an internal error occurs
     */
    private static Path getDirectoryForName(final Path parent, final String iri,
            final String name) throws InternalDatabaseException {
        String encodedName = Storage.encodeDirectory(iri, name);
        String dirName = null;
        try (var stream = Files.newDirectoryStream(parent)) {
            Iterator<Path> it = stream.iterator();
            while (it.hasNext()) {
                Path storage = it.next();
                if (!Files.isDirectory(storage)) {
                    if (Storage.decodeDirectory(Files.readString(storage))
                            .getValue0().equals(iri)) {
                        dirName = FilenameUtils
                                .getBaseName(storage.getFileName().toString());
                    }
                }
            }
        } catch (IOException e) {
            throw new InternalDatabaseException(e);
        }
        if (dirName == null) {
            int highest = 0;
            try (var stream = Files.newDirectoryStream(parent)) {
                Iterator<Path> it = stream.iterator();
                while (it.hasNext()) {
                    Path storage = it.next();
                    if (Files.isDirectory(storage)) {
                        if (highest <= Integer
                                .parseInt(FilenameUtils.getBaseName(
                                        storage.getFileName().toString()))) {
                            highest = Integer
                                    .parseInt(FilenameUtils.getBaseName(
                                            storage.getFileName().toString()))
                                    + 1;
                        }
                    }
                }
                dirName = Integer.toString(highest);
                Path textfile = parent.resolve(dirName + ".txt"); //$NON-NLS-1$
                Files.writeString(textfile, encodedName);
                Files.createDirectory(parent.resolve(dirName));
                return parent.resolve(dirName);
            } catch (IOException e) {
                throw new InternalDatabaseException(e);
            }
        }
        return parent.resolve(dirName);

    }

    @Override
    public void addEntry(String name, Configuration conf)
            throws InternalDatabaseException {
        try {
            this.save(this.encode(name), conf);
        } catch (IOException | IconDatabaseException e) {
            throw new InternalDatabaseException(e);
        }
    }

    @Override
    public void autosave(Configuration conf) throws InternalDatabaseException {
        try {
            this.save(
                    this.directory.resolve(
                            Paths.get(Storage.AUTO_NAME + Storage.CONF_SUFFIX)),
                    conf);
        } catch (IOException | IconDatabaseException e) {
            throw new InternalDatabaseException(e);
        }
    }

    /**
     * Saves a configuration in a file
     *
     * @param file the file the configuration should be saved to
     * @param conf the configuration
     * @throws IOException if an IO error occurs
     * @throws IconDatabaseException if an icon database error occurs
     */
    private void save(Path file, Configuration conf)
            throws IOException, IconDatabaseException {
        this.filesaver.generateFile(file).exportConfiguration(conf);
    }

    /**
     * Loads a configuration from a file
     *
     * @param file the file the configuration should be loaded from
     * @return the configuration
     * @throws FileTypeException if the file has the wrong filetype
     * @throws IOException if an IO error occurs
     */
    private Configuration load(Path file)
            throws FileTypeException, IOException {
        return this.filesaver.generateFile(file).importConfiguration();
    }

    @Override
    public void deleteEntry(String name) throws NoSuchEntryException {
        try {
            Files.delete(this.encode(name));
        } catch (IOException e) {
            throw new NoSuchEntryException(e);
        }

    }

    @Override
    public Configuration getAutosaveEntry()
            throws NoSuchEntryException, InternalDatabaseException {
        Path file = this.directory
                .resolve(Paths.get(Storage.AUTO_NAME + Storage.CONF_SUFFIX));
        try {
            return this.load(file);
        } catch (NoSuchFileException e) {
            if (Files.exists(this.directory)) {
                throw new NoSuchEntryException(e);
            }
            throw new InternalDatabaseException(e);
        } catch (FileTypeException | IOException e) {
            throw new InternalDatabaseException(e);
        }
    }

    @Override
    public boolean existsAutosave() {
        Path file = this.directory
                .resolve(Paths.get(Storage.AUTO_NAME + Storage.CONF_SUFFIX));
        return Files.exists(file);
    }

    @Override
    public Configuration getEntry(String name)
            throws NoSuchEntryException, InternalDatabaseException {
        try {
            return this.load(this.encode(name));
        } catch (NoSuchFileException e) {
            if (Files.exists(this.directory)) {
                throw new NoSuchEntryException(e);
            }
            throw new InternalDatabaseException(e);
        } catch (FileTypeException | IOException e) {
            throw new InternalDatabaseException(e);
        }
    }

    @Override
    public List<String> getEntrys() throws InternalDatabaseException {
        try (var stream = Files.newDirectoryStream(this.directory)) {
            Iterator<Path> it = stream.iterator();
            List<String> result = new LinkedList<>();
            while (it.hasNext()) {
                var next = it.next();
                if (!next.getFileName().toString()
                        .equals(Storage.AUTO_NAME + Storage.CONF_SUFFIX)) {
                    result.add(Storage.decode(next));
                }
            }
            return result;
        } catch (IOException e) {
            throw new InternalDatabaseException(e);
        }
    }

    /**
     * Decodes the name from a filename.
     *
     * @param file the file which name should be decoded
     * @return the name for the configuration
     * @throws InternalDatabaseException if an internal error occurs
     */
    private static String decode(Path file) throws InternalDatabaseException {
        String filename = file.getFileName().toString();
        return Storage
                .fileDecode(filename.substring(Storage.CONF_PREFIX.length(),
                        filename.length() - Storage.CONF_SUFFIX.length()));
    }

    /**
     * Decode a filename of a directory to an IRI and a name
     *
     * @param name the name
     * @return a pair of the IRI and the name
     * @throws InternalDatabaseException if an internal error occurs
     */
    static Pair<String, String> decodeDirectory(String name)
            throws InternalDatabaseException {
        String[] parts = name.split("_"); //$NON-NLS-1$
        return new Pair<>(Storage.fileDecode(parts[0]),
                Storage.fileDecode(parts[1]));
    }

    /**
     * Encodes an iri and a name to a directory name for the storage
     *
     * @param iri the IRI
     * @param name the name
     * @return the name
     */
    static String encodeDirectory(String iri, String name) {
        return Storage.fileEncode(iri) + "_" + Storage.fileEncode(name); //$NON-NLS-1$
    }

    /**
     * Encodes a name to a file.
     *
     * @param name the under which filename should be determined
     * @return the file as a path object
     */
    private Path encode(String name) {
        return this.directory.resolve(Paths.get(Storage.CONF_PREFIX
                + Storage.fileEncode(name) + Storage.CONF_SUFFIX));
    }

    /**
     * Encodes a name so that it can be used as a filename.
     *
     * @param name the name
     * @return the resulting string
     */
    static String fileEncode(String name) {
        return new String(Hex.encodeHex(name.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Decodes an encoded name.
     *
     * @param filename the code
     * @return the resulting name
     * @throws InternalDatabaseException if an internal error occurs
     */
    static String fileDecode(String filename) throws InternalDatabaseException {
        try {
            return new String(Hex.decodeHex(filename), StandardCharsets.UTF_8);
        } catch (DecoderException e) {
            throw new InternalDatabaseException(e);
        }
    }

    @Override
    public void renameEntry(String old, String newName)
            throws NoSuchEntryException {
        try {
            Files.move(this.encode(old), this.encode(newName));
        } catch (IOException e) {
            throw new NoSuchEntryException(e);
        }
    }

    @Override
    public String getOntologyName() throws InternalDatabaseException {
        try {
            return Storage.decodeDirectory(
                    Files.readString(this.directory.getParent().resolve(
                            this.directory.getFileName().toString() + ".txt")))
                    .getValue1();
        } catch (IOException e) {
            throw new InternalDatabaseException(e);
        }
    }
}
