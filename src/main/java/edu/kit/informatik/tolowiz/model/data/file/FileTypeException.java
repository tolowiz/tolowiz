/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.file;

/**
 * Exception indicating that a file has a wrong format or is corrupted.
 *
 * @author Tobias Klumpp
 *
 */
public class FileTypeException extends Exception {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new FileTypeException
     *
     * @param cause the original exception thrown by the IO method
     */
    FileTypeException(Exception cause) {
        super("not a valid configuration file", cause);
    }

}
