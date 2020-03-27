/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.controller.interpretation;

import java.io.FileNotFoundException;
import java.util.List;

import edu.kit.informatik.tolowiz.model.ontology.Ontology;

/**
 * This is the Interface to allow interpretation of ontology files. Ontologies
 * must be submitted in rdf format. The file will then be analyzed to create an
 * ontology object from it.
 *
 * @author Anne Bernhart
 *
 */
public interface InterpreterInterface {

    /**
     * Builds a new ontology from a rdf file.
     *
     * @return returns the newly built ontology.
     * @throws FileNotFoundException if the given file can not be found
     * @throws OntologyFileException if the given filepath doesn't refer to a valid
     *                               ontology file.
     */
    public Ontology buildOntology() throws FileNotFoundException, OntologyFileException;

    /**
     * returns all Error messages of Problems that have occured while reading and
     * processing the ontology given when constructing. The Messages describe what
     * went wrong and how this will influence the presentation of the ontology.
     *
     * @return the List of all Error messages.
     */
    public List<String> getErrorMessages();
}
