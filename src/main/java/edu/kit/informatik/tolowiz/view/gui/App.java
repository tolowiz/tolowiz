/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.view.gui;

import edu.kit.informatik.tolowiz.controller.ApplicationController;
import edu.kit.informatik.tolowiz.controller.ImageFiletype;
import edu.kit.informatik.tolowiz.controller.UndoException;
import edu.kit.informatik.tolowiz.controller.interpretation.RDFInterpreterFactory;
import edu.kit.informatik.tolowiz.model.data.configurations.Database;
import edu.kit.informatik.tolowiz.model.data.configurations.InternalDatabaseException;
import edu.kit.informatik.tolowiz.model.data.configurations.NoSuchEntryException;
import edu.kit.informatik.tolowiz.model.data.configurations.StorageInterface;
import edu.kit.informatik.tolowiz.model.data.file.FileSaver;
import edu.kit.informatik.tolowiz.model.data.icons.IconDatabase;
import edu.kit.informatik.tolowiz.model.data.icons.IconDatabaseException;
import edu.kit.informatik.tolowiz.model.data.icons.IconDatabaseInterface;
import edu.kit.informatik.tolowiz.model.data.icons.IconInterface;
import edu.kit.informatik.tolowiz.model.ontology.InstanceType;
import edu.kit.informatik.tolowiz.model.ontology.ValueType;
import edu.kit.informatik.tolowiz.model.visualization.Color;
import edu.kit.informatik.tolowiz.model.visualization.Configuration;
import edu.kit.informatik.tolowiz.model.visualization.DefaultHandler;
import edu.kit.informatik.tolowiz.model.visualization.Group;
import edu.kit.informatik.tolowiz.model.visualization.InstanceConfiguration;
import edu.kit.informatik.tolowiz.model.visualization.InstanceMark;
import edu.kit.informatik.tolowiz.model.visualization.InstanceTypeConfiguration;
import edu.kit.informatik.tolowiz.model.visualization.InstanceTypeConfiguration.TypeVisibility;
import edu.kit.informatik.tolowiz.model.visualization.RelationStyle;
import edu.kit.informatik.tolowiz.model.visualization.RelationTypeConfiguration;
import edu.kit.informatik.tolowiz.view.graph.Graph;
import edu.kit.informatik.tolowiz.view.graph.GraphControllerInterface;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.javatuples.Pair;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.CheckBoxTreeItem.TreeModificationEvent;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Class starting the program. Also serves as the event handler class which is
 * connected to the FXML files in which the GUI is specified. It passes the user
 * actions on to the GraphController, the ApplicationController or the
 * TabController.
 *
 * @author Tobias Klumpp, Fabian Palitza, Sandra Wolf
 *
 */
public class App extends Application implements JavaFxViewInterface {

    private static final String PNG = "png";
    /**
     * Indicates if events should be handled or ignored
     */
    private boolean eventsActive;

    private Stage cStage;

    @FXML
    private StackPane stackPane;

    private Map<String, CheckBoxTreeItem<String>> treeItems;
    private Map<String, CheckBoxTreeItem<String>> relationTreeItems;
    private Map<String, Pair<CheckBoxTreeItem<String>, Map<String, CheckBoxTreeItem<String>>>> valueTreeItems;

    private Image tolowizIcon = new Image(
            App.class.getResourceAsStream("tolowiz_hat.png")); //$NON-NLS-1$

    @FXML
    private VBox vBoxAll;

    @FXML
    private AnchorPane tabPaneAnchor;
    private double height;
    private double width;

    @FXML
    private AnchorPane warningBarPane;

    @FXML
    private SplitPane splitPaneWithTabs;

    @FXML
    private Button leavePresentingMode;

    @FXML
    private ScrollPane typesArea;
    @FXML
    private VBox typesVBox;

    @FXML
    private VBox groupsVBoxPane;

    @FXML
    private ScrollPane relationsArea;
    @FXML
    private ScrollPane valuesArea;
    @FXML
    private ScrollPane groupsArea;
    @FXML
    private VBox groupsVBox;
    @FXML
    private Button createNewGroup;
    @FXML
    private VBox hiddenInstancesVBox;
    @FXML
    private ScrollPane detailArea;
    @FXML
    private ScrollPane configurationArea;
    @FXML
    private TabPane configurationTabs;

    @FXML
    private TextField configurationTextField;
    @FXML
    private Button saveConfigurationButton;
    @FXML
    private Button cancelSaveConfigurationButton;
    @FXML
    private Label saveConfigWarningBar;

    @FXML
    private ScrollPane loadConfiguration;

    @FXML
    private TabPane tabPane;

    @FXML
    private Label warningBar;

    @FXML
    private Label warningBarGroups;

    @FXML
    private Button openButton;

    @FXML
    private Button saveButton;

    @FXML
    private TextArea warningPopUpText;

    @FXML
    private WebView helpWebView;
    @FXML
    private WebView creditsWebView;

    @FXML
    private MenuItem openOntology;
    @FXML
    private MenuItem importConfiguration;
    @FXML
    private MenuItem exportConfiguration;
    @FXML
    private Menu exportAsImage;
    @FXML
    private MenuItem closeOntology;
    @FXML
    private Menu graph;
    @FXML
    private MenuItem restoreStandardAlignment;
    @FXML
    private MenuItem restoreStandardView;
    @FXML
    private MenuItem saveConfiguration;
    @FXML
    private MenuItem switchToPresentingMode;
    @FXML
    private MenuItem undoMenuItem;
    @FXML
    private MenuItem redoMenuItem;
    @FXML
    private Menu changeFontSize;
    @FXML
    private ToggleGroup fontSize;

    @FXML
    private Button createGroupButton;
    @FXML
    private AnchorPane groupsAnchorPane;
    @FXML
    private TextField groupName;

    @FXML
    private Button undoButton;
    @FXML
    private Button redoButton;

    private FileChooser fileChooserImpConf = new FileChooser();

    private FileChooser fileChooserExpConf = new FileChooser();

    private FileChooser fileChooserExpImage = new FileChooser();

    private FileChooser fileChooserExpPDF = new FileChooser();
    private FileChooser fileChooserOnto = new FileChooser();

    private FileChooser fileChooserImpIcon = new FileChooser();

    private boolean inPresentingMode;

    /**
     * The GraphController which is connected to the graph shown in the current
     * active tab.
     */
    private GraphControllerInterface graphController;

    /**
     * The TabController of the current active tab. Changes when the user
     * changes the tab.
     */
    private TabControllerInterface tabController;

    /**
     * The current active tab.
     */
    private ToloTab tab;

    /**
     * The ApplicationController which performs actions that effect the hole
     * application.
     */
    private ApplicationControllerInterface controller;

    /**
     * The database in which the program data is stored.
     */
    private Database database;

    /**
     * The database in which the icons are stored.
     */
    private IconDatabaseInterface iconDatabase;

    /**
     * The filesaver which is needed by the database to store data.
     */
    private FileSaver filesaver;

    /**
     * The scene is used as a container for the graphical user interface with
     * JavaFX.
     */
    private Scene scene;

    /**
     * The JavaFX stage contains the scene with all the graphical components
     */
    private Stage stage;
    private StackPane progress;
    private CheckBoxTreeItem<String> rootRelationsCheckbox;

    /**
     * Standard constructor called at launch
     */
    public App() {
        this.inPresentingMode = false;
        this.filesaver = new FileSaver();
        Path programdir = Paths.get(System.getProperty("user.home")) //$NON-NLS-1$
                .resolve(".tolowiz"); //$NON-NLS-1$
        if (!Files.exists(programdir)) {
            try {
                Files.createDirectory(programdir);
            } catch (IOException e) {
                this.showError(e);
            }
        }
        try {
            this.database = new Database(this.filesaver, programdir);
        } catch (InternalDatabaseException e) {
            this.showError(e);
        }
        var defaultIcons = new HashSet<Pair<String, Pair<byte[], String>>>();
        try (InputStream in = App.class
                .getResourceAsStream("asset" + "." + App.PNG)) {
            defaultIcons.add(
                    new Pair<>("Asset", new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("firewall" + "." + App.PNG)) {
            defaultIcons.add(new Pair<>("Firewall",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("hardware_device" + "." + App.PNG)) {
            defaultIcons.add(new Pair<>("HardwareDevice",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("router" + "." + App.PNG)) {
            defaultIcons.add(
                    new Pair<>("Router", new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class.getResourceAsStream("switch.png")) {
            defaultIcons.add(
                    new Pair<>("Switch", new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("software_device" + "." + App.PNG)) {
            defaultIcons.add(new Pair<>("SoftwareDevice",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("configuration" + "." + App.PNG)) {
            defaultIcons.add(new Pair<>("Configuration",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("allowed_connection.png")) {
            defaultIcons.add(new Pair<>("AllowedConnection",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }

        try (InputStream in = App.class
                .getResourceAsStream("dns_interface.png")) {
            defaultIcons.add(new Pair<>("DnsInterface",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("ethernet_interface" + "." + App.PNG)) {
            defaultIcons.add(new Pair<>("EthernetInterface",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("http_interface.png")) {
            defaultIcons.add(new Pair<>("HttpInterface",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class.getResourceAsStream("interface.png")) {
            defaultIcons.add(new Pair<>("Interface",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("ipv4_interface.png")) {
            defaultIcons.add(new Pair<>("IpV4Interface",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("tcp" + "." + App.PNG)) {
            defaultIcons.add(new Pair<>("TcpInterface",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("udp" + "." + App.PNG)) {
            defaultIcons.add(new Pair<>("UdpInterface",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("os" + "." + App.PNG)) {
            defaultIcons.add(new Pair<>("OperatingSystem",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("pf" + "." + App.PNG)) {
            defaultIcons.add(
                    new Pair<>("Pf", new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("pf_conf" + "." + App.PNG)) {
            defaultIcons.add(new Pair<>("PfConfiguration",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("pf_rule" + "." + App.PNG)) {
            defaultIcons.add(
                    new Pair<>("PfRule", new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("port_range" + "." + App.PNG)) {
            defaultIcons.add(new Pair<>("PortRange",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("service" + "." + App.PNG)) {
            defaultIcons.add(new Pair<>("Service",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("service_list" + "." + App.PNG)) {
            defaultIcons.add(new Pair<>("ServiceList",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("software" + "." + App.PNG)) {
            defaultIcons.add(new Pair<>("Software",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("software_inventory" + "." + App.PNG)) {
            defaultIcons.add(new Pair<>("SoftwareInventory",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class.getResourceAsStream("network.png")) {
            defaultIcons.add(new Pair<>("Network",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("network_zone.png")) {
            defaultIcons.add(new Pair<>("NetworkZone",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("service_spec" + "." + App.PNG)) {
            defaultIcons.add(new Pair<>("ServiceSpecification", // $NON-NLS-2$
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class.getResourceAsStream("dns_server.png")) {
            defaultIcons.add(new Pair<>("DnsServer",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class
                .getResourceAsStream("routing_device.png")) {
            defaultIcons.add(new Pair<>("RoutingDevice",
                    new Pair<>(in.readAllBytes(), "png"))); // $NON-NLS-3$
        } catch (IOException e) {
            this.showError(e);
        }
        try (InputStream in = App.class.getResourceAsStream("default.png")) {
            this.iconDatabase = new IconDatabase(programdir, in.readAllBytes(),
                    "png", defaultIcons); // $NON-NLS-2$
        } catch (IOException | IconDatabaseException e) {
            this.showError(e);
        }
        this.controller = new ApplicationController(this, this.filesaver,
                this.database, new RDFInterpreterFactory(), this.iconDatabase);
        assert (this.controller != null);
        this.setFileChooserImpConf();
        this.setFileChooserExpConf();
        this.setFileChooserExpImage();
        this.setFileChooserExpPDF();
        this.setFileChooserOnto();
        this.setFileChooserImpIcon();
    }

    @Override
    public void init() throws IconDatabaseException {
        //
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(App.class.getResource("gui.fxml")); //$NON-NLS-1$

        App controller = this;
        loader.setController(controller);
        Pane newLoadedPane = loader.load();

        this.scene = new Scene(newLoadedPane);
        stage.getIcons().add(this.tolowizIcon);
        stage.setTitle("ToloWiz"); //$NON-NLS-1$
        stage.setScene(this.scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            this.controller.exitProgram();
            event.consume();
        });

        ImageView save = new ImageView(
                new Image(App.class.getResourceAsStream("saveButton.png")));
        save.setFitHeight(10);
        save.setFitWidth(10);
        this.saveButton.setTooltip(new Tooltip("Save Configuration"));
        this.saveButton.setGraphic(save);

        ImageView undo = new ImageView(
                new Image(App.class.getResourceAsStream("undoButton.png")));
        undo.setFitHeight(10);
        undo.setFitWidth(10);
        this.undoButton.setTooltip(new Tooltip("Undo"));
        this.undoButton.setGraphic(undo);

        ImageView redo = new ImageView(
                new Image(App.class.getResourceAsStream("redoButton.png")));
        redo.setFitHeight(10);
        redo.setFitWidth(10);
        this.redoButton.setTooltip(new Tooltip("Redo"));
        this.redoButton.setGraphic(redo);

        MenuItem res100 = new MenuItem("Resolution 1");
        res100.setOnAction(event -> this.exportAsImage(1.0));
        MenuItem res125 = new MenuItem("Resolution 1.25");
        res125.setOnAction(event -> this.exportAsImage(1.25));
        MenuItem res150 = new MenuItem("Resolution 1.5");
        res150.setOnAction(event -> this.exportAsImage(1.5));
        MenuItem res175 = new MenuItem("Resolution 1.75");
        res175.setOnAction(event -> this.exportAsImage(1.75));
        MenuItem res200 = new MenuItem("Resolution 2");
        res200.setOnAction(event -> this.exportAsImage(2.0));
        MenuItem res225 = new MenuItem("Resolution 2.25");
        res225.setOnAction(event -> this.exportAsImage(2.25));
        MenuItem res250 = new MenuItem("Resolution 2.5");
        res250.setOnAction(event -> this.exportAsImage(2.5));
        this.exportAsImage.getItems().addAll(res100, res125, res150, res175,
                res200, res225, res250);

        this.stackPane.getChildren().add(this.tabPaneAnchor);
        this.tabPaneAnchor.toFront();
        this.height = this.tabPaneAnchor.getHeight();
        this.width = this.tabPaneAnchor.getWidth();
        this.leavePresentingMode();

        for (Tab tab : this.configurationTabs.getTabs()) {
            tab.setOnSelectionChanged(
                    event -> this.configurationTabChanged(tab));
        }
        this.eventsActive = true;
        this.scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<>() {
            boolean pIsPressed = false;
            boolean sIsPressed = false;

            @Override
            public void handle(KeyEvent event) {
                if ((event.getCode() == KeyCode.DOWN)
                        || (event.getCode() == KeyCode.UP)
                        || (event.getCode() == KeyCode.LEFT)
                        || (event.getCode() == KeyCode.RIGHT)) {
                    App.this.handleKey(event);
                    event.consume();
                    this.pIsPressed = false;
                    this.sIsPressed = false;
                } else if (event.getCode() == KeyCode.P) {
                    this.pIsPressed = true;
                } else if ((event.getCode() == KeyCode.S) && this.pIsPressed) {
                    this.sIsPressed = true;
                } else if ((event.getCode() == KeyCode.E) && this.pIsPressed
                        && this.sIsPressed) {
                    App.this.scene.getStylesheets().add(App.class
                            .getResource("secretUnicornTheme.css").toString());
                    this.pIsPressed = false;
                    this.sIsPressed = false;
                } else {
                    this.pIsPressed = false;
                    this.sIsPressed = false;
                }
            }
        });
    }

    @Override
    public void runLater(Runnable run) {
        Platform.runLater(run);
        // run.run();
    }

    private void runConcurrent(Runnable run) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    run.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(task).start();
        // run.run();
    }

    private void runProgress(Runnable run) {

        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setStyle(" -fx-progress-color: #b200ff;");
        indicator.setMaxHeight(50);
        indicator.setMaxWidth(50);

        this.progress = new StackPane();
        this.progress.setAlignment(Pos.CENTER);
        this.progress.setStyle("-fx-background-color: rgba(160,160,160,0.7)");
        this.progress.getChildren().add(indicator);
        this.progress.toFront();

        this.stackPane.getChildren().add(this.progress);

        this.runConcurrent(() -> {
            run.run();
            this.endProgressBar();
        });
    }

    private void handleKey(KeyEvent event) {
        if (this.tab != null) {
            this.tab.getGraph().handleArrowKeys(event);
        }
    }

    @Override
    public void lockGui() {
        this.progress = new StackPane();
        this.progress.setAlignment(Pos.CENTER);
        this.progress.setStyle("-fx-background-color: rgba(160,160,160,0.7)");
        this.progress.toFront();
        this.stackPane.getChildren().addAll(this.progress);
    }

    @Override
    public void unlockGui() {
        this.stackPane.getChildren().removeAll(this.progress);
    }

    @Override
    public TabInterface createTab(TabControllerInterface controller,
            Configuration conf, GraphControllerInterface graphCont,
            String tabName) {
        assert (conf != null);
        MultiGraph mg = new MultiGraph("embedded");
        Graph graph = new Graph(mg,
                new FxViewer(mg, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD),
                new FxGraphRenderer(), conf, graphCont, this);
        ToloTab tab = new ToloTab(this, controller, this.configurationArea,
                graph);
        this.runLater(() -> this.tabPane.getTabs().add(tab));
        tab.setText(tabName);
        tab.setOnCloseRequest(event -> {
            this.closeOntology();
            event.consume();
        });
        assert (controller != null);
        this.changeTab(tab, controller, conf, graphCont);
        tab.setOnSelectionChanged(event -> {
            assert (controller != null);
            this.changeTab(tab, controller, conf, graphCont);
        });
        tab.setContent(graph);
        graph.setTab(tab);
        return tab;

    }

    /**
     * Shows the graph inside of a tab.
     *
     * @param tab The tab.
     * @param conf The configuration the graph should be shown with.
     * @param cont The graph controller.
     */
    void showGraph(TabInterface tab, Configuration conf,
            GraphControllerInterface cont) {
        ToloTab tab1 = (ToloTab) tab;
        MultiGraph mg = new MultiGraph("embedded");
        Graph graph = new Graph(mg,
                new FxViewer(mg, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD),
                new FxGraphRenderer(), conf, cont, this);
        tab1.setContent(graph);
        graph.setTab(tab1);
        this.refresh();
    }

    @Override
    public void closeApplication() {
        this.scene.getWindow().hide();
    }

    @Override
    public void showError(Exception e) {
        this.runLater(() -> {
            this.warningBar
                    .setText("Click here to show more: " + e.getMessage());
            this.warningBar.setStyle("-fx-text-fill: red;");
            this.warningBar.setTooltip(new Tooltip("Show More"));
            this.warningBar.setOnMouseClicked(event -> {
                this.showWarningPopUp(e);
            });
        });
    }

    private void showWarningPopUp(Exception e) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource("warningPopUp.fxml")); //$NON-NLS-1$
            App controller = this;
            loader.setController(controller);
            root = loader.load();
        } catch (IOException ex) {
            this.showError(ex);
        }
        this.warningPopUpText.setText(e.toString() + "\n"
                + Arrays.asList(e.getStackTrace()).stream()
                        .map(ste -> ste.toString())
                        .collect(Collectors.joining("\n ")));
        this.stage = new Stage();
        this.stage.getIcons().add(this.tolowizIcon);
        this.stage.setTitle("Warning!");
        this.stage.setScene(new Scene(root));
        this.stage.setOnCloseRequest(event -> this.unlockGui());
        this.lockGui();
        this.stage.show();
    }

    @FXML
    private void closeWarningPopUp() {
        this.unlockGui();
        this.stage.hide();
    }

    private void resetWarningBar() {
        this.warningBar.setText("");
        this.warningBar.setTooltip(null);
    }

    /**
     * Is called when the user clicks on the menu entry 'Open Ontology' in the
     * GUI and opens a file-menu where the user can select an ontology to be
     * opened. Then calls the openOntology method in the ApplicationController
     * and passes the file, which was selected by the user, on to the
     * controller.
     */
    @FXML
    private void openOntology() {
        this.resetWarningBar();
        this.lockGui();
        assert (this.controller != null);
        File file = this.fileChooserOnto.showOpenDialog(this.stage);
        this.unlockGui();
        if (file != null) {
            this.runProgress(
                    () -> App.this.controller.openOntology(file.toPath()));
            this.fileChooserOnto.setInitialDirectory(file.getParentFile());

        }
    }

    private void endProgressBar() {
        this.runLater(() -> this.stackPane.getChildren().remove(this.progress));
    }

    /**
     * Is called when the user clicks on the menu entry 'Import Configuration'
     * in the GUI and opens a file-menu where the user can select a
     * configuration to import. Then calls the importConfiguration method in the
     * TabController and passes the file, which was selected by the user, on to
     * the controller.
     */
    @FXML
    private void importConfiguration() {
        this.resetWarningBar();
        this.lockGui();
        File file = this.fileChooserImpConf.showOpenDialog(this.stage);
        this.unlockGui();
        if (file != null) {

            this.runProgress(
                    () -> this.controller.importConfiguration(file.toPath()));
            this.fileChooserImpConf.setInitialDirectory(file.getParentFile());
        }
    }

    /**
     * Is called when the user clicks on the menu entry 'Export Configuration'
     * in the GUI and calls the exportConfiguration method in the TabController.
     */
    @FXML
    private void exportConfiguration() {
        this.lockGui();
        File file = this.fileChooserExpConf.showSaveDialog(this.stage);
        this.unlockGui();
        if (file != null) {
            this.tabController.exportConfiguration(file.toPath(), false);
            this.fileChooserExpConf.setInitialDirectory(file.getParentFile());
        }
    }

    /**
     * Is called when the user clicks on the menu entry 'Export As Image' in the
     * GUI and calls the exportVisualizationImage method in the TabController.
     *
     * @param resolution the selected resolution
     */
    private void exportAsImage(double resolution) {
        this.lockGui();
        File file = this.fileChooserExpImage.showSaveDialog(this.stage);
        this.unlockGui();
        if (file != null) {
            ImageFiletype ift = ImageFiletype.valueOf(this.fileChooserExpImage
                    .getSelectedExtensionFilter().getDescription());
            this.tabController.exportVisualizationImage(file.toPath(),
                    resolution, ift);
            this.fileChooserExpImage.setInitialDirectory(file.getParentFile());
        }
    }

    @FXML
    private void exportAsPDF() {
        this.lockGui();
        File file = this.fileChooserExpPDF.showSaveDialog(this.stage);
        this.unlockGui();
        if (file != null) {
            this.tabController.exportVisualizationDocument(file.toPath());
            this.fileChooserExpPDF.setInitialDirectory(file.getParentFile());
        }
    }

    @Override
    public void selectUri(Set<String> suggestedUris) {
        Stage stage = new Stage();
        stage.getIcons().add(this.tolowizIcon);
        stage.setTitle("Select URI for Ontology");
        AnchorPane pane = new AnchorPane();
        Label warningBarUri = new Label();
        ToolBar toolBar = new ToolBar();
        VBox vBox = new VBox();
        vBox.getChildren().addAll(toolBar, warningBarUri);
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setEditable(true);
        comboBox.getItems().addAll(suggestedUris);
        Button button = new Button("OK");
        button.setOnAction(event -> {
            if (!comboBox.getValue().equals("")) {
                this.controller.setUriPrefix(comboBox.getValue());
                stage.hide();
            } else {
                warningBarUri.setText("URI can not be empty");
                warningBarUri.setStyle("-fx-text-fill: #b200ff;");
            }
        });
        AnchorPane.setBottomAnchor(vBox, 0.0);
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setLeftAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
        toolBar.getItems().addAll(comboBox, button);
        pane.getChildren().add(vBox);
        stage.setScene(new Scene(pane));
        stage.setOnCloseRequest(event -> {
            event.consume();
            warningBarUri.setText("Ontology needs an URI");
            warningBarUri.setStyle("-fx-text-fill: #b200ff;");
        });
        stage.show();
    }

    /**
     * Is called when the user clicks on the menu entry 'Close Ontology' or on
     * the small x on a tab in the GUI and calls the closeOntology method in the
     * TabController.
     */
    @FXML
    private void closeOntology() {
        this.tabController.closeOntology();
        if ((this.tabPane == null) || this.tabPane.getTabs().isEmpty()) {
            this.tab = null;
            this.tabController = null;
            this.detailArea.setContent(null);
            this.relationsArea.setContent(null);
            this.typesArea.setContent(null);
            this.valuesArea.setContent(null);
            this.hiddenInstancesVBox.getChildren().clear();
            this.groupsVBox.getChildren().clear();
        }
    }

    /**
     * Is called when the user clicks on the menu entry 'Restore Standard
     * Alignment' in the GUI and calls the restoreStandardAlignment method in
     * the GraphController.
     */
    @FXML
    private void restoreStandardAlignment() {
        this.graphController.restoreStandardAlignment();
    }

    /**
     * Is called when the user clicks on the menu entry 'Restore Standard View'
     * in the GUI and calls the restoreStandardView method in the
     * GraphController.
     */
    @FXML
    private void restoreStandardView() {
        this.graphController.restoreStandardView();
    }

    /**
     * Is called when the user clicks on the menu entry 'Load Configuration' in
     * the GUI and shows a file-menu with configurations to choose from. Then
     * calls the loadConfiguration method in the TabController and passes the
     * file, which was selected by the user, on to the controller.
     */
    @FXML
    private void loadConfiguration() {
        this.resetWarningBar();
        Parent root = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(
                    App.class.getResource("loadConfiguration.fxml"));
            loader.setController(this);
            root = loader.load();
        } catch (IOException e) {
            this.showError(e);
        }
        Stage stage = new Stage();
        stage.getIcons().add(this.tolowizIcon);
        stage.setTitle("Load Configuration");
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(event -> this.unlockGui());
        this.lockGui();
        stage.show();
        VBox vBox = new VBox(6);
        vBox.setPadding(new Insets(6.0, 6.0, 6.0, 6.0));
        try {
            for (String id : this.database.getStorageIDs()) {
                Button button = new Button(
                        this.database.getStorageByID(id).getOntologyName());
                button.setOnAction(
                        event -> this.loadConfigurationForOnto(stage, id));
                vBox.getChildren().add(button);
            }
        } catch (InternalDatabaseException | NoSuchEntryException e) {
            this.showError(e);
        }
        this.loadConfiguration.setContent(vBox);
    }

    private void loadConfigurationForOnto(Stage stage, String storageID) {
        Parent root = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(
                    App.class.getResource("loadConfiguration.fxml"));
            loader.setController(this);
            root = loader.load();
        } catch (IOException e) {
            this.showError(e);
        }
        stage.getIcons().add(this.tolowizIcon);
        stage.setTitle("Load Configuration For Ontology " + storageID);
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(event -> this.unlockGui());
        stage.show();
        VBox vBox = new VBox(6);
        vBox.setPadding(new Insets(6.0, 6.0, 6.0, 6.0));
        StorageInterface si = null;
        try {
            si = this.database.getStorageByID(storageID);
        } catch (InternalDatabaseException | NoSuchEntryException e) {
            this.showError(e);
        }
        try {
            if (si.existsAutosave()) {
                Button autosave = new Button("Autosave");
                autosave.setOnAction(event -> {
                    this.unlockGui();
                    stage.hide();
                    this.runProgress(() -> {
                        this.controller.loadAutosave(storageID);
                    });
                });
                vBox.getChildren().add(autosave);
            }
            for (String configName : si.getEntrys()) {
                Button button = new Button(configName);
                button.setOnAction(event -> {
                    this.unlockGui();
                    stage.hide();
                    this.runProgress(() -> {
                        this.controller.loadConfiguration(storageID,
                                configName);
                    });
                });
                vBox.getChildren().add(button);
            }
        } catch (InternalDatabaseException e) {
            this.showError(e);
        }

        Button back = new Button("Back");
        back.setOnAction(event -> {
            this.unlockGui();
            stage.hide();
            this.loadConfiguration();
        });

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(javafx.geometry.Orientation.VERTICAL);

        splitPane.setPadding(new Insets(6, 6, 6, 6));
        splitPane.getItems().addAll(new ToolBar(back), vBox);

        this.loadConfiguration.setContent(splitPane);
    }

    /**
     * Is called when the user clicks on the menu entry 'Save Configuration' in
     * the GUI and calls the saveConfiguration method in the TabController.
     */
    @FXML
    private void saveConfiguration() {
        Parent root = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(
                    App.class.getResource("saveConfiguration.fxml"));
            loader.setController(this);
            root = loader.load();
        } catch (IOException e) {
            this.showError(e);
        }
        Stage stage = new Stage();
        stage.getIcons().add(this.tolowizIcon);
        stage.setTitle("Save Configuration");
        stage.setScene(new Scene(root));

        this.saveConfigurationButton.setOnAction(event -> {
            String name = this.configurationTextField.getText();
            if (name.trim().isEmpty()) {
                this.saveConfigWarningBar.setText("Configuration needs a name");
                this.saveConfigWarningBar.setStyle("-fx-text-fill: #b200ff;");
            } else if (name.equals("Autosave") || name.equals("autosave")) {
                this.saveConfigWarningBar
                        .setText("Configuration can not be named " + name);
                this.saveConfigWarningBar.setStyle("-fx-text-fill: #b200ff;");

            } else {
                try {
                    if (this.database
                            .getStorageByID(this.graphController
                                    .getConfiguration().getOntology().getIRI())
                            .getEntrys().contains(name)) {
                        this.saveConfigWarningBar.setText(
                                "Name already exists for this Ontology");
                        this.saveConfigWarningBar
                                .setStyle("-fx-text-fill: #b200ff;");
                    } else {
                        assert (this.tabController != null);
                        this.tabController.saveConfiguration(name);
                        this.unlockGui();
                        stage.hide();
                    }
                } catch (InternalDatabaseException | NoSuchEntryException e) {
                    this.showError(e);
                }
            }
        });

        this.cancelSaveConfigurationButton.setOnAction(event -> {
            this.unlockGui();
            stage.hide();
        });

        stage.setOnCloseRequest(event -> this.unlockGui());
        this.lockGui();
        stage.show();
    }

    /**
     * Is called when the user clicks on the menu entry 'Font Size' in the GUI
     * and shows a menu to set the font size. Then applies the changes to the
     * GUI.
     */
    @FXML
    private void changeFontSize() {
        RadioMenuItem selected = (RadioMenuItem) this.fontSize
                .getSelectedToggle();
        this.scene.getStylesheets().clear();
        switch (selected.getId()) {
        case "size6":
            this.scene.getStylesheets()
                    .add(App.class.getResource("fontsize6.css").toString());
            break;
        case "size7":
            this.scene.getStylesheets()
                    .add(App.class.getResource("fontsize7.css").toString());
            break;
        case "size8":
            this.scene.getStylesheets()
                    .add(App.class.getResource("fontsize8.css").toString());
            break;
        case "size9":
            this.scene.getStylesheets()
                    .add(App.class.getResource("fontsize9.css").toString());
            break;
        case "size10":
            this.scene.getStylesheets()
                    .add(App.class.getResource("fontsize10.css").toString());
            break;
        case "size11":
            this.scene.getStylesheets()
                    .add(App.class.getResource("fontsize11.css").toString());
            break;
        case "size12":
            this.scene.getStylesheets()
                    .add(App.class.getResource("fontsize12.css").toString());
            break;
        case "size13":
            this.scene.getStylesheets()
                    .add(App.class.getResource("fontsize13.css").toString());
            break;
        case "size14":
            this.scene.getStylesheets()
                    .add(App.class.getResource("fontsize14.css").toString());
            break;
        case "size15":
            this.scene.getStylesheets()
                    .add(App.class.getResource("fontsize15.css").toString());
            break;
        case "size16":
            this.scene.getStylesheets()
                    .add(App.class.getResource("fontsize16.css").toString());
            break;
        default:
            //
        }
    }

    @FXML
    private void switchToPresentingMode() {
        this.inPresentingMode = true;
        this.stackPane.getChildren().add(this.tabPaneAnchor);
        this.tabPaneAnchor.toFront();
        this.height = this.tabPaneAnchor.getHeight();
        this.width = this.tabPaneAnchor.getWidth();
        this.tabPaneAnchor.setPrefSize(this.vBoxAll.getWidth(),
                this.vBoxAll.getHeight());
        this.leavePresentingMode.setVisible(true);
        this.leavePresentingMode.setDisable(false);
        this.leavePresentingMode.setOnAction(event -> {
            this.leavePresentingMode();
        });
        this.stage.setFullScreen(true);
    }

    /**
     * Is called when the user clicks on the 'Leave Presenting Mode' icon, when
     * the GUI is in presenting mode and switches from the presenting mode back
     * to the normal view.
     */
    private void leavePresentingMode() {
        this.stage.setFullScreen(false);
        this.tabPaneAnchor.setPrefSize(this.width, this.height);
        this.leavePresentingMode.setVisible(false);
        this.leavePresentingMode.setDisable(true);
        this.splitPaneWithTabs.getItems().set(1, this.tabPaneAnchor);
        this.inPresentingMode = false;
    }

    /**
     * Is called when the user clicks on the menu entry 'Help' in the GUI and
     * shows a window with a help-file.
     */
    @FXML
    private void showHelp() {
        Parent parent = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(App.class.getResource("help.fxml"));
            loader.setController(this);
            parent = loader.load();

        } catch (IOException e) {
            this.showError(e);
        }
        Stage stage = new Stage();
        stage.getIcons().add(this.tolowizIcon);
        stage.setTitle("Help");
        stage.setScene(new Scene(parent));

        loader.setController(this);
        stage.show();
        WebView browser = this.helpWebView;
        WebEngine webEngine = browser.getEngine();
        assert (App.class.getResource("help.html") != null);
        webEngine.load(App.class.getResource("help.html").toString());
    }

    /**
     * Is called when the user clicks on the menu entry 'Credits' in the GUI and
     * shows the credits.
     */
    @FXML
    private void showCredits() {
        Parent parent = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(App.class.getResource("credits.fxml"));
            loader.setController(this);
            parent = loader.load();

        } catch (IOException e) {
            this.showError(e);
        }
        Stage stage = new Stage();
        stage.getIcons().add(this.tolowizIcon);
        stage.setTitle("Credits");
        stage.setScene(new Scene(parent));

        loader.setController(this);
        stage.show();
        WebView browser = this.creditsWebView;
        WebEngine webEngine = browser.getEngine();
        webEngine.load(App.class.getResource("credits.html").toString());

    }

    /**
     * Is called when the user clicks on another Tab in the GUI, changes the Tab
     * and sets the tabController attribute to fit to the new current Tab. Also
     * changes the value-area and the configuration-area in the GUI.
     */
    private void changeTab(ToloTab tab, TabControllerInterface tabController,
            Configuration conf, GraphControllerInterface graphController) {
        this.setMenu();
        assert (tabController != null);
        if (tab.isSelected()) {
            this.runProgress(() -> {
                assert (tabController != null);
                this.tabController = tabController;
                this.tab = tab;
                this.graphController = graphController;
                this.setTypesTab(conf.getRootType());
                this.setRelationsTab(conf.getRelationTypes());
                this.setValuesTab(conf.getRootType());
                this.setDetailArea(tab);
                this.setConfigurationArea(tab);
                this.runLater(() -> {
                    this.setGroupsTab();
                    this.setHiddenInstances(conf);
                });
                this.refresh();
            });
        }
    }

    /**
     * Is called when the user checks or un-checks a checkbox in the 'Type' tab
     * in the configuration-area of the GUI and calls the showInstanceType
     * method or the hideInstanceType method in the GraphController according to
     * the state of the checkbox.
     */
    private void checkInstanceType(CheckBoxTreeItem<String> checkBox,
            String iri) {
        InstanceTypeConfiguration itc = this.tab.getGraph().getConfiguration()
                .getTypeByIRI(iri);
        if ((!checkBox.isIndeterminate()) && checkBox.isSelected()) {
            this.graphController.showInstanceType(itc);
        } else {
            this.graphController.hideInstanceType(itc);
        }
    }

    /**
     * Is called when the user checks or un-checks a checkbox in the 'Relation'
     * tab in the configuration-area of the GUI and calls the showRelationType
     * method or the hideRelationType method in the GraphController according to
     * the state of the checkbox.
     */
    private void checkRelationType(CheckBoxTreeItem<String> checkBox,
            String name) {
        RelationTypeConfiguration rtc = this.tab.getGraph().getConfiguration()
                .getRelationTypeByName(name);
        if ((!checkBox.isIndeterminate()) && checkBox.isSelected()) {
            this.graphController.showRelationType(rtc);
        } else {
            this.graphController.hideRelationType(rtc);
        }
    }

    /**
     * Is called when the user checks or un-checks a checkbox of a type in the
     * 'Values' tab in the configuration-area of the GUI and calls the
     * activateValue method or the deactivateValue method in the GraphController
     * according to the state of the checkbox.
     */
    private void checkValueType(CheckBoxTreeItem<String> checkBox,
            String typeIRI, String valueURI) {
        InstanceTypeConfiguration itc = this.tab.getGraph().getConfiguration()
                .getTypeByIRI(typeIRI);
        ValueType vt = this.tab.getGraph().getConfiguration()
                .getValueTypeByURI(valueURI);
        if ((!checkBox.isIndeterminate()) && checkBox.isSelected()) {
            this.graphController.activateValue(itc, vt);
        } else {
            this.graphController.deactivateValue(itc, vt);
        }
    }

    /**
     * Is called when the user changes the symbol for a type in the 'Type' tab
     * in the configuration-area of the GUI and calls the
     * changeInstanceTypeSymbol method in the GraphController.
     */
    private void changeInstanceTypeSymbol(InstanceTypeConfiguration itc,
            IconInterface icon) {
        if (this.eventsActive) {
            this.graphController.changeInstanceTypeSymbol(itc, icon);
        }
    }

    /**
     * Is called when the user changes the symbol for a relation in the
     * 'Relation' tab in the configuration-area of the GUI and calls the
     * changeRelationTypeSymbol method in the GraphController.
     */
    private void changeRelationTypeSymbol(RelationTypeConfiguration rtc,
            RelationStyle style) {
        if (this.eventsActive) {
            this.graphController.changeRelationTypeSymbol(rtc, style);
        }
    }

    /**
     * Is called when the user clicks on 'Show Values' in the context menu of
     * this node of the graph and calls the showActivatedValues method in the
     * GraphController.
     */
    @Override
    public void showActivatedValues(InstanceConfiguration ic) {
        if (this.tabPane.getTabs() != null) {
            StringBuilder sBuilder = new StringBuilder();
            for (Pair<String, String> pair : ic.getValues()) {
                sBuilder.append(pair.getValue0());
                sBuilder.append(": ");
                sBuilder.append(pair.getValue1());
                sBuilder.append("\n");
            }
            String s = sBuilder.toString();
            TextArea textArea = new TextArea(s);
            textArea.setEditable(false);
            this.detailArea.setContent(textArea);
            this.tab.setDetails(this.detailArea);
        } else {
            TextArea textArea = new TextArea();
            textArea.setEditable(false);
            this.detailArea.setContent(textArea);
        }

    }

    private void generateTypesForGroupCreation(
            InstanceTypeConfiguration rootType, CheckBoxTreeItem<String> root,
            HashMap<CheckBoxTreeItem<String>, InstanceConfiguration> map,
            HashSet<InstanceConfiguration> set) {
        for (InstanceTypeConfiguration itc : rootType.getSubTypes()) {
            CheckBoxTreeItem<String> current = new CheckBoxTreeItem<>(
                    itc.getName());
            current.setExpanded(true);
            root.getChildren().add(current);
            if (itc.getDirectMembers() != null) {
                this.generateInstancesForGroupCreation(itc, current, map, set);
            }
            if (!itc.getSubTypes().isEmpty()) {
                this.generateTypesForGroupCreation(itc, current, map, set);
            }
        }
    }

    private void generateInstancesForGroupCreation(
            InstanceTypeConfiguration itc, CheckBoxTreeItem<String> checkbox,
            HashMap<CheckBoxTreeItem<String>, InstanceConfiguration> map,
            HashSet<InstanceConfiguration> set) {
        for (InstanceConfiguration ic : itc.getDirectMembers()) {
            CheckBoxTreeItem<String> current = new CheckBoxTreeItem<>(
                    ic.getName());
            current.setExpanded(true);
            map.put(current, ic);
            current.addEventHandler(
                    CheckBoxTreeItem.<String>checkBoxSelectionChangedEvent(),
                    new EventHandler<TreeModificationEvent<String>>() {

                        @Override
                        public void handle(
                                TreeModificationEvent<String> event) {
                            if (current.isSelected()
                                    && !current.isIndeterminate()) {
                                set.add(ic);
                            } else {
                                set.remove(ic);
                            }
                        }
                    });

            checkbox.getChildren().add(current);
        }
    }

    /**
     * Is called when the user clicks on the 'Create New Group' button in the
     * 'Group' tab in the configuration-area of the GUI and calls the
     * createNewGroup method in the GraphController.
     */
    private void createNewGroup() {

        final HashMap<CheckBoxTreeItem<String>, InstanceConfiguration> map = new HashMap<>();
        final HashSet<InstanceConfiguration> set = new HashSet<>();

        CheckBoxTreeItem<String> root = new CheckBoxTreeItem<>(
                this.tabController.getConfig().getRootType().getName());
        root.setExpanded(true);
        TreeView<String> treeView = new TreeView<>(root);
        this.generateTypesForGroupCreation(
                this.tabController.getConfig().getRootType(), root, map, set);

        Parent parent = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(App.class.getResource("newGroup.fxml"));
            loader.setController(this);
            parent = loader.load();

        } catch (IOException e) {
            this.showError(e);
        }
        Stage stage = new Stage();
        stage.getIcons().add(this.tolowizIcon);
        stage.setTitle("Choose Members For Group");
        stage.setScene(new Scene(parent));
        stage.setOnCloseRequest(event -> {
            this.resetWarningBar();
            this.unlockGui();
        });

        loader.setController(this);
        this.lockGui();
        stage.show();
        treeView.setCellFactory(
                new Callback<TreeView<String>, TreeCell<String>>() {

                    @Override
                    public TreeCell<String> call(TreeView<String> param) {
                        return new CheckBoxTreeCell<>() {

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    this.setGraphic(null);
                                    this.setText(null);
                                } else if ((map
                                        .containsKey(this.getTreeItem()))) {
                                    this.setStyle("-fx-text-fill: #b200ff;");
                                } else {
                                    this.setStyle("-fx-text-fill: black;");
                                }
                            }
                        };
                    }
                });
        this.groupsVBoxPane.getChildren().add(1, treeView);
        VBox.setVgrow(treeView, Priority.ALWAYS);

        this.createGroupButton.setOnAction(event -> {
            String name = this.groupName.getText();
            if (name.trim().isEmpty()) {
                this.runLater(() -> {
                    this.warningBarGroups.setText("Group needs a name");
                    this.warningBarGroups.setStyle("-fx-text-fill: #b200ff;");
                });
            } else if ((this.tabController.getConfig().getGroups()).stream()
                    .map(Group::getName).anyMatch(name::equals)) {
                this.runLater(() -> {
                    this.warningBarGroups.setText("Name already exists");
                    this.warningBarGroups.setStyle("-fx-text-fill: #b200ff;");
                });

            } else {
                this.graphController.createNewGroup(name, set);
                this.setGroupsTab();
                this.unlockGui();
                stage.hide();
            }
            this.resetWarningBar();
        });

    }

    /**
     * Is called when the user hides a node in the context menu of this node of
     * the graph and calls the hideInstance method in the GraphController.
     */
    @Override
    public void hideInstance(Configuration config) {
        this.setHiddenInstances(config);

    }

    /**
     * Is called when the user clicks on the 'Undo' button in the GUI and calls
     * the undo method in the TabController.
     */
    @FXML
    private void undo() {
        try {
            this.tabController.undo();
        } catch (@SuppressWarnings("unused") UndoException e) {
            // undo is not possible => do nothing
        }
    }

    /**
     * Is called when the user clicks on the 'Redo' button in the GUI and calls
     * the redo method in the TabController.
     */
    @FXML
    private void redo() {
        try {
            this.tabController.redo();
        } catch (@SuppressWarnings("unused") UndoException e) {
            // redo is not possible => do nothing
        }
    }

    private void setFileChooserImpConf() {
        this.fileChooserImpConf.setTitle("Import Configuration");
        this.fileChooserImpConf.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("configuration", "*.tolo"));
    }

    private void setFileChooserExpConf() {
        this.fileChooserExpConf.setTitle("Export Configuration");
        this.fileChooserExpConf.setInitialFileName("Ontology_Configuration");
        this.fileChooserExpConf.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("configuration", "*.tolo"));
    }

    private void setFileChooserExpImage() {
        this.fileChooserExpImage.setTitle("Export Image");
        this.fileChooserExpImage.setInitialFileName("image");
        this.fileChooserExpImage.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        this.fileChooserExpImage.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("SVG", "*.svg"));
    }

    private void setFileChooserExpPDF() {
        this.fileChooserExpPDF.setTitle("Export PDF");
        this.fileChooserExpPDF.setInitialFileName("document");
        this.fileChooserExpPDF.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
    }

    private void setFileChooserOnto() {
        this.fileChooserOnto.setTitle("Open Ontology");
        this.fileChooserOnto.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("RDF/XML", "*.rdf", "*.owl"),
                new FileChooser.ExtensionFilter("RDF", "*.rdf"),
                new FileChooser.ExtensionFilter("OWL", "*.owl"));
    }

    private void setFileChooserImpIcon() {
        this.fileChooserImpIcon.setTitle("Import Icon");
        this.fileChooserImpIcon.setInitialFileName("icon");
        this.fileChooserImpIcon.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Image", "*.png",
                        "*.jpg", "*.jpeg", "*.gif", "*.tiff"),
                        new FileChooser.ExtensionFilter("PNG", "*.png"),
                        new FileChooser.ExtensionFilter("GIF", "*.gif"),
                        new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                        new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                        new FileChooser.ExtensionFilter("TIFF", "*.tiff"));
    }

    private void setTypesTab(InstanceTypeConfiguration rootType) {
        this.treeItems = new HashMap<>();
        CheckBoxTreeItem<String> root = new CheckBoxTreeItem<>(
                rootType.getName());
        root.setIndependent(true);
        root.setExpanded(true);
        TreeView<String> treeView = new TreeView<>(root);
        final HashMap<CheckBoxTreeItem<String>, Pair<Boolean, HBox>> map = new HashMap<>();
        this.createCheckBoxInstanceTypeTree(rootType, root, map);

        treeView.setCellFactory(
                new Callback<TreeView<String>, TreeCell<String>>() {

                    @Override
                    public TreeCell<String> call(TreeView<String> param) {
                        return new CheckBoxTreeCell<>() {

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                this.disclosureNodeProperty().get()
                                        .setOnMousePressed(
                                                event -> event.consume());
                                this.disclosureNodeProperty().get()
                                        .setOnMouseClicked(
                                                event -> event.consume());
                                this.getDisclosureNode().setStyle(
                                        "-fx-padding: 0 0 0 0; -fx-opacity: 0");
                                this.addEventFilter(MouseEvent.MOUSE_PRESSED,
                                        (MouseEvent e) -> {
                                            if (((e.getClickCount() % 2) == 0)
                                                    && e.getButton().equals(
                                                            MouseButton.PRIMARY)) {
                                                e.consume();
                                            }
                                        });
                                this.setDisabled(false);
                                if (empty) {
                                    this.setGraphic(null);
                                    this.setText(null);
                                } else if (map
                                        .containsKey(this.getTreeItem())) {
                                    HBox newHBox = map.get(this.getTreeItem())
                                            .getValue1();
                                    if (!map.get(this.getTreeItem())
                                            .getValue0()) {
                                        this.setDisabled(true);
                                    } else {
                                        this.setDisabled(false);
                                        newHBox.getChildren().add(0,
                                                this.getGraphic());
                                    }
                                    this.setGraphic(newHBox);
                                    this.setText(null);
                                }
                            }
                        };
                    }

                });
        String iri = rootType.getIRI();
        root.addEventHandler(
                CheckBoxTreeItem.<String>checkBoxSelectionChangedEvent(),
                event -> {
                    event.consume();
                    if (this.eventsActive) {
                        this.checkInstanceType(root, iri);
                    }
                });
        this.runLater(() -> this.typesArea.setContent(treeView));
        this.runLater(() -> this.typesArea.setFitToHeight(true));
        this.runLater(() -> this.typesArea.setFitToWidth(true));
        this.treeItems.put(rootType.getIRI(), root);

    }

    private void markInstanceType(InstanceTypeConfiguration itc,
            InstanceMark mark) {
        for (InstanceConfiguration ic : itc.getDirectMembers()) {
            this.markInstance(ic, mark);
        }
    }

    private void markInstance(InstanceConfiguration ic, InstanceMark mark) {
        this.graphController.markInstance(ic, mark);
    }

    private void createCheckBoxInstanceTypeTree(
            InstanceTypeConfiguration rootType, CheckBoxTreeItem<String> root,
            HashMap<CheckBoxTreeItem<String>, Pair<Boolean, HBox>> map) {
        for (InstanceTypeConfiguration itc : rootType.getSubTypes()) {
            CheckBoxTreeItem<String> current = new CheckBoxTreeItem<>(
                    itc.getName());
            current.setIndependent(true);
            current.setExpanded(true);
            root.getChildren().add(current);

            HBox hBox = new HBox(6);
            hBox.setAlignment(Pos.CENTER_LEFT);
            Label label = new Label(itc.getName());
            MenuButton button = new MenuButton();
            Menu changeIcon = new Menu("Change Icon");
            MenuItem importIcon = new MenuItem("Import Icon");

            Menu changeMark = new Menu("Change Mark");

            Menu changeStroke = new Menu("Change Stroke");
            Menu changeShape = new Menu("Change Shape");
            MenuItem changeColor = new MenuItem("Change Color");

            MenuItem strokeNone = new MenuItem("None");
            MenuItem strokePlain = new MenuItem("Plain");
            MenuItem strokeDashed = new MenuItem("Dashes");
            MenuItem strokeDotted = new MenuItem("Dots");

            MenuItem shapeCircle = new MenuItem("Circle");
            MenuItem shapeBox = new MenuItem("Box");
            MenuItem shapeRoundedBox = new MenuItem("Rounded-Box");
            MenuItem shapeDiamond = new MenuItem("Diamond");
            MenuItem shapeCross = new MenuItem("Cross");

            strokeNone.setOnAction(event -> {
                InstanceMark mark = new InstanceMark(
                        InstanceMark.InstanceStroke.NONE);
                this.markInstanceType(itc, mark);
            });
            strokePlain.setOnAction(event -> {
                InstanceMark mark = new InstanceMark(
                        InstanceMark.InstanceStroke.PLAIN);
                this.markInstanceType(itc, mark);
            });
            strokeDashed.setOnAction(event -> {
                InstanceMark mark = new InstanceMark(
                        InstanceMark.InstanceStroke.DASHES);
                this.markInstanceType(itc, mark);
            });
            strokeDotted.setOnAction(event -> {
                InstanceMark mark = new InstanceMark(
                        InstanceMark.InstanceStroke.DOTS);
                this.markInstanceType(itc, mark);
            });

            shapeCircle.setOnAction(event -> {
                InstanceMark mark = new InstanceMark(
                        InstanceMark.InstanceShape.CIRCLE);
                this.markInstanceType(itc, mark);
            });
            shapeBox.setOnAction(event -> {
                InstanceMark mark = new InstanceMark(
                        InstanceMark.InstanceShape.BOX);
                this.markInstanceType(itc, mark);
            });
            shapeRoundedBox.setOnAction(event -> {
                InstanceMark mark = new InstanceMark(
                        InstanceMark.InstanceShape.ROUNDED_BOX);
                this.markInstanceType(itc, mark);
            });
            shapeDiamond.setOnAction(event -> {
                InstanceMark mark = new InstanceMark(
                        InstanceMark.InstanceShape.DIAMOND);
                this.markInstanceType(itc, mark);
            });
            shapeCross.setOnAction(event -> {
                InstanceMark mark = new InstanceMark(
                        InstanceMark.InstanceShape.CROSS);
                this.markInstanceType(itc, mark);
            });

            ColorPicker colorWheel = new ColorPicker(
                    javafx.scene.paint.Color.BLACK);
            changeColor.setOnAction(event -> {
                Stage colorStage = new Stage();
                colorStage.getIcons().add(this.tolowizIcon);
                colorStage.setTitle("Choose Color");
                AnchorPane pane = new AnchorPane();
                AnchorPane.setBottomAnchor(colorWheel, 0.0);
                AnchorPane.setTopAnchor(colorWheel, 0.0);
                AnchorPane.setLeftAnchor(colorWheel, 0.0);
                AnchorPane.setRightAnchor(colorWheel, 0.0);
                pane.getChildren().add(colorWheel);
                colorStage.setScene(new Scene(pane, 100, 30));
                colorStage.setOnCloseRequest(ev -> this.unlockGui());
                this.lockGui();
                colorStage.show();
                this.cStage = colorStage;
            });

            colorWheel.setOnAction(event -> {
                int red = (int) (colorWheel.getValue().getRed() * 255);
                int green = (int) (colorWheel.getValue().getGreen() * 255);
                int blue = (int) (colorWheel.getValue().getBlue() * 255);
                Color color = new Color(red, green, blue);
                InstanceMark mark = new InstanceMark(color);
                this.markInstanceType(itc, mark);
                this.unlockGui();
                this.cStage.hide();
            });
            changeStroke.getItems().addAll(strokeNone, strokePlain,
                    strokeDashed, strokeDotted);

            changeShape.getItems().addAll(shapeCircle, shapeBox, shapeDiamond,
                    shapeCross);

            changeMark.getItems().addAll(changeStroke, changeShape,
                    changeColor);

            importIcon.setOnAction(event -> {
                this.importIcon(itc, changeIcon);
            }); // evtl this.eventsActive = true; hinzufügen
            this.updateIcons(itc, changeIcon);

            Menu handlers = new Menu("Display As...");
            for (DefaultHandler handler : this.tabController.getConfig()
                    .getHandlers()) {

                if (handler.checkLegality(itc)) {
                    MenuItem handlerMenu = new MenuItem(handler.getName());
                    handlerMenu.setOnAction(event -> {
                        this.graphController.setHandlerForType(itc, handler);

                    });
                    handlers.getItems().add(handlerMenu);
                }
            }

            button.getItems().addAll(importIcon, changeIcon, changeMark,
                    handlers);

            hBox.getChildren().addAll(label, button);
            boolean members = false;
            if (itc.getMembers().size() != 0) {
                members = true;
            }
            map.put(current, new Pair<>(members, hBox));

            current.addEventHandler(
                    CheckBoxTreeItem.<String>checkBoxSelectionChangedEvent(),
                    event -> {
                        event.consume();
                        if (this.eventsActive) {
                            this.checkInstanceType(current, itc.getIRI());
                        }
                    });

            if (!itc.getSubTypes().isEmpty()) {
                this.createCheckBoxInstanceTypeTree(itc, current, map);
            }
            // current.addEventHandler(CheckBoxTreeItem.<String>checkBoxSelectionChangedEvent(),
            // event -> {
            //
            // if (this.eventsActive) {
            // this.eventsActive = false;
            // event.consume();
            // InstanceTypeConfiguration tmp = null;
            // for (var x :
            // this.self.tab.getGraph().getConfiguration().getInstanceTypes())
            // {
            // if (x.getIRI().equals(rootType.getIRI())) {
            // tmp = x;
            // }
            // }
            // this.checkInstanceType(root, tmp);
            // this.eventsActive = true;
            // }
            // });

            this.treeItems.put(itc.getIRI(), current);
        }
    }

    private void updateIcons(InstanceTypeConfiguration itc, Menu menu) {
        try {
            for (IconInterface icon : this.iconDatabase
                    .getByType(itc.getInstanceType())) {
                MenuItem newIcon = new MenuItem();
                File file = icon.getPath().toFile();
                newIcon.setOnAction(event -> {
                    if (this.eventsActive) {
                        this.changeInstanceTypeSymbol(itc, icon);
                    }

                });
                String localUrl = null;
                try {
                    localUrl = file.toURI().toURL().toString();
                } catch (MalformedURLException e) {
                    this.showError(e);
                }

                Image image = new Image(localUrl);
                ImageView iv = new ImageView(image);
                iv.setFitHeight(50);
                iv.setFitWidth(50);
                newIcon.setGraphic(iv);
                menu.getItems().add(newIcon);
            }
        } catch (IconDatabaseException e2) {
            this.showError(e2);
        }
    }

    private void importIcon(InstanceTypeConfiguration itc, Menu menu) {
        this.lockGui();
        File file = this.fileChooserImpIcon.showOpenDialog(this.stage);
        this.unlockGui();
        if (file != null) {
            this.fileChooserImpIcon.setInitialDirectory(file.getParentFile());
            Path path = file.toPath();
            IconInterface icon = null;
            try {
                if (path != null) {
                    icon = this.iconDatabase.setForType(itc.getInstanceType(),
                            path);
                    this.addImportedIcon(icon, itc, menu);
                }
            } catch (IconDatabaseException e1) {
                this.showError(e1);
            }
        }
    }

    private void addImportedIcon(IconInterface icon,
            InstanceTypeConfiguration itc, Menu menu) {
        try {
            MenuItem newIcon = new MenuItem();
            File file = icon.getPath().toFile();
            newIcon.setOnAction(event -> {
                if (this.eventsActive) {
                    this.changeInstanceTypeSymbol(itc, icon);
                }
            });
            String localUrl = null;
            try {
                localUrl = file.toURI().toURL().toString();
            } catch (MalformedURLException e) {
                this.showError(e);
            }

            if (localUrl != null) {
                Image image = new Image(localUrl);
                ImageView iv = new ImageView(image);
                iv.setFitHeight(50);
                iv.setFitWidth(50);
                newIcon.setGraphic(iv);
                menu.getItems().add(newIcon);
            }

        } catch (IconDatabaseException e2) {
            this.showError(e2);
        }
    }

    private void setRelationsTab(Set<RelationTypeConfiguration> set) {
        this.relationTreeItems = new HashMap<>();
        CheckBoxTreeItem<String> root = new CheckBoxTreeItem<>("All");
        root.setExpanded(true);
        root.setIndependent(true);
        this.rootRelationsCheckbox = root;
        root.addEventHandler(
                CheckBoxTreeItem.<String>checkBoxSelectionChangedEvent(),
                event -> {
                    event.consume();
                    if (this.eventsActive) {
                        if ((!root.isIndeterminate()) && root.isSelected()) {
                            this.graphController.showAllRelations();
                        } else {
                            this.graphController.hideAllRelations();
                        }
                    }
                });
        TreeView<String> treeView = new TreeView<>(root);

        final HashMap<CheckBoxTreeItem<String>, HBox> map = new HashMap<>();

        for (RelationTypeConfiguration rtc : set) {
            CheckBoxTreeItem<String> current = new CheckBoxTreeItem<>(
                    rtc.getName());
            root.getChildren().add(current);

            HBox hBox = new HBox(6);
            hBox.setAlignment(Pos.CENTER_LEFT);
            MenuButton button = new MenuButton();
            Menu changeStroke = new Menu("Change Stroke");
            Menu changeArrowShape = new Menu("Change Arrow Shape");
            MenuItem changeColor = new MenuItem("Change Color");

            MenuItem strokeNone = new MenuItem("None");
            MenuItem strokePlain = new MenuItem("Plain");
            MenuItem strokeDashed = new MenuItem("Dashes");
            MenuItem strokeDotted = new MenuItem("Dots");

            MenuItem arrowShapeNone = new MenuItem("None");
            MenuItem arrowShapeArrow = new MenuItem("Arrow");
            MenuItem arrowShapeCircle = new MenuItem("Circle");
            MenuItem arrowShapeDiamond = new MenuItem("Diamond");

            strokeNone.setOnAction(event -> {
                if (this.eventsActive) {
                    RelationStyle relStyle = rtc.getStyle();
                    relStyle.setStroke(RelationStyle.RelationStroke.NONE);
                    this.changeRelationTypeSymbol(rtc, relStyle);
                }
            });
            strokePlain.setOnAction(event -> {
                if (this.eventsActive) {
                    RelationStyle relStyle = rtc.getStyle();
                    relStyle.setStroke(RelationStyle.RelationStroke.PLAIN);
                    this.changeRelationTypeSymbol(rtc, relStyle);
                }
            });
            strokeDashed.setOnAction(event -> {
                if (this.eventsActive) {
                    RelationStyle relStyle = rtc.getStyle();
                    relStyle.setStroke(RelationStyle.RelationStroke.DASHES);
                    this.changeRelationTypeSymbol(rtc, relStyle);
                }
            });
            strokeDotted.setOnAction(event -> {
                if (this.eventsActive) {
                    RelationStyle relStyle = rtc.getStyle();
                    relStyle.setStroke(RelationStyle.RelationStroke.DOTS);
                    this.changeRelationTypeSymbol(rtc, relStyle);
                }
            });

            arrowShapeNone.setOnAction(event -> {
                if (this.eventsActive) {
                    RelationStyle relStyle = rtc.getStyle();
                    relStyle.setShape(RelationStyle.ArrowShape.NONE);
                    this.changeRelationTypeSymbol(rtc, relStyle);
                }
            });
            arrowShapeArrow.setOnAction(event -> {
                if (this.eventsActive) {
                    RelationStyle relStyle = rtc.getStyle();
                    relStyle.setShape(RelationStyle.ArrowShape.ARROW);
                    this.changeRelationTypeSymbol(rtc, relStyle);
                }
            });
            arrowShapeCircle.setOnAction(event -> {
                if (this.eventsActive) {
                    RelationStyle relStyle = rtc.getStyle();
                    relStyle.setShape(RelationStyle.ArrowShape.CIRCLE);
                    this.changeRelationTypeSymbol(rtc, relStyle);
                }
            });
            arrowShapeDiamond.setOnAction(event -> {
                if (this.eventsActive) {
                    RelationStyle relStyle = rtc.getStyle();
                    relStyle.setShape(RelationStyle.ArrowShape.DIAMOND);
                    this.changeRelationTypeSymbol(rtc, relStyle);
                }
            });

            ColorPicker colorWheel = new ColorPicker(
                    javafx.scene.paint.Color.BLACK);
            changeColor.setOnAction(event -> {
                Stage colorStage = new Stage();
                colorStage.getIcons().add(this.tolowizIcon);
                colorStage.setTitle("Choose Color");
                AnchorPane pane = new AnchorPane();
                AnchorPane.setBottomAnchor(colorWheel, 0.0);
                AnchorPane.setTopAnchor(colorWheel, 0.0);
                AnchorPane.setLeftAnchor(colorWheel, 0.0);
                AnchorPane.setRightAnchor(colorWheel, 0.0);
                pane.getChildren().add(colorWheel);
                colorStage.setScene(new Scene(pane, 100, 30));
                colorStage.setOnCloseRequest(ev -> this.unlockGui());
                this.lockGui();
                colorStage.show();
                this.cStage = colorStage;
            });

            colorWheel.setOnAction(event -> {
                if (this.eventsActive) {

                    int red = (int) (colorWheel.getValue().getRed() * 255);
                    int green = (int) (colorWheel.getValue().getGreen() * 255);
                    int blue = (int) (colorWheel.getValue().getBlue() * 255);
                    Color color = new Color(red, green, blue);
                    RelationStyle relStyle = rtc.getStyle();
                    relStyle.setColor(color);
                    this.changeRelationTypeSymbol(rtc, relStyle);
                }
                this.unlockGui();
                this.cStage.hide();
            });

            changeStroke.getItems().addAll(strokeNone, strokePlain,
                    strokeDashed, strokeDotted);
            changeArrowShape.getItems().addAll(arrowShapeNone, arrowShapeArrow,
                    arrowShapeCircle, arrowShapeDiamond);

            button.getItems().addAll(changeStroke, changeArrowShape,
                    changeColor);
            hBox.getChildren().add(button);

            Label label = new Label();
            label.setText(rtc.getName());
            hBox.getChildren().add(0, label);

            map.put(current, hBox);
            current.setIndependent(true);
            current.addEventHandler(
                    CheckBoxTreeItem.<String>checkBoxSelectionChangedEvent(),
                    event -> {
                        event.consume();
                        if (this.eventsActive) {
                            this.checkRelationType(current, rtc.getName());
                        }
                    });
            this.relationTreeItems.put(rtc.getName(), current);
        }

        treeView.setCellFactory(
                new Callback<TreeView<String>, TreeCell<String>>() {

                    @Override
                    public TreeCell<String> call(TreeView<String> param) {
                        return new CheckBoxTreeCell<>() {

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                this.disclosureNodeProperty().get()
                                        .setOnMousePressed(
                                                event -> event.consume());
                                this.disclosureNodeProperty().get()
                                        .setOnMouseClicked(
                                                event -> event.consume());
                                this.getDisclosureNode().setStyle(
                                        "-fx-padding: 0 0 0 0; -fx-opacity: 0");
                                this.addEventFilter(MouseEvent.MOUSE_PRESSED,
                                        (MouseEvent e) -> {
                                            if (((e.getClickCount() % 2) == 0)
                                                    && e.getButton().equals(
                                                            MouseButton.PRIMARY)) {
                                                e.consume();
                                            }
                                        });
                                if (empty) {
                                    this.setGraphic(null);
                                    this.setText(null);
                                } else if (map
                                        .containsKey(this.getTreeItem())) {
                                    HBox newHBox = map.get(this.getTreeItem());
                                    newHBox.getChildren().add(0,
                                            this.getGraphic());
                                    this.setGraphic(newHBox);
                                    this.setText(null);
                                }
                            }
                        };
                    }
                });

        this.runLater(() -> this.relationsArea.setContent(treeView));
        this.runLater(() -> this.relationsArea.setFitToHeight(true));
        this.runLater(() -> this.relationsArea.setFitToWidth(true));

    }

    private void setValuesTab(InstanceTypeConfiguration rootType) {
        this.valueTreeItems = new HashMap<>();
        if (this.eventsActive) {

            TreeItem<String> root = new TreeItem<>(rootType.getName());
            root.setExpanded(true);
            TreeView<String> treeView = new TreeView<>(root);
            final HashMap<CheckBoxTreeItem<String>, Boolean> map = new HashMap<>();
            this.createInstanceTypeTree(rootType, root, map);

            treeView.setCellFactory(
                    new Callback<TreeView<String>, TreeCell<String>>() {

                        @Override
                        public TreeCell<String> call(TreeView<String> param) {
                            return new CheckBoxTreeCell<>() {

                                @Override
                                public void updateItem(String item,
                                        boolean empty) {
                                    super.updateItem(item, empty);
                                    this.setStyle("-fx-text-fill: black;");
                                    if (empty) {
                                        this.setGraphic(null);
                                        this.setText(null);
                                    } else if (map
                                            .containsKey(this.getTreeItem())) {
                                        if (map.get(this.getTreeItem())) {
                                            this.setDisabled(false);
                                        } else {
                                            CheckBoxTreeItem<String> c = (CheckBoxTreeItem<String>) this
                                                    .getTreeItem();
                                            c.setSelected(false);
                                            this.setDisabled(true);
                                            this.setGraphic(null);
                                        }
                                    } else {
                                        this.setDisabled(false);
                                        if (!(this
                                                .getTreeItem() instanceof CheckBoxTreeItem)) {
                                            this.setGraphic(null);
                                        } else {
                                            this.setStyle(
                                                    "-fx-text-fill: #b200ff;");
                                        }
                                    }
                                }
                            };
                        }
                    });

            this.runLater(() -> this.valuesArea.setContent(treeView));
            this.runLater(() -> this.valuesArea.setFitToHeight(true));
            this.runLater(() -> this.valuesArea.setFitToWidth(true));
        }
    }

    private void createInstanceTypeTree(InstanceTypeConfiguration rootType,
            TreeItem<String> root,
            HashMap<CheckBoxTreeItem<String>, Boolean> map) {
        for (InstanceTypeConfiguration itc : rootType.getSubTypes()) {
            CheckBoxTreeItem<String> currentIT = new CheckBoxTreeItem<>(
                    itc.getName());
            currentIT.setExpanded(true);
            currentIT.setIndependent(true);
            root.getChildren().add(currentIT);
            if ((itc.getMembers().size() == 0)
                    || (itc.getAllValues().size() == 0)) {
                map.put(currentIT, false);
            } else {
                map.put(currentIT, true);
            }
            if (itc.getActiveValues().containsAll(itc.getAllValues())) {
                currentIT.setIndeterminate(false);
                currentIT.setSelected(true);
            } else if (itc.getActiveValues().isEmpty()) {
                currentIT.setIndeterminate(false);
                currentIT.setSelected(false);
            } else {
                currentIT.setIndeterminate(true);
            }

            Map<String, CheckBoxTreeItem<String>> map2 = new HashMap<>();
            if (itc.getSubTypes().isEmpty()) {
                this.addValuesToInstanceType(itc, currentIT, map2);
            } else if (itc.getDirectMembers() != null) {
                this.addValuesToInstanceType(itc, currentIT, map2);
                this.createInstanceTypeTree(itc, currentIT, map);
            } else {
                this.createInstanceTypeTree(itc, currentIT, map);
            }
            currentIT.addEventHandler(
                    CheckBoxTreeItem.<String>checkBoxSelectionChangedEvent(),
                    event -> {
                        event.consume();
                        if (this.eventsActive) {
                            if ((!currentIT.isIndeterminate())
                                    && currentIT.isSelected()) {
                                this.graphController.activateAllValues(itc);
                            } else {
                                this.graphController.deactivateAllValues(itc);
                            }
                        }
                    });
            Pair<CheckBoxTreeItem<String>, Map<String, CheckBoxTreeItem<String>>> pair = new Pair<>(
                    currentIT, map2);

            this.valueTreeItems.put(itc.getIRI(), pair);
        }
    }

    private void addValuesToInstanceType(InstanceTypeConfiguration itc,
            TreeItem<String> root, Map<String, CheckBoxTreeItem<String>> map) {
        for (ValueType vt : itc.getAllValues()) {
            CheckBoxTreeItem<String> currentVT = new CheckBoxTreeItem<>(
                    vt.getName());
            currentVT.setIndependent(true);
            root.getChildren().add(currentVT);
            currentVT.addEventHandler(
                    CheckBoxTreeItem.<String>checkBoxSelectionChangedEvent(),
                    event -> {
                        event.consume();
                        if (this.eventsActive) {
                            this.checkValueType(currentVT, itc.getIRI(),
                                    vt.getURI());
                        }
                    });
            if (itc.getActiveValues().contains(vt)) {
                currentVT.setIndeterminate(false);
                currentVT.setSelected(true);
            } else {
                currentVT.setIndeterminate(false);
                currentVT.setSelected(false);
            }
            map.put(vt.getURI(), currentVT);
        }
    }

    private void setGroupsTab() {
        this.createNewGroup.setOnAction(event -> {
            if (this.eventsActive) {
                this.createNewGroup();
            }
        });
        this.groupsVBox.getChildren().clear();
        for (Group group : this.tabController.getConfig().getGroups()) {
            HBox hBox = new HBox(6);
            hBox.setAlignment(Pos.CENTER_LEFT);
            CheckBox checkBox = new CheckBox();
            Label label = new Label(group.getName());
            MenuButton button = new MenuButton();
            MenuItem hide = new MenuItem("Hide Group");
            MenuItem show = new MenuItem("Show Group");
            MenuItem delete = new MenuItem("Delete");

            Menu changeMark = new Menu("Change Mark");

            Menu changeStroke = new Menu("Change Stroke");
            Menu changeShape = new Menu("Change Shape");
            MenuItem changeColor = new MenuItem("Choose Color");

            MenuItem strokeNone = new MenuItem("None");
            MenuItem strokePlain = new MenuItem("Plain");
            MenuItem strokeDashed = new MenuItem("Dashes");
            MenuItem strokeDotted = new MenuItem("Dots");

            MenuItem shapeCircle = new MenuItem("Circle");
            MenuItem shapeBox = new MenuItem("Box");
            MenuItem shapeDiamond = new MenuItem("Diamond");
            MenuItem shapeCross = new MenuItem("Cross");

            show.setOnAction(event -> {
                if (this.eventsActive) {
                    this.showGroup(group);
                }
            });

            hide.setOnAction(event -> {
                if (this.eventsActive) {
                    this.hideGroup(group);
                }
            });

            delete.setOnAction(event -> {
                if (this.eventsActive) {
                    this.deleteGroup(group);
                }
            });

            strokeNone.setOnAction(event -> {
                if (this.eventsActive) {
                    InstanceMark mark = new InstanceMark(
                            InstanceMark.InstanceStroke.NONE);
                    this.markGroup(group, mark);
                }
            });
            strokePlain.setOnAction(event -> {
                if (this.eventsActive) {
                    InstanceMark mark = new InstanceMark(
                            InstanceMark.InstanceStroke.PLAIN);
                    this.markGroup(group, mark);
                }
            });
            strokeDashed.setOnAction(event -> {
                if (this.eventsActive) {
                    InstanceMark mark = new InstanceMark(
                            InstanceMark.InstanceStroke.DASHES);
                    this.markGroup(group, mark);
                }
            });
            strokeDotted.setOnAction(event -> {
                if (this.eventsActive) {
                    InstanceMark mark = new InstanceMark(
                            InstanceMark.InstanceStroke.DOTS);
                    this.markGroup(group, mark);
                }
            });

            shapeCircle.setOnAction(event -> {
                if (this.eventsActive) {
                    InstanceMark mark = new InstanceMark(
                            InstanceMark.InstanceShape.CIRCLE);
                    this.markGroup(group, mark);
                }
            });
            shapeBox.setOnAction(event -> {
                if (this.eventsActive) {
                    InstanceMark mark = new InstanceMark(
                            InstanceMark.InstanceShape.BOX);
                    this.markGroup(group, mark);
                }
            });
            shapeDiamond.setOnAction(event -> {
                if (this.eventsActive) {
                    InstanceMark mark = new InstanceMark(
                            InstanceMark.InstanceShape.DIAMOND);
                    this.markGroup(group, mark);
                }
            });
            shapeCross.setOnAction(event -> {
                if (this.eventsActive) {
                    InstanceMark mark = new InstanceMark(
                            InstanceMark.InstanceShape.CROSS);
                    this.markGroup(group, mark);
                }
            });

            ColorPicker colorWheel = new ColorPicker(
                    javafx.scene.paint.Color.BLACK);
            changeColor.setOnAction(event -> {
                Stage colorStage = new Stage();
                colorStage.getIcons().add(this.tolowizIcon);
                colorStage.setTitle("Choose Color");
                AnchorPane pane = new AnchorPane();
                AnchorPane.setBottomAnchor(colorWheel, 0.0);
                AnchorPane.setTopAnchor(colorWheel, 0.0);
                AnchorPane.setLeftAnchor(colorWheel, 0.0);
                AnchorPane.setRightAnchor(colorWheel, 0.0);
                pane.getChildren().add(colorWheel);
                colorStage.setScene(new Scene(pane, 100, 30));
                colorStage.setOnCloseRequest(ev -> this.unlockGui());
                this.lockGui();
                colorStage.show();
                this.cStage = colorStage;
            });

            colorWheel.setOnAction(event -> {
                if (this.eventsActive) {

                    int red = (int) (colorWheel.getValue().getRed() * 255);
                    int green = (int) (colorWheel.getValue().getGreen() * 255);
                    int blue = (int) (colorWheel.getValue().getBlue() * 255);
                    Color color = new Color(red, green, blue);
                    InstanceMark mark = new InstanceMark(color);
                    this.markGroup(group, mark);
                }
                this.unlockGui();
                this.cStage.hide();
            });

            checkBox.setOnAction(event -> {
                if (this.eventsActive) {

                    if (checkBox.isSelected() && !checkBox.isIndeterminate()) {
                        this.showGroup(group);
                    } else if (!checkBox.isSelected()
                            && !checkBox.isIndeterminate()) {
                        this.hideGroup(group);
                    }
                }
            });

            changeStroke.getItems().addAll(strokeNone, strokePlain,
                    strokeDashed, strokeDotted);
            changeShape.getItems().addAll(shapeCircle, shapeBox, shapeDiamond,
                    shapeCross);
            changeMark.getItems().addAll(changeStroke, changeShape,
                    changeColor);

            StringBuilder sBuilder = new StringBuilder();
            for (InstanceConfiguration ic : group.getInstances()) {
                sBuilder.append(ic.getName());
                sBuilder.append("\n");
            }
            String s = sBuilder.toString();
            label.setTooltip(new Tooltip(s));
            button.getItems().addAll(show, hide, changeMark, delete);
            hBox.getChildren().addAll(label, button);
            if (!this.groupsVBox.getChildren().contains(hBox)) {
                this.groupsVBox.getChildren().add(hBox);
            }

        }
    }

    private void markGroup(Group group, InstanceMark mark) {
        group.setMark(mark);
    }

    private void showGroup(Group group) {
        this.graphController.showGroup(group);
    }

    private void hideGroup(Group group) {
        this.graphController.hideGroup(group);
    }

    private void deleteGroup(Group group) {
        this.graphController.deleteGroup(group);
        this.setGroupsTab();
    }

    private void setHiddenInstances(Configuration config) {
        this.hiddenInstancesVBox.getChildren().clear();
        Set<InstanceConfiguration> set = config.getHiddenInstances();
        for (InstanceConfiguration ic : set) {
            Label label = new Label(ic.getName());
            Button button = new Button("Show");
            HBox hBox = new HBox(6);
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.getChildren().addAll(label, button);
            this.hiddenInstancesVBox.getChildren().add(hBox);
            button.setOnAction(event -> {
                if (this.eventsActive) {
                    this.graphController.showInstance(ic);
                    this.setHiddenInstances(config);
                }
            });
        }
    }

    private void setDetailArea(ToloTab tab) {
        this.detailArea.setContent(tab.getDetails().getContent());

    }

    private void setConfigurationArea(ToloTab tab) {
        this.configurationArea
                .setContent(tab.getConfigurationArea().getContent());
        this.configurationTabs.getSelectionModel().select(this.tab.getTab());
    }

    private void configurationTabChanged(Tab tab) {
        if ((this.tab != null) && tab.isSelected()) {
            this.tab.setConfigurationArea(this.configurationArea, tab,
                    this.configurationTabs);
        }
    }

    private void setMenu() {
        if ((this.tabPane == null) || this.tabPane.getTabs().isEmpty()) {
            this.exportConfiguration.setDisable(true);
            this.exportAsImage.setDisable(true);
            this.closeOntology.setDisable(true);
            this.switchToPresentingMode.setDisable(true);
            this.undoButton.setDisable(true);
            this.redoButton.setDisable(true);
            this.undoMenuItem.setDisable(true);
            this.redoMenuItem.setDisable(true);
            this.saveButton.setDisable(true);
            this.restoreStandardAlignment.setDisable(true);
            this.restoreStandardView.setDisable(true);
            this.saveConfiguration.setDisable(true);
            this.createNewGroup.setDisable(true);
        } else {
            this.exportConfiguration.setDisable(false);
            this.exportAsImage.setDisable(false);
            this.closeOntology.setDisable(false);
            this.switchToPresentingMode.setDisable(false);
            if (this.tabController != null) {
                this.undoButton
                        .setDisable(!this.tabController.isUndoPossible());
                this.redoButton
                        .setDisable(!this.tabController.isRedoPossible());
                this.undoMenuItem
                        .setDisable(!this.tabController.isUndoPossible());
                this.redoMenuItem
                        .setDisable(!this.tabController.isRedoPossible());
            }
            this.saveButton.setDisable(false);
            this.restoreStandardAlignment.setDisable(false);
            this.restoreStandardView.setDisable(false);
            this.saveConfiguration.setDisable(false);
            this.createNewGroup.setDisable(false);
        }
    }

    @Override
    public void refresh() {
        this.eventsActive = false;
        Configuration conf = this.tab.getGraph().getConfiguration();
        this.setMenu();
        for (InstanceTypeConfiguration x : this.tab.getGraph()
                .getConfiguration().getInstanceTypes()) {
            this.refreshCheckbox(x);
        }
        for (RelationTypeConfiguration x : this.tab.getGraph()
                .getConfiguration().getRelationTypes()) {
            this.refreshCheckbox(x);
        }
        boolean checked = true;
        boolean unchecked = true;
        for (RelationTypeConfiguration x : this.tab.getGraph()
                .getConfiguration().getRelationTypes()) {
            if (x.isVisible()) {
                unchecked = false;
            } else {
                checked = false;
            }
        }
        if (checked) {

            this.rootRelationsCheckbox.setSelected(true);
            this.rootRelationsCheckbox.setIndeterminate(false);
        } else if (unchecked) {

            this.rootRelationsCheckbox.setSelected(false);
            this.rootRelationsCheckbox.setIndeterminate(false);
        } else {

            this.rootRelationsCheckbox.setIndeterminate(true);
        }
        for (InstanceTypeConfiguration x : this.tab.getGraph()
                .getConfiguration().getInstanceTypes()) {
            this.refreshCheckboxValue(x);
            for (ValueType y : x.getAllValues()) {
                this.refreshCheckboxValueType(x, y);
            }
        }
        this.setDetailArea(this.tab);
        this.setConfigurationArea(this.tab);
        this.runLater(() -> {
            this.setHiddenInstances(conf);
            this.setGroupsTab();
        });
        this.eventsActive = true;
    }

    /**
     * @param x
     * @param y
     */
    private void refreshCheckboxValueType(InstanceTypeConfiguration it,
            ValueType vt) {
        var current = this.valueTreeItems.get(it.getIRI()).getValue1()
                .get(vt.getURI());
        if (it.getActiveValues().contains(vt)) {
            current.setIndeterminate(false);
            current.setSelected(true);
        } else {
            current.setIndeterminate(false);
            current.setSelected(false);
        }

    }

    /**
     * @param x
     */
    private void refreshCheckboxValue(InstanceTypeConfiguration itc) {
        if (this.valueTreeItems.containsKey(itc.getIRI())) {
            var current = this.valueTreeItems.get(itc.getIRI()).getValue0();
            if (itc.getAllValuesShown() == TypeVisibility.PARTIAL) {
                current.setIndeterminate(true);
            } else {
                if (itc.getAllValuesShown() == TypeVisibility.YES) {
                    current.setIndeterminate(false);
                    current.setSelected(true);
                } else {
                    current.setIndeterminate(false);
                    current.setSelected(false);
                }
            }
        }
    }

    private void refreshCheckbox(InstanceTypeConfiguration itc) {
        var current = this.treeItems.get(itc.getIRI());
        if (itc.isVisible() == TypeVisibility.PARTIAL) {
            current.setIndeterminate(true);
        } else {
            if (itc.isVisible() == TypeVisibility.YES) {
                current.setIndeterminate(false);
                current.setSelected(true);
            } else {
                current.setIndeterminate(false);
                current.setSelected(false);
            }
        }
    }

    private void refreshCheckbox(RelationTypeConfiguration rtc) {
        var current = this.relationTreeItems.get(rtc.getName());
        if (rtc.isVisible()) {
            current.setIndeterminate(false);
            current.setSelected(true);
        } else {
            current.setIndeterminate(false);
            current.setSelected(false);
        }
    }

    @Override
    public IconInterface getDefaultIcon(InstanceType t) {
        try {
            return this.iconDatabase.getDefaultIcon(t);
        } catch (IconDatabaseException e) {
            this.showError(e);
        }
        return null;
    }

    /**
     * Main method.
     *
     * @param args commandline arguments
     */
    public static void main(final String[] args) {
        Application.launch(args);
    }

    @Override
    public boolean isInPresentingMode() {
        return this.inPresentingMode;
    }

}
