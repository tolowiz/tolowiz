/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.file;

import java.io.IOException;

import edu.kit.informatik.tolowiz.model.data.icons.IconDatabaseException;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;

/**
 * Class representing a file
 *
 * @author Tobias Klumpp
 *
 */
public interface ConfigurationFileInterface {
    /**
     * Export a configuration to the file. If the file already exists, this method
     * will just overwrite it without any warning.
     *
     * @param conf the configuration to be exported
     * @throws IOException           if an IO error occurs
     * @throws IconDatabaseException if an icon database error occurs
     */
    public void exportConfiguration(Configuration conf) throws IOException, IconDatabaseException;

    /**
     * Imports a configuration from the file
     *
     * @return the configuration
     * @throws FileTypeException if the file has a wrong format
     * @throws IOException       if an IO error occurs
     */
    public Configuration importConfiguration() throws FileTypeException, IOException;
}
