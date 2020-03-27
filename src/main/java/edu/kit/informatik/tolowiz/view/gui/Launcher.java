/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.gui;

/**
 * External launcher fallback in case App can not be started correctly by
 * JavaFX.
 *
 * @author Fabian
 *
 */
public class Launcher {

    /**
     * Launches the application.
     *
     * More specifically, calls the main method of
     * {@link edu.kit.informatik.tolowiz.view.gui.App} which will then build the
     * application.
     *
     * @param args command line arguments. They will be passed on to the
     *             application's main method.
     */
    public static void main(String[] args) {
        App.main(args);
    }
}
