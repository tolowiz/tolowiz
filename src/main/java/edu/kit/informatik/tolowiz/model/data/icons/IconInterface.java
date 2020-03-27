/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.icons;

import java.io.Serializable;
import java.nio.file.Path;

/**
 * Class representing an Icon which can be used as a symbol for a specific type
 * of node. Every icon is bound to a specific instance type
 *
 * @author Tobias Klumpp
 *
 */
public interface IconInterface extends Serializable {

    /**
     * Returns the icon path as a Java path object.
     *
     * @return the path
     * @throws IconDatabaseException if an error occurs
     */
    public Path getPath() throws IconDatabaseException;
}
