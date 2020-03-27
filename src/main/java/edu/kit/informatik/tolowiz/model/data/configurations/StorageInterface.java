/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.data.configurations;

import edu.kit.informatik.tolowiz.model.visualization.Configuration;

import java.util.List;

/**
 * Represents a database where configurations can be stored to and loaded from.
 * A single storage is bound to one Ontology.
 *
 * @author Tobias Klumpp
 *
 */
public interface StorageInterface {

    /**
     * Saves a configuration and a name.
     *
     * @param name the name under which the configuration is saved
     * @param conf the configuration
     * @throws InternalDatabaseException if an internal database error occurs
     */
    public void addEntry(String name, Configuration conf)
            throws InternalDatabaseException;

    /**
     * Autosaves a configuration so it is loaded at next startup.
     *
     * @param conf the configuration
     * @throws InternalDatabaseException if an internal database error occurs
     */
    public void autosave(Configuration conf) throws InternalDatabaseException;

    /**
     * Deletes a configuration from the list of saved entries.
     *
     * @param name the name of the entry to be deleted
     * @throws NoSuchEntryException if no entry with this name exists
     */
    public void deleteEntry(String name) throws NoSuchEntryException;

    /**
     * Loads the last autosaved entry for this ontology
     *
     * @return the autosaved conifguration
     * @throws NoSuchEntryException if no such entry exists
     * @throws InternalDatabaseException if an internal database error occurs
     */
    public Configuration getAutosaveEntry()
            throws NoSuchEntryException, InternalDatabaseException;

    /**
     * Loads a configuration entry by name.
     *
     * @param name the name
     * @return the configuration entry
     * @throws NoSuchEntryException if no entry with this name exists
     * @throws InternalDatabaseException if an internal database error occurs
     */
    public Configuration getEntry(String name)
            throws NoSuchEntryException, InternalDatabaseException;

    /**
     * Returns a list of all existing configurations in this storage.
     *
     * @return the list
     * @throws InternalDatabaseException if an internal error occurs in the
     * database
     */
    public List<String> getEntrys() throws InternalDatabaseException;

    /**
     * Renames a configuration entry to a new name
     *
     * @param old the old name of the entry
     * @param newName the new name of the entry
     * @throws NoSuchEntryException if no such entry exists
     */
    public void renameEntry(String old, String newName)
            throws NoSuchEntryException;

    /**
     * Returns the name of the ontology.
     *
     * @return the name
     * @throws InternalDatabaseException if an internal database error occurs
     */
    public String getOntologyName() throws InternalDatabaseException;

    /**
     * Tells if autosave entry exists.
     * 
     * @return true if an autosave entryh for this storage exists. false
     * otherwise.
     */
    public boolean existsAutosave();

}
