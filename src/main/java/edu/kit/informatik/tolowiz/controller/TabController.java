/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedList;

import org.apache.commons.io.FilenameUtils;

import edu.kit.informatik.tolowiz.model.data.configurations.DatabaseInterface;
import edu.kit.informatik.tolowiz.model.data.configurations.InternalDatabaseException;
import edu.kit.informatik.tolowiz.model.data.configurations.NoSuchEntryException;
import edu.kit.informatik.tolowiz.model.data.configurations.StorageInterface;
import edu.kit.informatik.tolowiz.model.data.file.FileSaverInterface;
import edu.kit.informatik.tolowiz.model.data.file.FileTypeException;
import edu.kit.informatik.tolowiz.model.data.icons.IconDatabaseException;
import edu.kit.informatik.tolowiz.model.data.icons.IconDatabaseInterface;
import edu.kit.informatik.tolowiz.model.data.icons.IconInterface;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;
import edu.kit.informatik.tolowiz.model.visualization.InstanceTypeConfiguration;
import edu.kit.informatik.tolowiz.view.graph.ImageInterface;
import edu.kit.informatik.tolowiz.view.graph.ImageWriteException;
import edu.kit.informatik.tolowiz.view.gui.TabControllerInterface;
import edu.kit.informatik.tolowiz.view.gui.TabInterface;
import edu.kit.informatik.tolowiz.view.gui.ViewInterface;

/**
 * Controller for operations involving only one tab. This class has neither a
 * public constructor nor factory methods in other classes because instances of
 * this class are created and given directly to their responding tabs by the
 * {@link ApplicationController}.
 *
 * @author Tobias Klumpp
 *
 */

class TabController implements TabControllerInterface {
    /**
     * Dequeue for stroring undo operations
     */
    private Deque<Configuration> undoOperations = new LinkedList<>();
    /**
     * Dequeue for storing redo operations
     */
    private Deque<Configuration> redoOperations = new LinkedList<>();
    /**
     * The current configuration
     */
    private Configuration current;
    /**
     * The tab this controller is associated with.
     */
    private TabInterface tab;
    /**
     * The storage where the configurations are stored.
     */
    private StorageInterface st;
    /**
     * The class for exporting files
     */
    private final FileSaverInterface filesaver;
    /**
     * The graph controller of the graph belonging to this tab.
     */
    private final GraphController graphCont;
    private final ApplicationController appController;

    /**
     * Creates a tab controller object, which is associated with a specific tab.
     *
     * @param appCont       the application controller of the application this
     *                      tabcontroller belongs to
     * @param view          the view the tab should be displayed in
     * @param filesaver     the filesaver that should be used for saving files
     * @param database      the database to store the configuration
     * @param buildOntology the ontology to be displayed in this tab
     * @param icons         the icon database to be used in this tab
     * @param name          the name of the tab
     */
    TabController(ApplicationController appCont, ViewInterface view, FileSaverInterface filesaver,
            DatabaseInterface database, IconDatabaseInterface icons, Ontology buildOntology, String name) {
        this(appCont, view, filesaver, database, icons, new Configuration(buildOntology, icons), name);
        this.tab.getGraph().autoLayout();
        TabController.this.tab.getGraph()
        .fixLayout(buildOntology.getNumberOfRelations() + buildOntology.getNumberOfInstances());
    }

    /**
     * Creates a tab controller object, which is associated with a specific tab with
     * a predefined configuration.
     *
     * @param appCont   the application controller of the application this
     *                  tabcontroller belongs to
     *
     * @param view      the view the tab should be displayed in
     * @param filesaver the filesaver that should be used for saving files
     * @param database  the database to store the configuration
     * @param conf      the configuration to be used
     * @param icons     the icon database to be used in this tab
     * @param name      the name of the tab
     */
    TabController(ApplicationController appCont, ViewInterface view, FileSaverInterface filesaver,
            DatabaseInterface database, IconDatabaseInterface icons, Configuration conf, String name) {
        this.appController = appCont;
        this.filesaver = filesaver;
        this.current = conf;
        this.graphCont = new GraphController(this);
        this.tab = view.createTab(this, this.current, this.graphCont, name);
        try {
            this.st = database.createStorage(conf.getOntology());
        } catch (InternalDatabaseException e) {
            this.appController.showError(e);
        }
    }

    @Override
    public void closeOntology() {
        try {
            this.st.autosave(this.current);
            this.tab.closeTab();
        } catch (InternalDatabaseException e) {
            this.tab.showError(e);
        }

    }

    /**
     * Must be called before every change to the configuration to allow undo and
     * redo.
     */
    void doOperation() {
        this.undoOperations.addFirst(this.current.clone());
        // this.undoOperations.addFirst(this.current);
        this.redoOperations = new LinkedList<>();
        assert (this.undoOperations.getFirst() != null);
    }

    @Override
    public void exportConfiguration(Path configurationFile, boolean overwrite) {
        Path configurationFile2 = configurationFile;
        if (!FilenameUtils.isExtension(configurationFile.toString(), "tolo")) { //$NON-NLS-1$
            configurationFile2 = configurationFile.getParent()
                    .resolve(configurationFile.getFileName().toString() + ".tolo"); //$NON-NLS-1$
        }
        try {
            if (Files.exists(configurationFile2) && !overwrite) {
                if (this.tab.askForOverwrite(configurationFile2)) {
                    this.filesaver.generateFile(configurationFile2).exportConfiguration(this.current);
                }
            } else {
                this.filesaver.generateFile(configurationFile2).exportConfiguration(this.current);
            }
        } catch (IOException | IconDatabaseException e) {
            this.tab.showError(e);
        }
    }

    @Override
    public void exportVisualizationDocument(Path file) {
        Path file2 = file;
        if (!FilenameUtils.isExtension(file.toString(), "pdf")) { //$NON-NLS-1$
            file2 = file.getParent().resolve(file.getFileName().toString() + ".pdf"); //$NON-NLS-1$
        }
        ImageInterface image = this.tab.getGraph().getImage();
        try {
            image.exportPDF(file2);
        } catch (ImageWriteException e) {
            this.tab.showError(e);
        }
    }

    @Override
    public void exportVisualizationImage(Path file, double resolution, ImageFiletype type) {
        Path file2 = file;
        if (!FilenameUtils.isExtension(file.toString(), type.toString().toLowerCase())) { // $NON-NLS-1$
            file2 = file.getParent().resolve(file.getFileName().toString()
                    + "." + type.toString().toLowerCase()); //$NON-NLS-1$
        }
        ImageInterface image = this.tab.getGraph().getImage(resolution);
        try {
            assert (image != null);
            image.export(file2, type);
        } catch (ImageWriteException e) {
            this.tab.showError(e);
        }
    }

    @Override
    public void exportVisualizationImage(Path file, ImageFiletype type) {
        Path file2 = file;
        if (!FilenameUtils.isExtension(file.toString(), type.toString().toLowerCase())) { // $NON-NLS-1$
            file2 = file.getParent().resolve(file.getFileName().toString()
                    + "." + type.toString().toLowerCase()); //$NON-NLS-1$
        }
        ImageInterface image = this.tab.getGraph().getImage();
        try {
            assert (image != null);
            image.export(file2, type);
        } catch (ImageWriteException e) {
            this.tab.showError(e);
        }
    }

    @Override
    public void exportVisualizationVector(Path file) {
        Path file2 = file;
        if (!FilenameUtils.isExtension(file.toString(), "svg")) { //$NON-NLS-1$
            file2 = file.getParent().resolve(file.getFileName().toString() + ".svg"); //$NON-NLS-1$
        }
        ImageInterface image = this.tab.getGraph().getImage();
        try {
            image.exportSVG(file2);
        } catch (ImageWriteException e) {
            this.tab.showError(e);
        }
    }

    @Override
    @Deprecated
    public void importConfiguration(Path configurationFile) {
        try {
            Configuration conf = this.filesaver.generateFile(configurationFile).importConfiguration();
            if (!conf.getOntology().getIRI().equals(this.current.getOntology().getIRI())) {
                this.tab.showError(new WrongOntologyException());
            } else {
                this.current = conf;
                this.undoOperations = new LinkedList<>();
                this.redoOperations = new LinkedList<>();
                this.tab.showGraph(conf);
            }
        } catch (FileTypeException | IOException e) {
            this.tab.showError(e);
        }

    }

    @Override
    @Deprecated
    public void loadAutosaveConfiguration() {
        try {
            this.current = this.st.getAutosaveEntry();
            this.undoOperations = new LinkedList<>();
            this.redoOperations = new LinkedList<>();
            this.tab.showGraph(this.current);
        } catch (NoSuchEntryException | InternalDatabaseException e) {
            this.tab.showError(e);
        }
    }

    @Override
    @Deprecated
    public void loadConfiguration(String name) {
        try {
            this.current = this.st.getEntry(name);
            this.undoOperations = new LinkedList<>();
            this.redoOperations = new LinkedList<>();
            this.tab.showGraph(this.current);
        } catch (NoSuchEntryException | InternalDatabaseException e) {
            this.tab.showError(e);
        }
    }

    @Override
    public void printOntology() {
        ImageInterface image = this.tab.getGraph().getImage();
        try {
            image.print();
        } catch (ImageWriteException e) {
            this.tab.showError(e);
        }
    }

    @Override
    public void redo() throws UndoException {
        if (!this.isRedoPossible()) {
            throw new UndoException();
        }
        this.undoOperations.addFirst(this.current);
        this.current = this.redoOperations.removeFirst();
        this.tab.showGraph(this.current);
        assert (this.current != null);
    }

    @Override
    public void saveConfiguration(String name) {
        try {
            this.st.addEntry(name, this.current);
        } catch (InternalDatabaseException e) {
            this.tab.showError(e);
        }
    }

    @Override
    public void undo() throws UndoException {
        if (!this.isUndoPossible()) {
            throw new UndoException();
        }
        this.redoOperations.addFirst(this.current);
        this.current = this.undoOperations.removeFirst();
        this.tab.showGraph(this.current);
        assert (this.current != null);

    }

    @Override
    public Configuration getConfig() {
        return this.current;
    }

    @Override
    public boolean isUndoPossible() {
        return !this.undoOperations.isEmpty();
    }

    @Override
    public boolean isRedoPossible() {
        return !this.redoOperations.isEmpty();
    }

    /**
     * Returns the default icon for a specified instance type
     *
     * @param t the instance type to return the default icon for
     * @return the icon
     */
    IconInterface getDefaultIcon(InstanceTypeConfiguration t) {
        return this.appController.getDefaultIcon(t);
    }

}
