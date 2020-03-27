/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.graph;

import java.util.HashSet;

import org.graphstream.ui.fx_viewer.util.FxMouseManager;
import org.graphstream.ui.graphicGraph.GraphicElement;

import edu.kit.informatik.tolowiz.model.visualization.Color;
import edu.kit.informatik.tolowiz.model.visualization.Group;
import edu.kit.informatik.tolowiz.model.visualization.InstanceMark;
import edu.kit.informatik.tolowiz.model.visualization.Point;
import edu.kit.informatik.tolowiz.view.gui.App;
import edu.kit.informatik.tolowiz.view.gui.JavaFxViewInterface;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Manages all mouse events that are set off inside the tab that shows the
 * Graph, mainly including dragging a node and showing a context menu inside the
 * tab to show values or hide the node.
 *
 * @author anja
 * @version 1.0
 */

public class GraphMouseManager extends FxMouseManager {

    private JavaFxViewInterface viewInterface;
    private Graph graph;

    private ContextMenu contextMenuBackground = new ContextMenu();
    private ContextMenu contextMenuNode = new ContextMenu();
    private ContextMenu contextMenuCurrent = new ContextMenu(); // the currently shown context menu
    private MenuItem menuItemValues = new MenuItem("Show Values");
    private MenuItem menuItemHide = new MenuItem("Hide Instance");
    private MenuItem menuItemShow = new MenuItem("Show Total Graph");

    private Menu addToGroup = new Menu("Add To Group ");
    private Menu removeFromGroup = new Menu("Remove From Group ");
    private Menu changeMark = new Menu("Change Mark");
    private Menu changeStroke = new Menu("Change Stroke");
    private Menu changeShape = new Menu("Change Shape");
    private MenuItem changeColor = new MenuItem("Choose Color");

    private MenuItem strokeNone = new MenuItem("None");
    private MenuItem strokePlain = new MenuItem("Plain");
    private MenuItem strokeDashed = new MenuItem("Dashes");
    private MenuItem strokeDotted = new MenuItem("Dots");

    private MenuItem shapeCircle = new MenuItem("Circle");
    private MenuItem shapeBox = new MenuItem("Box");
    private MenuItem shapeDiamond = new MenuItem("Diamond");
    private MenuItem shapeCross = new MenuItem("Cross");

    private ColorPicker colorWheel = new ColorPicker(javafx.scene.paint.Color.BLACK);
    private GraphicElement lastSelectedNode;
    private Image tolowizIcon;
    private Stage colorStage;

    /**
     * The constructor. Initializes the JavaFx context menus that will be shown
     * inside the tab where you click on the right mouse button.
     *
     * @param graph         That is currently shown in the tab and which nodes can
     *                      be clicked on
     * @param viewInterface To communicate with the gui package, e.g. when values to
     *                      a certain node should be shown, the gui has to be
     *                      notified
     */
    public GraphMouseManager(Graph graph, JavaFxViewInterface viewInterface) {
        super();
        this.tolowizIcon = new Image(App.class.getResourceAsStream("tolowiz_hat.png")); //$NON-NLS-1$
        this.graph = graph;
        this.viewInterface = viewInterface;

        this.contextMenuBackground.getItems().addAll(this.menuItemShow);

        this.changeStroke.getItems().addAll(this.strokeNone, this.strokePlain, this.strokeDashed, this.strokeDotted);
        this.changeShape.getItems().addAll(this.shapeCircle, this.shapeBox, this.shapeDiamond, this.shapeCross);
        this.changeColor.setOnAction(event -> {
            this.colorStage = new Stage();
            this.colorStage.getIcons().add(this.tolowizIcon);
            this.colorStage.setTitle("Choose Color");
            AnchorPane pane = new AnchorPane();
            AnchorPane.setBottomAnchor(this.colorWheel, 0.0);
            AnchorPane.setTopAnchor(this.colorWheel, 0.0);
            AnchorPane.setLeftAnchor(this.colorWheel, 0.0);
            AnchorPane.setRightAnchor(this.colorWheel, 0.0);
            pane.getChildren().add(this.colorWheel);
            this.colorStage.setScene(new Scene(pane, 100, 30));
            this.colorStage.setOnCloseRequest(ev -> this.viewInterface.unlockGui());
            this.viewInterface.lockGui();
            this.colorStage.show();

        });

        this.changeMark.getItems().addAll(this.changeStroke, this.changeShape, this.changeColor);

        this.contextMenuNode.getItems().addAll(this.menuItemValues, this.menuItemHide, this.changeMark);

    }

    /**
     * When you click the right mouse button on the background of the tab, a context
     * menu for showing the whole graph again should pop up.
     */
    @Override
    protected void mouseButtonPress(MouseEvent event) {
        super.mouseButtonPress(event);
        this.contextMenuCurrent.hide();

        if (event.getButton() == MouseButton.SECONDARY) {
            this.menuItemShow.setOnAction((mouseevent) -> {
                this.graph.onZoomAll();
            });

            this.contextMenuCurrent = this.contextMenuBackground;
            this.graph.getTab().setContextMenu(this.contextMenuCurrent);
        }
    }

    /**
     * Shows context menu for a node when clicking right mouse button, which
     * includes menu items like hiding the node or changing its appearance. Shows
     * label for the last one that was selected by clicking on it with the left
     * mouse button and therefore 'selecting' it.
     */
    @Override
    protected void mouseButtonPressOnElement(GraphicElement element, MouseEvent event) {

        this.contextMenuCurrent.hide();

        final ToloNode current = this.graph.getAllNodes().stream()
                .filter(n -> (n.getGsNode().getId()).equals(element.getId())).findFirst().orElse(null);

        // Does same thing as stream above:
        // for (ToloNode n : this.graph.getAllNodes()) {
        // if (n.getGsNode().getId().equals(element.getId())) {
        // current = n;
        // }
        // }

        if (element.getAttribute("ui.hide") == null) {
            super.mouseButtonPressOnElement(element, event);

            if (event.getButton() == MouseButton.PRIMARY) {
                for (ToloNode n : this.graph.getAllNodes()) {
                    if ((n.getGsNode().getId()).equals(element.getId())) {
                        if (this.lastSelectedNode != null) {
                            this.lastSelectedNode.removeAttribute("ui.label");
                        }
                        element.setAttribute("ui.label", n.getInstance().getName());
                        this.lastSelectedNode = element;
                    }
                }
            }

            if ((event.getButton() == MouseButton.SECONDARY) && !this.viewInterface.isInPresentingMode()) {
                this.menuItemValues.setOnAction((mouseevent) -> {
                    this.viewInterface.showActivatedValues(current.getInstance());
                });

                this.menuItemHide.setOnAction((mouseevent) -> {
                    this.graph.getController().hideInstance(current.getInstance());
                    this.graph.getViewInterface().hideInstance(this.graph.getConfiguration());
                    this.graph.getViewInterface().refresh();
                });

                this.strokeNone.setOnAction(mouseevent -> {
                    InstanceMark mark = new InstanceMark(InstanceMark.InstanceStroke.NONE);
                    this.graph.getController().markInstance(current.getInstance(), mark);
                });

                this.strokePlain.setOnAction(mouseevent -> {
                    InstanceMark mark = new InstanceMark(InstanceMark.InstanceStroke.PLAIN);
                    this.graph.getController().markInstance(current.getInstance(), mark);
                });

                this.strokeDashed.setOnAction(mouseevent -> {
                    InstanceMark mark = new InstanceMark(InstanceMark.InstanceStroke.DASHES);
                    this.graph.getController().markInstance(current.getInstance(), mark);
                });

                this.strokeDotted.setOnAction(mouseevent -> {
                    InstanceMark mark = new InstanceMark(InstanceMark.InstanceStroke.DOTS);
                    this.graph.getController().markInstance(current.getInstance(), mark);
                });

                this.shapeCircle.setOnAction(mouseevent -> {
                    InstanceMark mark = new InstanceMark(InstanceMark.InstanceShape.CIRCLE);
                    this.graph.getController().markInstance(current.getInstance(), mark);
                });

                this.shapeBox.setOnAction(mouseevent -> {
                    InstanceMark mark = new InstanceMark(InstanceMark.InstanceShape.BOX);
                    this.graph.getController().markInstance(current.getInstance(), mark);
                });

                this.shapeDiamond.setOnAction(mouseevent -> {
                    InstanceMark mark = new InstanceMark(InstanceMark.InstanceShape.DIAMOND);
                    this.graph.getController().markInstance(current.getInstance(), mark);
                });

                this.shapeCross.setOnAction(mouseevent -> {
                    InstanceMark mark = new InstanceMark(InstanceMark.InstanceShape.CROSS);
                    this.graph.getController().markInstance(current.getInstance(), mark);
                });

                this.colorWheel.setOnAction(mouseevent -> {
                    int red = (int) (this.colorWheel.getValue().getRed() * 255);
                    int green = (int) (this.colorWheel.getValue().getGreen() * 255);
                    int blue = (int) (this.colorWheel.getValue().getBlue() * 255);
                    Color color = new Color(red, green, blue);
                    InstanceMark mark = new InstanceMark(color);
                    this.graph.getController().markInstance(current.getInstance(), mark);
                    this.viewInterface.unlockGui();
                    this.colorStage.hide();
                });

                HashSet<MenuItem> allGroups = new HashSet<>();
                for (Group g : this.graph.getConfiguration().getGroups()) {
                    MenuItem groupNameItem = new MenuItem(g.getName());
                    groupNameItem.setOnAction(mouseevent -> {
                        this.graph.getController().addToGroup(g, current.getInstance());
                    });
                    allGroups.add(groupNameItem);
                    if (g.getInstances().contains(current.getInstance())) {
                        groupNameItem.setDisable(true);
                    } else {
                        groupNameItem.setDisable(false);
                    }
                }

                this.addToGroup.getItems().clear();
                if (allGroups.isEmpty()) {
                    this.addToGroup.setDisable(true);
                } else {
                    this.addToGroup.setDisable(false);
                }
                this.addToGroup.getItems().addAll(allGroups);

                HashSet<MenuItem> activeGroups = new HashSet<>();
                for (Group g : this.graph.getConfiguration().getGroups()) {
                    MenuItem groupNameItem = new MenuItem(g.getName());
                    groupNameItem.setOnAction(mouseevent -> {
                        this.graph.getController().removeFromGroup(g, current.getInstance());
                    });

                    if (g.getInstances().contains(current.getInstance())) {
                        activeGroups.add(groupNameItem);
                    }
                }

                this.removeFromGroup.getItems().clear();
                if (activeGroups.isEmpty()) {
                    this.removeFromGroup.setDisable(true);
                } else {
                    this.removeFromGroup.setDisable(false);
                }
                this.removeFromGroup.getItems().addAll(activeGroups);

                this.contextMenuNode.getItems().add(2, this.addToGroup);
                this.contextMenuNode.getItems().add(3, this.removeFromGroup);

                this.contextMenuCurrent = this.contextMenuNode;
                this.graph.getTab().setContextMenu(this.contextMenuCurrent);
            }
        } else {
            this.mouseButtonPress(event);
        }
    }

    /**
     * Dragging a node must set the position in the configuration anew.
     */
    @Override
    protected void mouseButtonReleaseOffElement(GraphicElement element, MouseEvent event) {
        if (element.getAttribute("ui.hide") == null) {
            super.mouseButtonReleaseOffElement(element, event);
            for (ToloNode n : this.graph.getAllNodes()) {
                Point old = n.getInstance().getStoredPosition();
                Double delta = 0.005;
                if ((Math.abs(old.getX() - element.getX()) >= delta)
                        || (Math.abs(old.getY() - element.getY()) >= delta)) {
                    if ((n.getGsNode().getId()).equals(element.getId())) {
                        this.graph.getController().moveNodeTo(n.getInstance(), element.getX(), element.getY());
                        n.getGsNode().neighborNodes()
                                .map(gsn -> this.graph.getAllNodes().stream()
                                        .filter(tn -> gsn.getId().equals(tn.getGsNode().getId())).findFirst()
                                        .orElse(null))
                                .forEach(ToloNode::build);
                    }
                }
            }
        }
    }

    /**
     * Dragging a node should only be possible for the primary mouse click or else a
     * secondary click menu selection is seen as dragging and will be undone by
     * undo.
     */
    @Override
    protected void elementMoving(GraphicElement element, MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            super.elementMoving(element, event);
        }

    }

    /**
     * Workaround to show a context menu inside a tab and not at the label of the
     * tab.
     *
     * @return the context menu that should be shown inside the tab.
     */
    public ContextMenu getContextMenu() {
        return this.contextMenuCurrent;
    }

}
