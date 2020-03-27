/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.gui;

import java.nio.file.Path;

import edu.kit.informatik.tolowiz.controller.ImageFiletype;
import edu.kit.informatik.tolowiz.controller.UndoException;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;

/**
 * Controller which performs operations for a specified tab. In the view, every
 * tab should have an associated tab controller object.
 *
 * @author Tobias Klumpp
 *
 */
public interface TabControllerInterface {
    /**
     * Exports the current visualization as a picture.
     *
     * @param file       the file the visualization will be saved to.
     * @param resolution of the file
     * @param type       the format in which the file will be saved.
     */
    public void exportVisualizationImage(Path file, double resolution, ImageFiletype type);

    /**
     * Exports the current visualization as a picture.
     *
     * @param file the file the visualization will be saved to.
     * @param type the format in which the file will be saved.
     */
    public void exportVisualizationImage(Path file, ImageFiletype type);

    /**
     * Exports the current visualization as a pdf document.
     *
     * @param file the file the visualization will be saved to.
     */
    public void exportVisualizationDocument(Path file);

    /**
     * Exports the current visualization as a vector graphics.
     *
     * @param file the file the visualization will be saved to.
     */
    public void exportVisualizationVector(Path file);

    /**
     * Closes the ontology and therefore the tab.
     */
    public void closeOntology();

    /**
     * Export the current configuration to a file.
     *
     * @param configurationFile the configuration file
     * @param overwrite         if the file should be overwritten
     */
    public void exportConfiguration(Path configurationFile, boolean overwrite);

    /**
     * Import a configuration from a file.
     *
     * @param configurationFile The configuration to import.
     */
    public void importConfiguration(Path configurationFile);

    /**
     * Load the configuration
     *
     * @param name The name of the configuration to load.
     */
    public void loadConfiguration(String name);

    /**
     * Print the current graph.
     */
    public void printOntology();

    /**
     * Says if undo is possible
     *
     * @return if undo is possible
     */
    public boolean isUndoPossible();

    /**
     * Says if redo is possible
     *
     * @return if redo is possible
     */
    public boolean isRedoPossible();

    /**
     * The last configuration change will be reversed and the graph is updated to be
     * shown again as it was before the last configuration change.
     *
     * @throws UndoException if Undo is not possible
     */
    public void undo() throws UndoException;

    /**
     * The last undo command will be reversed and the graph is updated to be shown
     * again as it was before the last undo command.
     *
     * @throws UndoException if redo is not possible
     */
    public void redo() throws UndoException;

    /**
     * Internally save the current configuration of the ontology.
     *
     * @param name the under which the configuration should be saved
     */
    public void saveConfiguration(String name);

    /**
     * Loads the autosaved configuration.
     *
     */
    public void loadAutosaveConfiguration();

    /**
     * Returns the current configuration.
     *
     * @return the configuration
     */
    public Configuration getConfig();
}
