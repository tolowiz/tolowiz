/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.gui;

import java.nio.file.Path;
import java.util.Set;

/**
 * Class for actions which affect the whole application. Only one of this
 * objects should exist in one Application.
 *
 * @author Tobias Klumpp
 *
 */
public interface ApplicationControllerInterface {

    /**
     * Exits the program.
     */
    public void exitProgram();

    /**
     * Open a new ontology and therefore a new tab.
     *
     * @param path the path to the ontology file
     */
    public void openOntology(Path path);

    /**
     * Imports a configuration
     *
     * @param path the path to the configuration
     */
    public void importConfiguration(Path path);

    /**
     * Loads an internally saved configuration
     *
     * @param id   the id of the ontology the configuration belongs to
     * @param name the name of the configuration
     */
    public void loadConfiguration(String id, String name);

    /**
     * Loads the autosaved configuration.
     *
     * @param id the id of the ontology
     */
    public void loadAutosave(String id);

    // No factory method as controller sets instances to view itself

    /**
     * Selects a single URI from the set
     * @param uris The set to pick from
     * @return The URI to use
     */
    public String selectURI(Set<String> uris);

    /**
     * Sets the URI prefix to use.
     * @param value The value to set
     */
    public void setUriPrefix(String value);
}
