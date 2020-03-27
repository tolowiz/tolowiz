/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.gui;

import java.nio.file.Path;

import edu.kit.informatik.tolowiz.model.visualization.Configuration;
import edu.kit.informatik.tolowiz.view.graph.GraphInterface;

/**
 * Interface representing a tab. This should only be used by the controller.
 * View components should call a tab directly.
 *
 * @author Tobias Klumpp
 *
 */
public interface TabInterface {
    /**
     * Closes the tab.
     */
    public void closeTab();

    /**
     * This Method returns the corresponding graph to a tab. Every tab has exactly
     * one graph connected to it.
     *
     * @return Returns the graph connected to this tab.
     */
    public GraphInterface getGraph();

    /**
     * Shows an error caused by an exception.
     *
     * @param e the exception to show
     */
    public void showError(Exception e);

    /**
     * Asks if configuration can be overwritten.
     *
     * @param configurationFile the file to possibly overwrite
     *
     * @return if configuration file can be overwritten
     */
    public boolean askForOverwrite(Path configurationFile);

    /**
     * Shows a new configuration in this tab
     *
     * @param conf the configuration to show
     */
    public void showGraph(Configuration conf);
}
