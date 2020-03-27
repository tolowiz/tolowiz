/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.file;

import java.nio.file.Path;

/**
 * Default implementation for filesaver interface using Java serialization API
 *
 * @author Tobias Klumpp
 *
 */
public class FileSaver implements FileSaverInterface {

    /**
     * Default constructor for FileSaver.
     */
    public FileSaver() {
    }

    @Override
    public ConfigurationFileInterface generateFile(Path path) {
        return new ConfigurationFile(path);
    }

}
