/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.graph;

import edu.kit.informatik.tolowiz.controller.ImageFiletype;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSinkSVG2;
import org.graphstream.ui.view.GraphRenderer;

/**
 * @author Tobias Klumpp
 *
 */
class Image implements ImageInterface {
    private MultiGraph graph;
    private GraphRenderer renderer;
    private int width;
    private int height;

    /**
     * The constructor.
     *
     * @param visualizedGraph The graph a screenshot should be taken of.
     * @param renderer The GraphStream graph renderer.
     * @param d The width of the image.
     * @param e The height of the image.
     */
    Image(MultiGraph visualizedGraph, GraphRenderer renderer, double d,
            double e) {
        this.renderer = renderer;
        this.graph = visualizedGraph;
        this.width = (int) d;
        this.height = (int) e;
    }

    @Override
    public void export(Path file, ImageFiletype type)
            throws ImageWriteException {
        // WritableImage image = this.node.snapshot(new SnapshotParameters(),
        // null);
        String typename = null;
        switch (type) {
        case GIF:
            typename = "gif";
            break;
        case JPEG:
            typename = "jpeg";
            break;
        case PNG:
            typename = "png";
            break;
        case TIFF:
            typename = "tiff";
            break;
        case SVG:
            typename = "svg";
            break;
        default:
            break;

        }
        // this.exportSVG(file);
        this.renderer.screenshot(file.toString(), this.width, this.height);

    }

    @Override
    public void exportSVG(Path file) throws ImageWriteException {
        System.setProperty("gs.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        // String path = "C:\\users\\anja\\documents\\tryexport.svg";

        FileSinkSVG2 svg = new FileSinkSVG2();

        try {
            svg.writeAll(this.graph, file.toString());
            svg.flush();

        } catch (IOException ex) {
            throw new ImageWriteException(ex);
        }

    }

    @Override
    public void exportPDF(Path file) throws ImageWriteException {
        Path pngFile = Paths.get(System.getProperty("user.home"))
                .resolve(".tolowiz").resolve("creation")
                .resolve("pdfcreate.png");

        try {
            Files.createDirectories(pngFile.getParent());
            this.export(pngFile, ImageFiletype.PNG);
            PDDocument pdDoc = new PDDocument();
            PDPage pdPage = new PDPage(PDRectangle.A4);
            pdDoc.addPage(pdPage);

            PDImageXObject pdImage;

            pdImage = PDImageXObject.createFromFile(pngFile.toString(), pdDoc);
            PDPageContentStream contentStream;
            contentStream = new PDPageContentStream(pdDoc, pdPage);
            contentStream.drawImage(pdImage, 10, 400);
            contentStream.close();

            pdDoc.save(file.toString());
            pdDoc.close();
            Files.deleteIfExists(pngFile);
        } catch (IOException e) {
            throw new ImageWriteException(e);
        }
    }

    @Override
    public void print() throws ImageWriteException {
//        Path pdfFile = Paths.get(System.getProperty("user.home")).resolve(".tolowiz").resolve("creation")
//                .resolve("pdfprint.pdf");
//
//        this.exportPDF(pdfFile);
//        try {
//            PDDocument document = PDDocument.load(pdfFile.toFile());
//        } catch (IOException e) {
//            throw new ImageWriteException(e);
//        }
//
//        PrintService myPrintService;
//        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
//        for (PrintService printService : printServices) {
//            if (printService.getName().trim().equals("My Windows printer Name")) {
//                myPrintService = printService;
//            }
//        }
//        PrinterJob job = PrinterJob.createPrinterJob();
        // job.setPageable(new PDFPageable(document));
        // job.setPrintService(myPrintService);
        // job.print();

    }

}
