/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.icons;

import java.nio.file.Path;
import java.util.List;

import edu.kit.informatik.tolowiz.model.ontology.InstanceType;

/**
 * Class used for creating icons.
 *
 * @author Tobias Klumpp
 *
 */
public interface IconDatabaseInterface {
    /**
     * Returns the icons for a type
     *
     * @param type the type
     * @return the icons
     * @throws IconDatabaseException if an internal database error occurs
     */
    public List<? extends IconInterface> getByType(InstanceType type) throws IconDatabaseException;

    /**
     * Set an icon as icon for a specified type.
     *
     * @param type the type for which the icon should be set
     * @param path the path to the icon
     * @return the icon set
     * @throws IconDatabaseException if an internal database error occurs
     */
    public IconInterface setForType(InstanceType type, Path path) throws IconDatabaseException;

    /**
     * Returns the general default icon
     *
     * @return the default icon
     * @throws IconDatabaseException if an internal error occurs
     */
    @Deprecated
    public IconInterface getDefaultIcon() throws IconDatabaseException;

    /**
     * Returns the default icon for a specific type
     *
     * @param type the type for whioch the default icon should be returned
     * @return the default icon
     * @throws IconDatabaseException if an internal error occurs
     */
    public IconInterface getDefaultIcon(InstanceType type) throws IconDatabaseException;

    /**
     * Sets the general default icon
     *
     * @param path the default icon path
     * @throws IconDatabaseException if an internal error occurs
     */
    public void setDefaultIcon(Path path) throws IconDatabaseException;

    /**
     * @param icon the icon to set as default icon for its type
     * @throws IconDatabaseException if an internal error occurs
     */
    public void setDefaultForType(IconInterface icon) throws IconDatabaseException;
}
