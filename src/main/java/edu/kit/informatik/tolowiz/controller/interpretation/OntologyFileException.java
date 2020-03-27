/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.controller.interpretation;

/**
 * This Exception should be thrown if an Ontology File that is to be interpreted
 * can not be read. E.g. if the file in not encoded with RDF/XML syntax.
 *
 * @author Anne
 *
 */
public class OntologyFileException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception
     *
     * @param message the message to be displayed
     * @param cause   the causing exception
     */
    public OntologyFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
