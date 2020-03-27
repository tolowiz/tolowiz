/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.controller;

import edu.kit.informatik.tolowiz.controller.interpretation.InterpreterFactory;
import edu.kit.informatik.tolowiz.controller.interpretation.OntologyFileException;
import edu.kit.informatik.tolowiz.model.data.configurations.DatabaseInterface;
import edu.kit.informatik.tolowiz.model.data.configurations.InternalDatabaseException;
import edu.kit.informatik.tolowiz.model.data.configurations.NoSuchEntryException;
import edu.kit.informatik.tolowiz.model.data.file.FileSaverInterface;
import edu.kit.informatik.tolowiz.model.data.file.FileTypeException;
import edu.kit.informatik.tolowiz.model.data.icons.IconDatabaseException;
import edu.kit.informatik.tolowiz.model.data.icons.IconDatabaseInterface;
import edu.kit.informatik.tolowiz.model.data.icons.IconInterface;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;
import edu.kit.informatik.tolowiz.model.visualization.InstanceTypeConfiguration;
import edu.kit.informatik.tolowiz.view.gui.ApplicationControllerInterface;
import edu.kit.informatik.tolowiz.view.gui.TabControllerInterface;
import edu.kit.informatik.tolowiz.view.gui.ViewInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

/**
 * Controller for application-wide actions. This controller is called by the
 * GUI. For every tab, a tab controller is created by this controller.
 *
 * @author Tobias Klumpp
 *
 */
public class ApplicationController implements ApplicationControllerInterface {
    /**
     * The view this controller controls.
     */
    private final ViewInterface view;
    /**
     * The filesaver used for exporting configurations.
     */
    private final FileSaverInterface filesaver;
    /**
     * The database used for saving configurations internally.
     */
    private final DatabaseInterface database;
    /**
     * The interpreter used for interpreting an ontology file.
     */
    private final InterpreterFactory interpreter;
    /**
     * Used for storing icons for types and instances.
     */
    private final IconDatabaseInterface icons;

    /**
     * List containing all tabs controllers of this application.
     */
    private List<TabController> tabs = new LinkedList<>();
    private boolean uriLocked;
    private String uriPrefix;

    /**
     * Creates a default application controller
     *
     * @param view the associated with the application controller
     * @param filesaver the class used for saving configurations as files
     * @param database the database associated with the application controller
     * @param interpreter the interpreter to be used to interpret the ontology
     * @param icons the icon database to be used to store icons
     */
    public ApplicationController(ViewInterface view,
            FileSaverInterface filesaver, DatabaseInterface database,
            InterpreterFactory interpreter, IconDatabaseInterface icons) {
        this.icons = icons;
        this.view = view;
        this.filesaver = filesaver;
        this.database = database;
        this.interpreter = interpreter;
    }

    @Override
    public void exitProgram() {
        Iterator<TabController> i = this.tabs.iterator();
        while (i.hasNext()) {
            TabController tc = i.next();
            tc.closeOntology();
            i.remove();
        }

        this.view.closeApplication();
    }

    @Override
    public void openOntology(Path path) {
        try {
            Ontology onto = this.interpreter.getInterpreter(path, this)
                    .buildOntology();
            this.runLater(() -> this.tabs.add(new TabController(this, this.view,
                    this.filesaver, this.database, this.icons, onto,
                    FilenameUtils
                    .removeExtension(path.getFileName().toString()))));
        } catch (FileNotFoundException e) {
            this.view.showError(e);
        } catch (OntologyFileException e) {
            this.view.showError(e);
        }
    }

    @Override
    public void importConfiguration(Path path) {
        try {
            Configuration conf = this.filesaver.generateFile(path)
                    .importConfiguration();
            this.runLater(() -> this.tabs.add(new TabController(this, this.view,
                    this.filesaver, this.database, this.icons, conf,
                    FilenameUtils
                    .removeExtension(path.getFileName().toString()))));
        } catch (NoSuchFileException | FileNotFoundException e) {
            this.view.showError(e);
        } catch (FileTypeException e) {
            this.view.showError(e);
        } catch (IOException e) {
            this.view.showError(e);
        }
    }

    @Override
    public void loadConfiguration(String id, String name) {
        try {
            Configuration conf = this.database.getStorageByID(id)
                    .getEntry(name);
            this.runLater(() -> this.tabs.add(new TabController(this, this.view,
                    this.filesaver, this.database, this.icons, conf, name)));
        } catch (NoSuchEntryException | InternalDatabaseException e) {
            this.view.showError(e);
        }
    }

    @Override
    public void loadAutosave(String id) {
        try {
            Configuration conf = this.database.getStorageByID(id)
                    .getAutosaveEntry();
            String name = this.database.getStorageByID(id).getOntologyName();
            this.runLater(() -> this.tabs.add(new TabController(this, this.view,
                    this.filesaver, this.database, this.icons, conf, name)));
        } catch (NoSuchEntryException | InternalDatabaseException e) {
            this.view.showError(e);
        }
    }

    /**
     * Removes a tab controller from the controller when a tab is deleted.
     *
     * @param tabController the tab controller to remove
     */
    void removeTab(TabControllerInterface tabController) {
        this.tabs.remove(tabController);

    }

    /**
     * Returns the default icon for a specified type
     *
     * @param t the InstanceType to get the default icon for
     * @return the icon
     */
    IconInterface getDefaultIcon(InstanceTypeConfiguration t) {
        try {
            return this.icons.getDefaultIcon(t.getInstanceType());
        } catch (IconDatabaseException e) {
            this.view.showError(e);
            return null;
        }

    }

    /**
     * Runs lambda in gui thread.
     *
     * @param run The runnable to execute.
     */
    void runLater(Runnable run) {
        this.view.runLater(run);

    }

    /**
     * Shows error in the gui.
     *
     * @param e The exception in the data base.
     */
    void showError(InternalDatabaseException e) {
        this.view.showError(e);

    }

    @Override
    public synchronized String selectURI(Set<String> uris) {
        this.uriLocked = true;
        this.runLater(() -> this.view.selectUri(uris));
        while (this.uriLocked) {
            try {
                this.wait();
            } catch (@SuppressWarnings("unused") InterruptedException e) {
                // do nothing
            }
        }
        return this.uriPrefix;

    }

    @Override
    public synchronized void setUriPrefix(String value) {
        this.uriPrefix = value;
        this.uriLocked = false;
        this.notifyAll();

    }

}
