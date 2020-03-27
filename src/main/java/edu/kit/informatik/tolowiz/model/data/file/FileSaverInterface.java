/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.file;

import java.nio.file.Path;

/**
 * Saves an ontology configuration as a file.
 *
 * @author Tobias Klumpp
 *
 */
public interface FileSaverInterface {

    /**
     * Generates a file object specifying a specific file. Access to the file system
     * itself will only happen if methods of this file are called. The path to the
     * file is given as a Java path object.
     *
     * @param path the path to the file
     * @return the file object
     */
    public abstract ConfigurationFileInterface generateFile(Path path);
}
