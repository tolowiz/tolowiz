/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.controller.interpretation;

import java.nio.file.Path;

import edu.kit.informatik.tolowiz.view.gui.ApplicationControllerInterface;

/**
 * Creates an RDF interpreter
 *
 * @author Tobias Klumpp
 *
 */
public class RDFInterpreterFactory extends InterpreterFactory {

    @Override
    public InterpreterInterface getInterpreter(Path filepath, ApplicationControllerInterface applicationController) {
        return new Interpreter(filepath, applicationController);
    }

}
