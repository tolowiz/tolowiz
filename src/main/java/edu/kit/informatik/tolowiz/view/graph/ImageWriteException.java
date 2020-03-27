/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.graph;

/**
 * Exception signalling that exporting an image failed.
 *
 * @author Tobias Klumpp
 *
 */
public class ImageWriteException extends Exception {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception
     *
     * @param cause the original exception thrown by the IO method
     */
    ImageWriteException(Exception cause) {
        super(cause);
    }
}
