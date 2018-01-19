package threadmonitor.view;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import threadmonitor.config.TerminalConfig;
import threadmonitor.entry.Command;
import threadmonitor.entry.Progress;
import threadmonitor.entry.SSHConnInfo;
import threadmonitor.services.ProsessService;

public class MainTab extends Tab {

    private TabPane prosessInfo;
    private TabPane consoleTabPane;
    private TableView progressListView;
    private TableColumn userColumn;
    private TableColumn pidColumn;
    private TableColumn cpuColumn;
    private TableColumn memColumn;
    private TableColumn vszColumn;
    private TableColumn rssColumn;
    private TableColumn statColumn;
    private TableColumn timeColumn;
    private TableColumn commandColumn;
    private TextArea sysInfoArea;
    private TextField command;
    private TextField commandDesc;

    private ProsessService prosessService;
    private ListView<Command> commandList;

    public MainTab(SSHConnInfo connInfo) {
        prosessService = new ProsessService(connInfo);

        initView();
        initComponent();
        startProsessThread();
        setListening();
    }

    private void setListening() {
        progressListView.setRowFactory(tv -> {
            TableRow<Progress> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Progress rowData = row.getItem();
                    try {
                        ProcessTab tab = new ProcessTab(rowData.getPidColumn(),prosessService);
                        prosessInfo.getTabs().add(tab);
                        prosessInfo.getSelectionModel().select(tab);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });

        commandList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String item = commandList.getSelectionModel().getSelectedItem().getCommand();
                TerminalTab current = (TerminalTab) consoleTabPane.getSelectionModel().getSelectedItem();
                try {
                    current.command(item);
                    current.focusCursor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initComponent() {
        userColumn.setCellValueFactory(new PropertyValueFactory<>("userColumn"));
        pidColumn.setCellValueFactory(new PropertyValueFactory<>("pidColumn"));
        cpuColumn.setCellValueFactory(new PropertyValueFactory<>("cpuColumn"));
        memColumn.setCellValueFactory(new PropertyValueFactory<>("memColumn"));
        vszColumn.setCellValueFactory(new PropertyValueFactory<>("vszColumn"));
        rssColumn.setCellValueFactory(new PropertyValueFactory<>("rssColumn"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeColumn"));
        statColumn.setCellValueFactory(new PropertyValueFactory<>("statColumn"));
        commandColumn.setCellValueFactory(new PropertyValueFactory<>("commandColumn"));
        progressListView.setItems(FXCollections.observableArrayList());

        commandList.setCellFactory(new Callback<ListView<Command>, ListCell<Command>>() {
            @Override
            public ListCell<Command> call(ListView<Command> param) {
                ListCell<Command> cell = new ListCell<Command>(){
                    @Override
                    protected void updateItem(Command item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null){
                            setText(item.getCommand() + " : " + item.getCommand_desc());
                        }
                        if (item == null){
                            setText("");
                        }
                    }
                };
                return cell;
            }
        });

        TerminalConfig defaultConfig = new TerminalConfig();
        TerminalBuilder terminalBuilder = new TerminalBuilder(defaultConfig);
        TerminalTab terminal = terminalBuilder.newTerminal();
        consoleTabPane.getTabs().add(terminal);

        List<Command> commands = prosessService.getDbService().queryAllCommand();
        ObservableList<Command> strList = FXCollections.observableArrayList(commands);
        commandList.setItems(strList);
    }

    private void startProsessThread() {
        new Thread(() -> {
            try {
                prosessService.init();
                viewUpdate();
            } catch (Throwable ignored) {
            }
        },"prosess").start();
    }

    private void viewUpdate() {
        new Thread(() -> {
            while (true) {
                ObservableList<Progress> item = prosessService.getProgress();
                Platform.runLater(() -> {
                    progressListView.getItems().clear();
                    progressListView.getItems().addAll(item);
                    sysInfoArea.clear();
                    sysInfoArea.appendText(prosessService.getSshService().querySysInfo());
                });
            }
        }, "queryprosess").start();
    }

    private void updateCommand(){
        Platform.runLater(() -> {
            List<Command> commands = prosessService.getDbService().queryAllCommand();
            commandList.getItems().clear();
            commandList.getItems().addAll(commands);
        });
    }

    private void saveCommand(ActionEvent actionEvent) {
        String cmdStr = command.getText();
        String cmdDesc = commandDesc.getText();
        command.clear();
        commandDesc.clear();
        prosessService.getDbService().insertCommand(cmdStr,cmdDesc);
        updateCommand();
    }

    private void resetCommandInput(ActionEvent actionEvent) {
        command.clear();
        commandDesc.clear();
    }

    private void onCommandEnter(ActionEvent actionEvent) {
        saveCommand(null);
        command.requestFocus();
    }

    private void queryCommand(ActionEvent actionEvent) {

    }

    private void delCommand(ActionEvent actionEvent) {
        Integer id = commandList.getSelectionModel().getSelectedItem().getId();
        prosessService.getDbService().delCommand(id);
        commandList.getItems().remove(commandList.getSelectionModel().getSelectedItem());

        System.out.println(commandList.getItems().size());
    }

    private void initView(){
        VBox vBox = new VBox();
        SplitPane splitPane = new SplitPane();
        BorderPane borderPane = new BorderPane();
        SplitPane splitPane0 = new SplitPane();
        prosessInfo = new TabPane();
        VBox vBox0 = new VBox();
        consoleTabPane = new TabPane();
        SplitPane splitPane1 = new SplitPane();
        progressListView = new TableView();
        userColumn = new TableColumn();
        pidColumn = new TableColumn();
        cpuColumn = new TableColumn();
        memColumn = new TableColumn();
        vszColumn = new TableColumn();
        rssColumn = new TableColumn();
        statColumn = new TableColumn();
        timeColumn = new TableColumn();
        commandColumn = new TableColumn();
        GridPane gridPane = new GridPane();
        sysInfoArea = new TextArea();
        ColumnConstraints columnConstraints = new ColumnConstraints();
        RowConstraints rowConstraints = new RowConstraints();
        SplitPane splitPane2 = new SplitPane();
        AnchorPane anchorPane = new AnchorPane();
        GridPane gridPane0 = new GridPane();
        Label label = new Label();
        Label label0 = new Label();
        Button button = new Button();
        Button button0 = new Button();
        command = new TextField();
        commandDesc = new TextField();
        ColumnConstraints columnConstraints0 = new ColumnConstraints();
        ColumnConstraints columnConstraints1 = new ColumnConstraints();
        ColumnConstraints columnConstraints2 = new ColumnConstraints();
        ColumnConstraints columnConstraints3 = new ColumnConstraints();
        RowConstraints rowConstraints0 = new RowConstraints();
        RowConstraints rowConstraints1 = new RowConstraints();
        RowConstraints rowConstraints2 = new RowConstraints();
        Pane pane = new Pane();
        TextField textField = new TextField();
        Button button1 = new Button();
        Button button2 = new Button();
        commandList = new ListView();
        HBox hBox = new HBox();
        Label label1 = new Label();
        Pane pane0 = new Pane();
        Label label2 = new Label();


        VBox.setVgrow(splitPane, javafx.scene.layout.Priority.ALWAYS);
        splitPane.setFocusTraversable(true);
        splitPane.setPrefHeight(-1.0);
        splitPane.setPrefWidth(-1.0);

        borderPane.setPrefHeight(200.0);
        borderPane.setPrefWidth(200.0);

        splitPane0.setDividerPositions(0.5);
        splitPane0.setOrientation(javafx.geometry.Orientation.VERTICAL);

        prosessInfo.setPrefHeight(462.0);
        prosessInfo.setPrefWidth(896.0);

        VBox.setVgrow(consoleTabPane, javafx.scene.layout.Priority.ALWAYS);
        consoleTabPane.setPrefHeight(200.0);
        consoleTabPane.setPrefWidth(200.0);
        consoleTabPane.getStyleClass().add("floating");
        consoleTabPane.setTabClosingPolicy(javafx.scene.control.TabPane.TabClosingPolicy.ALL_TABS);
        borderPane.setCenter(splitPane0);

        splitPane1.setDividerPositions(0.5);
        splitPane1.setOrientation(javafx.geometry.Orientation.VERTICAL);
        splitPane1.setPrefHeight(200.0);
        splitPane1.setPrefWidth(400.0);
        splitPane1.setStyle("-fx-min-width: 400;");

        progressListView.setPrefHeight(400.0);
        progressListView.setPrefWidth(302.0);
        progressListView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        userColumn.setPrefWidth(75.0);
        userColumn.setText("用户");

        pidColumn.setPrefWidth(75.0);
        pidColumn.setText("PID");

        cpuColumn.setMinWidth(1.0);
        cpuColumn.setPrefWidth(75.0);
        cpuColumn.setText("CPU");

        memColumn.setMinWidth(1.0);
        memColumn.setPrefWidth(75.0);
        memColumn.setText("内存");

        vszColumn.setMinWidth(1.0);
        vszColumn.setPrefWidth(75.0);
        vszColumn.setText("虚");

        rssColumn.setMinWidth(1.0);
        rssColumn.setPrefWidth(75.0);
        rssColumn.setText("实");

        statColumn.setMinWidth(1.0);
        statColumn.setPrefWidth(75.0);
        statColumn.setText("状态");

        timeColumn.setPrefWidth(75.0);
        timeColumn.setText("运行");

        commandColumn.setMinWidth(60.0);
        commandColumn.setPrefWidth(75.0);
        commandColumn.setText("命令");

        gridPane.setStyle("-fx-max-height: 250;");

        sysInfoArea.setEditable(false);
        sysInfoArea.setPrefHeight(200.0);
        sysInfoArea.setPrefWidth(200.0);

        columnConstraints.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        columnConstraints.setMinWidth(10.0);
        columnConstraints.setPrefWidth(100.0);

        rowConstraints.setMinHeight(10.0);
        rowConstraints.setPrefHeight(30.0);
        rowConstraints.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
        borderPane.setLeft(splitPane1);

        BorderPane.setAlignment(splitPane2, javafx.geometry.Pos.CENTER);
        splitPane2.setDividerPositions(0.5, 0.5);
        splitPane2.setOrientation(javafx.geometry.Orientation.VERTICAL);

        anchorPane.setStyle("-fx-max-height: 80;");

        AnchorPane.setBottomAnchor(gridPane0, 0.0);
        AnchorPane.setLeftAnchor(gridPane0, 0.0);
        AnchorPane.setRightAnchor(gridPane0, 0.0);
        AnchorPane.setTopAnchor(gridPane0, 0.0);
        gridPane0.setPrefHeight(90.0);
        gridPane0.setPrefWidth(200.0);

        label.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
        label.setText("命令：");

        GridPane.setRowIndex(label0, 1);
        label0.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
        label0.setText("描述：");

        GridPane.setColumnIndex(button, 2);
        GridPane.setRowIndex(button, 2);
        button.setMnemonicParsing(false);
        button.setOnAction(this::saveCommand);
        button.setText("Add");

        GridPane.setColumnIndex(button0, 3);
        GridPane.setRowIndex(button0, 2);
        button0.setMnemonicParsing(false);
        button0.setOnAction(this::resetCommandInput);
        button0.setText("Clear");
        GridPane.setMargin(button0, new Insets(0.0, 0.0, 0.0, 4.0));

        GridPane.setColumnIndex(command, 2);
        GridPane.setColumnSpan(command, 2);
        command.setPrefHeight(23.0);
        command.setPrefWidth(97.0);
        GridPane.setMargin(command, new Insets(0.0, 5.0, 0.0, 0.0));

        GridPane.setColumnIndex(commandDesc, 2);
        GridPane.setColumnSpan(commandDesc, 2);
        GridPane.setRowIndex(commandDesc, 1);
        commandDesc.setOnAction(this::onCommandEnter);
        GridPane.setMargin(commandDesc, new Insets(0.0, 5.0, 0.0, 0.0));

        columnConstraints0.setHalignment(javafx.geometry.HPos.RIGHT);
        columnConstraints0.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        columnConstraints0.setMaxWidth(40.0);
        columnConstraints0.setMinWidth(40.0);
        columnConstraints0.setPrefWidth(100.0);

        columnConstraints1.setHalignment(javafx.geometry.HPos.RIGHT);
        columnConstraints1.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        columnConstraints1.setMaxWidth(20.0);
        columnConstraints1.setMinWidth(10.0);
        columnConstraints1.setPrefWidth(100.0);

        columnConstraints2.setHalignment(javafx.geometry.HPos.RIGHT);
        columnConstraints2.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        columnConstraints2.setMinWidth(10.0);
        columnConstraints2.setPrefWidth(100.0);

        columnConstraints3.setHalignment(javafx.geometry.HPos.CENTER);
        columnConstraints3.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        columnConstraints3.setMinWidth(10.0);
        columnConstraints3.setPrefWidth(100.0);

        rowConstraints0.setMinHeight(10.0);
        rowConstraints0.setPrefHeight(30.0);
        rowConstraints0.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints1.setMinHeight(10.0);
        rowConstraints1.setPrefHeight(30.0);
        rowConstraints1.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints2.setMinHeight(10.0);
        rowConstraints2.setPrefHeight(30.0);
        rowConstraints2.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        pane.setPrefHeight(128.0);
        pane.setPrefWidth(200.0);
        pane.setStyle("-fx-max-height: 30;");

        textField.setLayoutX(4.0);
        textField.setLayoutY(3.0);
        textField.setStyle("-fx-max-width: 80;");

        button1.setLayoutX(92.0);
        button1.setLayoutY(2.0);
        button1.setMnemonicParsing(false);
        button1.setOnAction(this::queryCommand);
        button1.setText("查询");

        button2.setLayoutX(141.0);
        button2.setLayoutY(2.0);
        button2.setMnemonicParsing(false);
        button2.setOnAction(this::delCommand);
        button2.setText("Del");

        commandList.setPrefHeight(200.0);
        commandList.setPrefWidth(200.0);
        borderPane.setRight(splitPane2);

        VBox.setVgrow(hBox, javafx.scene.layout.Priority.NEVER);
        hBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        hBox.setId("HBox");
        hBox.setSpacing(5.0);

        HBox.setHgrow(label1, javafx.scene.layout.Priority.ALWAYS);
        label1.setMaxHeight(Double.MAX_VALUE);
        label1.setMaxWidth(-1.0);
        label1.setText("Left status");
        label1.setFont(new Font(11.0));

        HBox.setHgrow(pane0, javafx.scene.layout.Priority.ALWAYS);
        pane0.setPrefHeight(-1.0);
        pane0.setPrefWidth(-1.0);

        HBox.setHgrow(label2, javafx.scene.layout.Priority.NEVER);
        label2.setFont(new Font(11.0));
        label2.setMaxWidth(-1.0);
        label2.setText("Right status");
        label2.setTextFill(javafx.scene.paint.Color.color(0.625,0.625,.0625));
        hBox.setPadding(new Insets(3.0));
        setContent(vBox);

        splitPane0.getItems().add(prosessInfo);
        vBox0.getChildren().add(consoleTabPane);
        splitPane0.getItems().add(vBox0);
        progressListView.getColumns().add(userColumn);
        progressListView.getColumns().add(pidColumn);
        progressListView.getColumns().add(cpuColumn);
        progressListView.getColumns().add(memColumn);
        progressListView.getColumns().add(vszColumn);
        progressListView.getColumns().add(rssColumn);
        progressListView.getColumns().add(statColumn);
        progressListView.getColumns().add(timeColumn);
        progressListView.getColumns().add(commandColumn);
        splitPane1.getItems().add(progressListView);
        gridPane.getChildren().add(sysInfoArea);
        gridPane.getColumnConstraints().add(columnConstraints);
        gridPane.getRowConstraints().add(rowConstraints);
        splitPane1.getItems().add(gridPane);
        gridPane0.getChildren().add(label);
        gridPane0.getChildren().add(label0);
        gridPane0.getChildren().add(button);
        gridPane0.getChildren().add(button0);
        gridPane0.getChildren().add(command);
        gridPane0.getChildren().add(commandDesc);
        gridPane0.getColumnConstraints().add(columnConstraints0);
        gridPane0.getColumnConstraints().add(columnConstraints1);
        gridPane0.getColumnConstraints().add(columnConstraints2);
        gridPane0.getColumnConstraints().add(columnConstraints3);
        gridPane0.getRowConstraints().add(rowConstraints0);
        gridPane0.getRowConstraints().add(rowConstraints1);
        gridPane0.getRowConstraints().add(rowConstraints2);
        anchorPane.getChildren().add(gridPane0);
        splitPane2.getItems().add(anchorPane);
        pane.getChildren().add(textField);
        pane.getChildren().add(button1);
        pane.getChildren().add(button2);
        splitPane2.getItems().add(pane);
        splitPane2.getItems().add(commandList);
        splitPane.getItems().add(borderPane);
        vBox.getChildren().add(splitPane);
        hBox.getChildren().add(label1);
        hBox.getChildren().add(pane0);
        hBox.getChildren().add(label2);
        vBox.getChildren().add(hBox);
    }
}
