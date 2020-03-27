/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.graph;

/**
 * Interface for the graph that visualizes the ontology. <br>
 * Is used to communicate with the controller.
 *
 * @author Anja
 * @version 1.0
 */
public interface GraphInterface {

    /**
     * Takes an image of the displayed graph.
     *
     * @param resolution of the generated image
     * @return the image
     */
    public ImageInterface getImage(double resolution);

    /**
     * Takes an image of the displayed graph.
     *
     * @return the image
     */
    public ImageInterface getImage();

    /**
     * Fixes (traces) the layout to the model
     *
     * @param size the size of the ontology to determine waiting time
     */
    public void fixLayout(long size);

    /**
     * Enables GraphStream's autolayout.
     */
    public void autoLayout();

}
