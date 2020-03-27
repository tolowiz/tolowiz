/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.gui;

import java.nio.file.Path;

import edu.kit.informatik.tolowiz.model.visualization.Configuration;
import edu.kit.informatik.tolowiz.view.graph.Graph;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;

//package-modifier because controller only knows factory method in ViewInterface
/**
 * This class represents a specific tab in the GUI.
 *
 * @author Sandra Wolf
 *
 */
class ToloTab extends javafx.scene.control.Tab implements TabInterface {

    /**
     * The AnchorPane which represents the detail-area in the GUI. If the user
     * changes the tab, the detail-area also changes. Every tab has exactly one
     * detail-area connected to it.
     */
    private ScrollPane details;

    /**
     * The AnchorPane which represents the configuration-area in the GUI. If the
     * user changes the tab, the configuration-area also changes. Every tab has
     * exactly one configuration-area connected to it.
     */
    private ScrollPane configurationArea;

    /**
     * The graph shown in the GUI. If the user changes the tab, the graph also
     * changes. Every tab has exactly one graph connected to it.
     */
    private final Graph graph;

    private javafx.scene.control.Tab tab;

    private final App app;

    /**
     * When a new tab is created the corresponding controller and all connected
     * parts in the GUI are set.
     *
     * @param app               the app associated with the tab
     * @param controller        The controller connected to this tab.
     * @param configurationArea The configuration-area connected to this tab.
     * @param graph             The graph connected to this tab.
     */
    ToloTab(App app, TabControllerInterface controller, ScrollPane configurationArea, Graph graph) {
        super();
        this.app = app;
        this.details = new ScrollPane();
        this.configurationArea = new ScrollPane();
        this.configurationArea.setContent(configurationArea.getContent());
        this.graph = graph;
    }

    @Override
    public void closeTab() {
        if (this.getTabPane() != null) {
            this.getTabPane().getTabs().remove(this);
        }
    }

    /**
     * Returns the ScrollPane which represents the detail-area in the GUI.
     *
     * @return Returns a JavaFX ScrollPane.
     */
    ScrollPane getDetails() {
        return this.details;
    }

    /**
     * Sets a scroll pane as detail area to show values.
     *
     * @param details The pane.
     */
    void setDetails(ScrollPane details) {
        this.details.setContent(details.getContent());
    }

    /**
     * Returns the AnchorPane which represents the configuration-area in the GUI.
     *
     * @return Returns a JavaFX AnchorPane.
     */
    ScrollPane getConfigurationArea() {
        return this.configurationArea;
    }

    /**
     * Sets a scroll pane as configuration area.
     *
     * @param configArea The pane.
     * @param tab        The selected tab.
     * @param tabs       All tabs to be shown in the configuration area.
     */
    void setConfigurationArea(ScrollPane configArea, javafx.scene.control.Tab tab, TabPane tabs) {
        TabPane tabPane = tabs;
        tabPane.getSelectionModel().select(tab);
        this.configurationArea.setContent(tabPane);
        this.tab = tab;
    }

    /**
     * @return the JavaFX tab.
     */
    javafx.scene.control.Tab getTab() {
        return this.tab;
    }

    @Override
    public Graph getGraph() {
        return this.graph;
    }

    @Override
    public void showGraph(Configuration conf) {
        this.app.runLater(() -> this.app.showGraph(this, conf, this.graph.getController()));

    }

    @Override
    public void showError(Exception e) {
        this.app.showError(e);

    }

    @Override
    public boolean askForOverwrite(Path configurationFile) {
        // TODO Auto-generated method stub
        return false;
    }

}
