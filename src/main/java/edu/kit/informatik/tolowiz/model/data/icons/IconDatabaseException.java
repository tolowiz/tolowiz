/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.icons;

/**
 * Exception indicating that an internal database error occured.
 *
 * @author Tobias Klumpp
 *
 */
public class IconDatabaseException extends Exception {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception
     *
     * @param cause the original exception thrown by the IO method
     */
    IconDatabaseException(Exception cause) {
        super(cause);
    }

    /**
     * Creates a new exception with the given message
     * @param string the message describing the cause
     */
    public IconDatabaseException(String string) {
        super(string);
    }

}
