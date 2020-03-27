/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.graph;

import java.nio.file.Path;

import edu.kit.informatik.tolowiz.controller.ImageFiletype;

/**
 * Interface representing an image of an ontology to be exported or printed.
 *
 * @author Tobias Klumpp
 *
 */
public interface ImageInterface {

    /**
     * Exports the image to a location in the specified file type.
     *
     * @param file the location the image should be stored
     * @param type the filetype of the image
     * @throws ImageWriteException if an internal error occurs
     */
    public void export(Path file, ImageFiletype type) throws ImageWriteException;

    /**
     * Exports the image to a location as SVG.
     *
     * @param file the location
     * @throws ImageWriteException When an error occurs during writing
     */
    public void exportSVG(Path file) throws ImageWriteException;

    /**
     * Exports the image to a location as PDF.
     *
     * @param file the location
     * @throws ImageWriteException if an internal error occurs
     */
    public void exportPDF(Path file) throws ImageWriteException;

    /**
     * Prints the image.
     *
     * @throws ImageWriteException if an internal error occurs
     */
    public void print() throws ImageWriteException;

}
