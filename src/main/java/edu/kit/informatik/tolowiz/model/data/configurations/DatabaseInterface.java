/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.configurations;

import java.util.List;

import edu.kit.informatik.tolowiz.model.ontology.Ontology;

/**
 * The full database of the program which contains the storages for all
 * ontologies.
 *
 * @author Tobias Klumpp
 *
 */
public interface DatabaseInterface {
    /**
     * Returns the storage for this ontology where all configurations for this
     * ontology are saved.
     *
     * @param onto the ontology the saved configurations in this storage belong to.
     * @return the storage for this ontology.
     * @throws InternalDatabaseException if an internal database error occurs
     */
    public StorageInterface createStorage(Ontology onto) throws InternalDatabaseException;

    /**
     * Gets the IDs of all storages
     *
     * @return the IDs
     * @throws InternalDatabaseException if an internal error occurs
     */
    public List<String> getStorageIDs() throws InternalDatabaseException;

    /**
     * Gets the names of all storages
     *
     * @return the names
     * @throws InternalDatabaseException if an internal error occurs
     */
    public List<String> getStorageNames() throws InternalDatabaseException;

    /**
     * Gets a specific storage by its ID
     *
     * @param id the ID
     * @return the storage
     * @throws InternalDatabaseException if an internal error occurs
     * @throws NoSuchEntryException      if the entry doesn't exist
     */
    public StorageInterface getStorageByID(String id) throws InternalDatabaseException, NoSuchEntryException;

    /**
     * Gets a specific storage by its name
     *
     * @param name the name
     * @return the storage
     * @throws InternalDatabaseException if an internal error occurs
     * @throws NoSuchEntryException      if the entry doesn't exist
     */
    public StorageInterface getStorageByName(String name) throws NoSuchEntryException, InternalDatabaseException;
}
