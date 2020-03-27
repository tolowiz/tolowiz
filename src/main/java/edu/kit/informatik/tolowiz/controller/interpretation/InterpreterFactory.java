/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.controller.interpretation;

import java.nio.file.Path;

import edu.kit.informatik.tolowiz.view.gui.ApplicationControllerInterface;

/**
 * Creates
 *
 * @author Tobias Klumpp
 *
 */
public abstract class InterpreterFactory {
    /**
     * Creates an interpreter
     *
     * @param filepath              the path to the ontology
     * @param applicationController the application controller which will be used
     * @return the interpreter
     */
    public abstract InterpreterInterface getInterpreter(Path filepath,
            ApplicationControllerInterface applicationController);
}
