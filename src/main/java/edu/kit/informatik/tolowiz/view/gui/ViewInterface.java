/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.gui;

import java.util.Set;

import edu.kit.informatik.tolowiz.model.visualization.Configuration;
import edu.kit.informatik.tolowiz.view.graph.GraphControllerInterface;

/**
 * Interface for the full view to be used by the controller.
 *
 * @author Tobias Klumpp
 *
 */
public interface ViewInterface {

    /**
     * Opens a new tab.
     *
     * @param controller the control who should control this tab
     * @param conf       the current configuration
     * @param graphCont  the controller for the graph
     * @param tabName    the name of the tab
     * @return the tab as a TabInterface
     */
    public abstract TabInterface createTab(TabControllerInterface controller, Configuration conf,
            GraphControllerInterface graphCont, String tabName);

    /**
     * Shows an error caused by an exception.
     *
     * @param e the exception to be displayed
     */
    public void showError(Exception e);

    /**
     * Closes all windows and exits the program. This method should only be called
     * by the controller to ensure autosave.
     */
    public void closeApplication();

    /**
     * Runs it in the gui thread.
     *
     * @param run The runnable execution.
     */
    public abstract void runLater(Runnable run);

    /**
     * Displays a selector to choose one URI for an ontology.
     * @param uris the uris to use
     */
    public abstract void selectUri(Set<String> uris);

}
