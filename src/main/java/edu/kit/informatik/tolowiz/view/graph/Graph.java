/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.graph;

import java.util.HashSet;
import java.util.Set;

import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.fx_viewer.FxDefaultView;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.GraphRenderer;
import org.graphstream.ui.view.camera.Camera;

import edu.kit.informatik.tolowiz.model.visualization.CameraListenerInterface;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;
import edu.kit.informatik.tolowiz.model.visualization.ConfigurationListenerInterface;
import edu.kit.informatik.tolowiz.model.visualization.InstanceConfiguration;
import edu.kit.informatik.tolowiz.model.visualization.Point;
import edu.kit.informatik.tolowiz.model.visualization.RelationConfiguration;
import edu.kit.informatik.tolowiz.view.gui.JavaFxViewInterface;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Models the graph that visualizes the ontology with the help of the
 * GraphStream tool: An {@link ToloEdge} represents a relation, a
 * {@link ToloNode} represents an instance. The ontology is visualized with the
 * current configuration that is saved in the model.visualization package. <br>
 * Is used for communicating with the view.gui package. <br>
 * Inherits from the FxDefaultView provided by GraphStream in order to be easily
 * added into the GUI.
 *
 *
 * @author Anja
 * @version 1.0
 */
public class Graph extends FxDefaultView
        implements GraphInterface, ConfigurationListenerInterface, CameraListenerInterface {

    /**
     * Lists all the edges the graph currently contains.
     */
    private Set<ToloEdge> allEdges;

    /**
     * Lists all the nodes the graph currently contains.
     */
    private Set<ToloNode> allNodes;

    /**
     * The GraphStream graph object.
     */
    private MultiGraph visualizedGraph;

    /**
     * Set of views on a graphic graph that visualizes a MultiGraph, known by
     * GraphStream.
     */
    private FxViewer viewer;

    /**
     * The tab the graph is visualized in.
     */
    private Tab tab;

    /**
     * Manages all mouse events occurring in the tab that contains the Graph object
     * and therefore an FxDefaultView object.
     */
    private GraphMouseManager mouseManager;

    /**
     * An interface for communicating with the view.gui package when it needs to be
     * notified of changes in the graph package's own event handling class that is
     * called 'GraphMouseManager'.
     */
    private JavaFxViewInterface viewInterface;

    /**
     * A controller that receives instructions from the graph object and forwards
     * them to the model.visualization.Configuration.
     */
    private GraphControllerInterface controller;

    /**
     * Sprites are additional information that you can attach (visually, if you want
     * to) to an edge or a node. They are used as labels of an edge here, as the
     * GraphStream labels interfere with each other when multiple edges exist
     * between the same two nodes.
     */
    private SpriteManager spriteManager;

    /**
     * Constructor to create new Graph objects.
     *
     * @param mg            The graph object known by GraphStream.
     * @param viewer        Complete architecture to render a graph in a panel or
     *                      frame.
     * @param renderer      To only render the Graphic Graph, no matter which
     *                      surface.
     * @param conf          The configuration the graph should be initialized with.
     * @param controller    The Graph controller object.
     * @param viewInterface Interface to communicate with the gui if necessary.
     */
    public Graph(MultiGraph mg, FxViewer viewer, GraphRenderer renderer, Configuration conf,
            GraphControllerInterface controller, JavaFxViewInterface viewInterface) {

        super(viewer, conf.getOntology().getIRI(), renderer);

        this.visualizedGraph = mg;
        this.viewer = viewer; // create viewer first, add edges and nodes later
        this.controller = controller;
        this.viewInterface = viewInterface;

        this.visualizedGraph.setAttribute("ui.quality");
        this.visualizedGraph.setAttribute("ui.antialias");

        viewer.addView(this);

        this.spriteManager = new SpriteManager(this.graph);

        this.mouseManager = new GraphMouseManager(this, viewInterface);
        this.setMouseManager(this.mouseManager);

        this.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double pixX = event.getX();
                double pixY = event.getY();
                if (event.getDeltaY() > 0) {
                    Graph.this.onZoomPlus(pixX, pixY);
                } else {
                    Graph.this.onZoomMinus(pixX, pixY);
                }
            }
        });

        this.setOnContextMenuRequested(
                e -> this.mouseManager.getContextMenu().show(this, e.getScreenX(), e.getScreenY()));
        conf.getCameraConfiguration().addListener(this);
        this.showGraph(conf);

    }

    /**
     * Show the graph with a configuration.
     *
     * @param conf Selected configuration file.
     */
    private void showGraph(Configuration conf) {
        conf.addListener(this); // register as listener to changes in the
                                // configuration

        this.allNodes = new HashSet<>();
        for (InstanceConfiguration ic : conf.getInstances()) {
            ToloNode node = new ToloNode(this, ic);
            this.allNodes.add(node);
        }

        this.allEdges = new HashSet<>();
        for (RelationConfiguration rc : conf.getRelations()) {
            ToloEdge edge = new ToloEdge(this, rc);
            this.allEdges.add(edge);
        }

        this.paint();

    }

    /**
     * Adds GraphStream elements to the GraphStream graph for every node or relation
     * known by the configuration.
     */
    private void paint() {

        for (ToloNode node : this.allNodes) {
            String nodeID = String.valueOf(node.getInstance().getURI());
            this.visualizedGraph.addNode(nodeID); // unfortunately, GraphStream only accepts Strings as ID
            node.setGsNode(nodeID);
        }

        for (ToloEdge edge : this.allEdges) {
            String nodeID1 = String.valueOf(edge.getRelation().getOrigin().getURI());
            String nodeID2 = String.valueOf(edge.getRelation().getDestination().getURI());
            String edgeID = edge.getRelation().getURI();
            this.visualizedGraph.addEdge(edgeID, nodeID1, nodeID2, true);
            edge.setGsEdge(edgeID);
        }

        this.build();
    }

    /**
     * A listener as to when a graph has to be shown with a completely new
     * configuration and therefore has to be be recreated anew by GraphStream.
     * Notification comes as this object registered itself as listener to
     * ConfigurationListenerInterface. This should not be used for other purposes as
     * to serve as a listener.
     */
    @Override
    public void onFullChange() {
        this.runLater(() -> {
            this.build();
            this.viewInterface.refresh();
        });
    }

    /**
     * Is used when a change concerns the majority of the elements of a graph (e.g.
     * restoring standard view) and it has to be shown with a completely new
     * configuration and therefore has to be be recreated anew by GraphStream. Is
     * used when a graph has to be built for the very first time or when a
     * notification comes from the onFullChange() method.
     */
    private void build() {
        this.visualizedGraph = new MultiGraph("embedded");
        this.visualizedGraph.setAttribute("ui.quality");
        this.visualizedGraph.setAttribute("ui.antialias");

        this.allNodes.forEach(n -> n.build());
        this.allEdges.forEach(e -> e.build());
    }

    /**
     * Show the required section of the graph when angle of camera is changed.
     * Includes zooming and scrolling.
     */
    @Deprecated
    @Override
    public void onCameraConfigurationChange() {
        this.onZoomAll();

    }

    /**
     * Takes a screenshot of a graph in the PNG format.
     *
     * @param resolution the resolution factor of the screenshot. 1 equals a normal
     *                   screenshot.
     */
    @Override
    public ImageInterface getImage(double resolution) {
        return new Image(this.visualizedGraph, this.renderer, this.tab.getTabPane().getWidth() * resolution,
                this.tab.getTabPane().getHeight() * resolution);
    }

    /**
     * Takes a screenshot of a graph in the PNG format.
     */
    @Override
    public ImageInterface getImage() {
        return new Image(this.visualizedGraph, this.renderer, this.tab.getTabPane().getWidth(),
                this.tab.getTabPane().getHeight());
    }

    /**
     * Zooms in.
     */
    private void onZoomPlus(double pixX, double pixY) {
        Camera cam = this.getCamera();
        // zoom about the pixPoint of the mouse cursor
        if ((Math.abs(pixX) > 0.01) || (Math.abs(pixY) > 0.01)) {
            Point3 viewCenterPt = cam.getViewCenter();
            Point3 pixCenterPt = this.ptToPixelPt(viewCenterPt);
            double dx = pixX - pixCenterPt.x;
            double dy = pixY - pixCenterPt.y;
            pixCenterPt.x += dx * 0.25 * 0.8;
            pixCenterPt.y += dy * 0.25 * 0.8;
            viewCenterPt = this.pixelPtToPt(pixCenterPt);
            cam.setViewCenter(viewCenterPt.x, viewCenterPt.y, viewCenterPt.z);
        }
        double zoomFactor = cam.getViewPercent();
        zoomFactor *= 0.8;
        cam.setViewPercent(zoomFactor);
    }

    /**
     * Zooms out.
     */
    private void onZoomMinus(double pixX, double pixY) {
        Camera cam = this.getCamera();
        // zoom about the pixPoint of the mouse cursor
        if ((Math.abs(pixX) > 0.01) || (Math.abs(pixY) > 0.01)) {
            Point3 viewCenterPt = cam.getViewCenter();
            Point3 pixCenterPt = this.ptToPixelPt(viewCenterPt);
            double dx = pixX - pixCenterPt.x;
            double dy = pixY - pixCenterPt.y;
            pixCenterPt.x -= (dx * 0.25) / 1.25;
            pixCenterPt.y -= (dy * 0.25) / 1.25;
            viewCenterPt = this.pixelPtToPt(pixCenterPt);
            cam.setViewCenter(viewCenterPt.x, viewCenterPt.y, viewCenterPt.z);
        }
        double zoomFactor = cam.getViewPercent();
        zoomFactor *= 1.25;
        cam.setViewPercent(zoomFactor);
    }

    /**
     * Sets zooming angle so that the whole graph is shown.
     */
    void onZoomAll() {
        Camera cam = this.getCamera();
        if (cam == null) {
            return;
        }
        cam.resetView();
    }

    /**
     * Converts a point (pixel) on the screen to a logical point.
     *
     * @param pixelPt The pixel point on the screen.
     * @return globPt The logical point.
     */
    Point3 pixelPtToPt(Point3 pixelPt) {
        Point3 globPt = new Point3();
        Camera cam = this.getCamera();
        if (cam == null) {
            return globPt;
        }
        globPt = cam.transformPxToGu(pixelPt.x, pixelPt.y);
        return globPt;
    }

    /**
     * Converts a logical point to a point (pixel) on the screen.
     *
     * @param globPt The logical point.
     * @return pixelPt The pixel point.
     */
    Point3 ptToPixelPt(Point3 globPt) {
        Point3 pixelPt = new Point3();
        Camera cam = this.getCamera();
        if (cam == null) {
            return pixelPt;
        }
        pixelPt = cam.transformGuToPx(globPt.x, globPt.y, globPt.z);
        return pixelPt;

    }

    /**
     * Gets notified by gui which arrow key was pushed and calls the right panning
     * method accordingly.
     *
     * @param event The key event that was triggered by pressing an arrow key on the
     *              keyboard.
     */
    public void handleArrowKeys(KeyEvent event) {
        if (event.getCode() == KeyCode.RIGHT) {
            this.onPanRight();
        }
        if (event.getCode() == KeyCode.DOWN) {
            this.onPanDown();
        }
        if (event.getCode() == KeyCode.UP) {
            this.onPanUp();
        }
        if (event.getCode() == KeyCode.LEFT) {
            this.onPanLeft();
        }
        event.consume();
    }

    /**
     * Moves the camera to the left.
     */
    private void onPanLeft() {
        Camera cam = this.getCamera();
        double left = 100 / cam.getMetrics().ratioPx2Gu;
        // the view itself is changing the centerPt on some events
        Point3 viewCenterPt = cam.getViewCenter();
        viewCenterPt.x -= left;
        cam.setViewCenter(viewCenterPt.x, viewCenterPt.y, viewCenterPt.z);
    }

    /**
     * Moves the camera to the right.
     */
    private void onPanRight() {
        Camera cam = this.getCamera();
        double right = 100 / cam.getMetrics().ratioPx2Gu;
        Point3 viewCenterPt = cam.getViewCenter();
        viewCenterPt.x += right;
        cam.setViewCenter(viewCenterPt.x, viewCenterPt.y, viewCenterPt.z);
    }

    /**
     * Moves the camera down.
     */
    private void onPanDown() {
        Camera cam = this.getCamera();
        double down = 100 / cam.getMetrics().ratioPx2Gu;
        Point3 viewCenterPt = cam.getViewCenter();
        viewCenterPt.y -= down;
        cam.setViewCenter(viewCenterPt.x, viewCenterPt.y, viewCenterPt.z);
    }

    /**
     * Moves the camera up.
     */
    private void onPanUp() {
        Camera cam = this.getCamera();
        double up = 100 / cam.getMetrics().ratioPx2Gu;
        Point3 viewCenterPt = cam.getViewCenter();
        viewCenterPt.y += up;
        cam.setViewCenter(viewCenterPt.x, viewCenterPt.y, viewCenterPt.z);
    }

    /**
     * Refreshes the outer view.
     */
    public void viewRefresh() {
        this.viewInterface.refresh();
    }

    /**
     * Traces the position the autolayout from GraphStream gives the nodes at the
     * initialization of the graph and saves them in the model. Disables the
     * autolayout afterwards in order to enable dragging a node.
     */
    public void traceAutolayoutGraph() {

        GraphicNode graphicNode; // the internal and updated GraphicNode
        double x;
        double y;
        String id;
        // the internal and updated GraphicGraph
        GraphicGraph graphicGraph = this.viewer.getGraphicGraph();

        System.out.println("--------------------------------------------------");
        System.out.println("Trace graph");
        System.out.println("--------------------------------------------------");

        for (ToloNode n : this.allNodes) {
            if (n.getInstance().getPosition().isEmpty()) {
                id = n.getGsNode().getId();
                graphicNode = (GraphicNode) graphicGraph.getNode(id); // the modified graph is here

                x = graphicNode.getX();
                y = graphicNode.getY();

                System.out.println("Position set by autolayout: x: " + x + ", y: " + y);

                n.getInstance().setDefaultPosition(new Point(x, y));

                System.out.println("Position in model: x: " + n.getInstance().getPosition().get().getX() + ", y:"
                        + n.getInstance().getPosition().get().getY());

            } else {
                System.out.println("Positions are already set");
            }

        }
        System.out.println("--------------------------------------------------");
        System.out.println("End of graph tracing");
        System.out.println("--------------------------------------------------");
        System.out.println();

        this.viewer.disableAutoLayout();

    }

    /**
     * Enables GraphStream's autolayout. Is called directly by the controller,
     * shortly before a new thread is started for the fixLayout() method.
     */
    @Override
    public void autoLayout() {
        this.runLater(() -> this.viewer.enableAutoLayout());
    }

    /**
     * Fixes (traces) the layout to the model.
     */
    @Override
    public void fixLayout(long size) {

        this.runLater(() -> new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500 + 2 * size);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Graph.this.traceAutolayoutGraph();
            }
        }.start());
    }

    /**
     * @return the configuration the graph is currently shown with.
     */
    public Configuration getConfiguration() {
        return this.controller.getConfiguration();
    }

    /**
     * @return The GraphStream graph element.
     */
    public MultiGraph getVisualizedGraph() {
        return this.visualizedGraph;
    }

    /**
     * @return all nodes a graph contains.
     */
    public Set<ToloNode> getAllNodes() {
        return this.allNodes;
    }

    /**
     * @return the tab the graph is shown in.
     */
    public Tab getTab() {
        return this.tab;
    }

    /**
     * @param tab the graph should be shown in.
     */
    public void setTab(Tab tab) {
        this.tab = tab;
    }

    /**
     * @return the graph controller object.
     */
    public GraphControllerInterface getController() {
        return this.controller;
    }

    /**
     * @return the view interface.
     */
    public JavaFxViewInterface getViewInterface() {
        return this.viewInterface;
    }

    /**
     * @return the sprite manager for labelling an edge.
     */
    public SpriteManager getSpriteManager() {
        return this.spriteManager;
    }

    /**
     * Defers an execution to the view.
     *
     * @param run The execution.
     */
    public void runLater(Runnable run) {
        this.getViewInterface().runLater(run);

    }
}
