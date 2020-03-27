/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

/**
 * This interface provides the means to identify objects that wish to be
 * informed of changes to a {@link Configuration}. Specifically, calls to this
 * interface mean that a great part of the Configuration has changed and the
 * visualization will have to be rebuild from scratch instead of updating a few
 * changed Instances.
 *
 * @author Fabian Palitza
 * @version 1.0
 * @see Configuration
 */
public interface ConfigurationListenerInterface {

    /**
     * Called when the Configuration of the ontology has changed so much that it
     * should be rebuild instead of updated.
     *
     * @see Configuration
     * @see Configuration#addListener(ConfigurationListenerInterface)
     * @see Configuration#changed()
     */
    public void onFullChange();
}
