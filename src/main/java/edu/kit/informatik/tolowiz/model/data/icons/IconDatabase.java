/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.icons;

import edu.kit.informatik.tolowiz.model.ontology.InstanceType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.javatuples.Pair;

/**
 * Default implementation for the icon database just storing the icons in a
 * configuration directory.
 *
 * @author Tobias Klumpp
 *
 */
public class IconDatabase implements IconDatabaseInterface {

    private static final String GENERAL_DEFAULT = "zdefault"; //$NON-NLS-1$
    private static final String TYPE_DIRECTORY = "icons"; //$NON-NLS-1$
    private static final String DEFTYPE_PREFIX = "ztype"; //$NON-NLS-1$
    private static final String STANDARD_INFIX = "standard"; //$NON-NLS-1$
    private final Path typeDirectory;

    /**
     * Creates a new IconDatabase
     *
     * @param programdir the dir where the program saves its data
     * @param defaultContent the content of the default image
     * @param defaultType the filetype of the default image as a String
     * identical to the file ending
     * @param defaultImages the default images with their regex expressions
     * @throws IconDatabaseException if an internal error occurs
     */
    public IconDatabase(Path programdir, byte[] defaultContent,
            String defaultType,
            Set<Pair<String, Pair<byte[], String>>> defaultImages)
            throws IconDatabaseException {
        Icon.setIconDatabase(this);
        this.typeDirectory = programdir.resolve(IconDatabase.TYPE_DIRECTORY); // $NON-NLS-1$
        if (!Files.exists(this.typeDirectory)) {
            try {
                Files.createDirectory(this.typeDirectory);
            } catch (IOException e) {
                throw new IconDatabaseException(e);
            }
        }
        this.setDefaultIcon(defaultContent, defaultType);
        for (var x : defaultImages) {
            this.addDefaultIcon(x.getValue1().getValue0(), x.getValue0(),
                    x.getValue1().getValue1());
        }
    }

    private List<Icon> getAllByType(InstanceType type)
            throws IconDatabaseException {
        List<Icon> icons = new LinkedList<>();
        try (DirectoryStream<Path> stream = Files
                .newDirectoryStream(this.typeDirectory)) {
            Iterator<Path> it = stream.iterator();
            while (it.hasNext()) {
                Path current = it.next();
                if (current.getFileName().toString()
                        .startsWith(this.fileEncode(type.getIRI()))
                        && !FilenameUtils
                                .getExtension(current.getFileName().toString())
                                .equals("txt")) { //$NON-NLS-1$
                    icons.add(new Icon(current, type));
                }
            }
        } catch (IOException e) {
            throw new IconDatabaseException(e);
        }
        return icons;
    }

    private IconInterface setForTypeUnchecked(InstanceType type, Path path)
            throws IconDatabaseException {

        Path newPath = null;
        for (int i = 0; true; i++) {
            String typename = this.fileEncode(type.getIRI());
            String filename = typename + "_" + Integer.toString(i) //$NON-NLS-1$
                    + "." //$NON-NLS-1$
                    + FilenameUtils.getExtension(path.getFileName().toString());
            newPath = this.typeDirectory.resolve(filename);
            if (!Files.exists(newPath)) {
                try {
                    try {
                        ImageIO.read(path.toFile()).toString();
                    } catch (NullPointerException e) {
                        throw new IconDatabaseException(
                                "File is not a valid image");
                    }
                    Files.copy(path, newPath);
                    Icon icon = new Icon(newPath, type);
                    return icon;
                } catch (IOException e) {
                    throw new IconDatabaseException(e);
                }
            }
        }

    }

    @Override
    public void setDefaultForType(IconInterface icon)
            throws IconDatabaseException {
        Path oldpath = icon.getPath();
        String newStr = FilenameUtils
                .removeExtension(oldpath.getFileName().toString()).split("_")[0] //$NON-NLS-1$
                + "_" //$NON-NLS-1$
                + IconDatabase.STANDARD_INFIX + "." //$NON-NLS-1$
                + FilenameUtils.getExtension(oldpath.getFileName().toString());
        Path newpath = oldpath.getParent().resolve(newStr);
        try {
            Files.deleteIfExists(newpath);
            Files.copy(oldpath, newpath);
        } catch (IOException e) {
            throw new IconDatabaseException(e);
        }

    }

    /**
     * Imports an external (e.g. serialized) icon in the database
     *
     * @param icon the icon to import
     * @return the path to the imported icon
     * @throws IconDatabaseException if an internal error occurs
     */
    Path importIcon(Icon icon) throws IconDatabaseException {
        for (IconInterface x : this.getByType(icon.getInstanceType())) {
            if (x.equals(icon)) {
                return x.getPath();
            }
        }
        Path newPath = null;
        boolean exit = false;
        for (int i = 0; !exit; i++) {
            String typename = this.fileEncode(icon.getInstanceType().getIRI());
            String filename = typename + "_" + Integer.toString(i) //$NON-NLS-1$
                    + "." + FilenameUtils.getExtension(icon.getFileType()); //$NON-NLS-1$
            newPath = this.typeDirectory.resolve(filename);
            if (!Files.exists(newPath)) {
                exit = true;
            }
        }
        try {
            Files.createFile(newPath);
            Files.write(newPath, icon.getContent());
        } catch (IOException e) {
            throw new IconDatabaseException(e);
        }
        return newPath;

    }

    /**
     * Encodes a name so that it can be used as a filename.
     *
     * @param name the name
     * @return the resulting string
     * @throws IconDatabaseException
     */
    private String fileEncode(String name) throws IconDatabaseException {
        byte[] raw = StandardCharsets.UTF_8.encode(name).array();
        String result = ""; //$NON-NLS-1$
        for (byte x : raw) {
            String hex = Integer.toHexString(x);
            if (hex.length() == 1) {
                hex = "0" + hex; //$NON-NLS-1$
            }
            result = result.concat(hex);
        }
        for (int i = 0; true; i++) {
            Path newPath = this.typeDirectory.resolve(i + ".txt"); //$NON-NLS-1$
            if (!Files.exists(newPath)) {
                try {
                    Files.write(newPath,
                            result.getBytes(Charset.defaultCharset()));
                } catch (IOException e) {
                    throw new IconDatabaseException(e);
                }
                return Integer.toString(i);

            }
            try {
                if (Files.readString(newPath).equals(result)) {
                    return Integer.toString(i);
                }
            } catch (IOException e1) {
                throw new IconDatabaseException(e1);
            }
        }
    }

    @Override
    public IconInterface getDefaultIcon() throws IconDatabaseException {
        try (DirectoryStream<Path> stream = Files
                .newDirectoryStream(this.typeDirectory)) {
            Iterator<Path> it = stream.iterator();
            while (it.hasNext()) {
                Path current = it.next();
                if (current.getFileName().toString()
                        .startsWith(IconDatabase.GENERAL_DEFAULT)) { // $NON-NLS-1$
                    return new Icon(current);
                }
            }
        } catch (IOException e) {
            throw new IconDatabaseException(e);
        }
        return null;
    }

    private void setDefaultIcon(byte[] content, String type)
            throws IconDatabaseException {
        Path newPath = this.typeDirectory.resolve("zdefault." + type); //$NON-NLS-1$
        try {
            Files.write(newPath, content);
        } catch (IOException e) {
            throw new IconDatabaseException(e);
        }
    }

    @Override
    public void setDefaultIcon(Path path) throws IconDatabaseException {
        try {
            this.setDefaultIcon(Files.readAllBytes(path),
                    FilenameUtils.getExtension(path.getFileName().toString()));
        } catch (IOException e) {
            throw new IconDatabaseException(e);
        }
    }

    @Override
    public IconInterface getDefaultIcon(InstanceType type)
            throws IconDatabaseException {
        // try getting default icon for type
        for (IconInterface x : this.getAllByType(type)) {
            if (x.getPath().getFileName().toString()
                    .contains(IconDatabase.STANDARD_INFIX)) {
                return x;
            }
        }
        // try getting default icon which string matches the IRI
        try (DirectoryStream<Path> stream = Files
                .newDirectoryStream(this.typeDirectory)) {
            Iterator<Path> it = stream.iterator();
            while (it.hasNext()) {
                Path current = it.next();
                if (current.getFileName().toString()
                        .startsWith(IconDatabase.DEFTYPE_PREFIX)) { // $NON-NLS-1$
                    String[] parts = FilenameUtils
                            .removeExtension(current.getFileName().toString())
                            .split("_"); //$NON-NLS-1$
                    if (type.getIRI().toLowerCase()
                            .matches(".*#" + parts[1].toLowerCase())) { // $NON-NLS-1$
                        // //$NON-NLS-2$
                        IconInterface ico = this.setForType(type, current);
                        this.setDefaultForType(ico);
                        return ico;
                    }
                }
            }
        } catch (IOException e) {
            throw new IconDatabaseException(e);
        }
        // if not, try getting general default icon
        try (DirectoryStream<Path> stream = Files
                .newDirectoryStream(this.typeDirectory)) {
            Iterator<Path> it = stream.iterator();
            while (it.hasNext()) {
                Path current = it.next();
                if (current.getFileName().toString()
                        .startsWith(IconDatabase.GENERAL_DEFAULT)) { // $NON-NLS-1$
                    IconInterface ico = this.setForType(type, current);
                    this.setDefaultForType(ico);
                    return ico;
                }
            }
        } catch (IOException e) {
            throw new IconDatabaseException(e);
        }
        return null;
    }

    private void addDefaultIcon(byte[] content, String matchStr,
            String filetype) throws IconDatabaseException {
        Path newPath = this.typeDirectory.resolve(
                IconDatabase.DEFTYPE_PREFIX + "_" + matchStr + "." + filetype); //$NON-NLS-1$ //$NON-NLS-2$
        try {
            Files.write(newPath, content);
        } catch (IOException e) {
            throw new IconDatabaseException(e);
        }
    }

    @Override
    public IconInterface setForType(InstanceType type, Path path)
            throws IconDatabaseException {
        byte[] content;
        try {
            content = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new IconDatabaseException(e);
        }
        List<Icon> list = this.getByType(type);
        for (Icon x : list) {
            if (Arrays.equals(x.getContent(), content)) {
                return x;
            }
        }
        return this.setForTypeUnchecked(type, path);
    }

    @SuppressWarnings("exports")
    @Override
    public List<Icon> getByType(InstanceType type)
            throws IconDatabaseException {
        List<Icon> result = new LinkedList<>();
        for (Icon x : this.getAllByType(type)) {
            if (!x.getPath().toString().contains(IconDatabase.STANDARD_INFIX)) {
                result.add(x);
            }
        }
        return result;
    }

}
