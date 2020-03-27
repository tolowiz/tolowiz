/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.gui;

import edu.kit.informatik.tolowiz.model.data.icons.IconInterface;
import edu.kit.informatik.tolowiz.model.ontology.InstanceType;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;
import edu.kit.informatik.tolowiz.model.visualization.InstanceConfiguration;

/**
 * Interface that is used to communicate between the graph and the gui package,
 * as the graph package needs its own event handling class in order to interact
 * with the nodes of a graph directly. This interface passes information on
 * about which nodes are selected by the user.
 *
 * @author Anja, Sandra
 */
public interface JavaFxViewInterface extends ViewInterface {

    /**
     * Shows active values for a selected instance.
     *
     * @param instance The selected node.
     */
    public void showActivatedValues(InstanceConfiguration instance);

    /**
     * Is called when the user hides a node in the context menu of this node of the
     * graph and calls the hideInstance method in the GraphController.
     *
     * @param config The current configuration.
     */
    public void hideInstance(Configuration config);

    /**
     * Refreshes the full view according to the data in the model.
     */
    public void refresh();

    /**
     * Gets the default icon for a certain type.
     *
     * @param t The type.
     * @return the default icon.
     */
    public IconInterface getDefaultIcon(InstanceType t);

    @Override
    public void runLater(Runnable run);

    /**
     * @return whether the gui is in the presenting mode.
     */
    public boolean isInPresentingMode();

    /**
     * locks the gui
     */
    public void unlockGui();

    /**
     * unlocks the gui
     */
    public void lockGui();

}
