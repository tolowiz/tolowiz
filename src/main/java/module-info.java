/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * ToloWiz - A simple application for visualizing network ontologies.
 *
 * @authors Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza, Sandra
 *          Wolf
 *
 */
module edu.kit.informatik.tolowiz {
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    requires javafx.web;
    // requires transitive java.desktop;
    requires javatuples;
    requires org.apache.jena.core;
    requires transitive gs.core;
    requires transitive gs.ui.javafx;
    requires commons.io;
    requires javafx.swing;
    requires org.apache.jena.arq;
    requires javafx.base;
    requires org.apache.pdfbox;
    requires org.apache.commons.collections4;
    requires org.apache.commons.codec;
    // requires org.mockito;

    // opens edu.kit.informatik.tolowiz to javafx.fxml;
    opens edu.kit.informatik.tolowiz.view.gui to javafx.fxml;

    exports edu.kit.informatik.tolowiz.model.visualization;
    exports edu.kit.informatik.tolowiz.model.data.configurations;
    exports edu.kit.informatik.tolowiz.model.data.file;
    exports edu.kit.informatik.tolowiz.model.data.icons;
    exports edu.kit.informatik.tolowiz.model.ontology;
    exports edu.kit.informatik.tolowiz.controller;
    exports edu.kit.informatik.tolowiz.controller.interpretation;

    opens edu.kit.informatik.tolowiz.model.ontology;
    opens edu.kit.informatik.tolowiz.model.visualization;
    opens edu.kit.informatik.tolowiz.model.data.configurations;
    opens edu.kit.informatik.tolowiz.model.data.file;
    opens edu.kit.informatik.tolowiz.model.data.icons;
    opens edu.kit.informatik.tolowiz.controller;
    opens edu.kit.informatik.tolowiz.controller.interpretation;

    // exports edu.kit.informatik.tolowiz;
    // exports edu.kit.informatik.tolowiz.view.gui to javafx.fxml;
    exports edu.kit.informatik.tolowiz.view.gui;// to javafx.graphics;
    exports edu.kit.informatik.tolowiz.view.graph;// to javafx.graphics;

    opens edu.kit.informatik.tolowiz.view.graph to javafx.graphics;
}
