package threadmonitor.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import threadmonitor.config.TerminalConfig;
import threadmonitor.entry.Command;
import threadmonitor.entry.Progress;
import threadmonitor.entry.SSHConnInfo;
import threadmonitor.services.DbService;
import threadmonitor.services.ProsessService;
import threadmonitor.services.SshService;
import threadmonitor.util.Utils;
import threadmonitor.view.TerminalBuilder;
import threadmonitor.view.TerminalTab;

public class Controller {

    @FXML
    private ListView<SSHConnInfo> connectList;
    @FXML
    private  TextField connectUser;
    @FXML
    private  TextField connectPwd;
    @FXML
    private TextField connectDesc;
    @FXML
    private TextField connectIp;
    @FXML
    private TextField command;
    @FXML
    private TextField commandDesc;
    @FXML
    private TabPane consoleTabPane;
    @FXML
    private TableView<Progress> progressListView;
    @FXML
    private TableColumn<Progress, String> userColumn;
    @FXML
    private TableColumn<Progress, String> pidColumn;
    @FXML
    private TableColumn<Progress, String> cpuColumn;
    @FXML
    private TableColumn<Progress, String> memColumn;
    @FXML
    private TableColumn<Progress, String> vszColumn;
    @FXML
    private TableColumn<Progress, String> rssColumn;
    @FXML
    private TableColumn<Progress, String> statColumn;
    @FXML
    private TableColumn<Progress, String> timeColumn;
    @FXML
    private TableColumn<Progress, String> commandColumn;
    @FXML
    private TabPane prosessInfo;
    @FXML
    private ListView<Command> commandList;
    @FXML
    private TextArea sysInfoArea;

    private ProsessService prosessService;

    private DbService dbService = new DbService();

    private SshService sshService = new SshService();

    private ConcurrentHashMap<Tab, HashMap<String, Series>> seriesMap = new ConcurrentHashMap();

    public void init() {
        new Thread(() -> {
            while (true) {
                ObservableList<Progress> item = prosessService.getProgress();
                Platform.runLater(() -> {
                    progressListView.getItems().clear();
                    progressListView.getItems().addAll(item);
                    sysInfoArea.clear();
                    sysInfoArea.appendText(sshService.querySysInfo());
                });
            }
        }, "queryprosess").start();
    }

    private List<Data> getSeriesData(List<Map<String, Progress>> records, String pid, String field)
            throws ParseException {
        List<Data> dataList = new ArrayList<>();
        for (Map<String, Progress> map : records) {
            Progress current = map.get(pid);
            if (current == null) {
                continue;
            }
            dataList.add(getData(current, field));
        }
        return dataList;
    }

    private Data getData(Progress progress, String field) throws ParseException {
        switch (field) {
            case "cpu":
                return new Data(Utils.getHMS(progress.getDate()),
                        Utils.strToNum(progress.getCpuColumn()));
            case "mem":
                return new Data(Utils.getHMS(progress.getDate()),
                        Utils.strToNum(progress.getMemColumn()));
            default:
                return new Data<>();
        }
    }

    private LineChart<String, Number> getLineChart(Series series, String name)
            throws ParseException {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> lineChart = new LineChart(xAxis, yAxis);
        lineChart.getData().add(series);
        lineChart.setTitle(name);
        return lineChart;
    }

    private String getConmand(List<Map<String, Progress>> records, String pid) {
        return records.get(0).get(pid).getCommandColumn();
    }

    private Tab generateProcessTab(Progress progress) throws ParseException {
        Tab tab = new Tab(progress.getPidColumn());
        GridPane gridPane = new GridPane();

        List<Map<String, Progress>> records = dbService.getRecentRecord();
        String pid = progress.getPidColumn();

        HashMap<String, Series> tabMap = new HashMap<>();

        Series memSeries = new Series();
        memSeries.getData().addAll(getSeriesData(records, pid, "mem"));
        LineChart<String, Number> memLineChart = getLineChart(memSeries, "内存占用趋势");
        tabMap.put("mem", memSeries);

        Series cpuSeries = new Series();
        cpuSeries.getData().addAll(getSeriesData(records, pid, "cpu"));
        LineChart<String, Number> cpuLineChart = getLineChart(cpuSeries, "CPU占用趋势");
        tabMap.put("cpu", cpuSeries);

        seriesMap.put(tab, tabMap);
        gridPane.add(memLineChart, 1, 1);
        gridPane.add(cpuLineChart, 2, 1);

        TextArea processInfo = new TextArea();
        processInfo.appendText(sshService.queryProcessInfo(getConmand(records, pid), pid));

        gridPane.add(processInfo, 1, 2, 2, 1);
        tab.setContent(gridPane);

        TabThread exitOnTabClose = new TabThread(tab, pid);
        exitOnTabClose.start();

        tab.setOnCloseRequest(event -> exitOnTabClose.cancel());
        return tab;
    }

    @FXML
    public void initialize() {
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
        sysInfoArea.appendText(sshService.querySysInfo());

        TerminalConfig defaultConfig = new TerminalConfig();
        TerminalBuilder terminalBuilder = new TerminalBuilder(defaultConfig);
        TerminalTab terminal = terminalBuilder.newTerminal();
        consoleTabPane.getTabs().add(terminal);

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

        connectList.setCellFactory(new Callback<ListView<SSHConnInfo>, ListCell<SSHConnInfo>>() {
            @Override
            public ListCell<SSHConnInfo> call(ListView<SSHConnInfo> param) {
                ListCell<SSHConnInfo> connInfo = new ListCell<SSHConnInfo>(){
                    @Override
                    protected void updateItem(SSHConnInfo item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null){
                            setText(item.getConnectIp() + " : " + item.getConnectUser());
                        }
                        if (item == null){
                            setText("");
                        }
                    }
                };
                return connInfo;
            }
        });

        addListening();
        loadConmmand();
        loadConnects();
    }

    private void loadConnects() {
        List<SSHConnInfo> connInfos = dbService.queryAllConnect();
        ObservableList<SSHConnInfo> connList = FXCollections.observableArrayList(connInfos);
        connectList.setItems(connList);
    }

    private void loadConmmand() {
        List<Command> commands = dbService.queryAllCommand();
        ObservableList<Command> strList = FXCollections.observableArrayList(commands);
        commandList.setItems(strList);
    }

    private void updateCommand(){
        Platform.runLater(() -> {
            List<Command> commands = dbService.queryAllCommand();
            commandList.getItems().clear();
            commandList.getItems().addAll(commands);
        });
    }

    private void updateConnect() {
        Platform.runLater(() -> {
            List<SSHConnInfo> connInfos = dbService.queryAllConnect();
            connectList.getItems().clear();
            connectList.getItems().addAll(connInfos);
        });
    }

    private void addListening() {
        progressListView.setRowFactory(tv -> {
            TableRow<Progress> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Progress rowData = row.getItem();
                    try {
                        Tab tab = generateProcessTab(rowData);
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

    public void setProsessService(ProsessService prosessService) {
        this.prosessService = prosessService;
    }

    @FXML
    public void resetCommandInput() {
        command.clear();
        commandDesc.clear();
    }

    @FXML
    public void saveCommand() {
        String cmdStr = command.getText();
        String cmdDesc = commandDesc.getText();
        command.clear();
        commandDesc.clear();
        dbService.insertCommand(cmdStr,cmdDesc);
        updateCommand();
    }


    @FXML
    public void delCommand() {
        Integer id = commandList.getSelectionModel().getSelectedItem().getId();
        dbService.delCommand(id);
        commandList.getItems().remove(commandList.getSelectionModel().getSelectedItem());

        System.out.println(commandList.getItems().size());
    }

    public void queryCommand(ActionEvent actionEvent) {

    }

    @FXML
    public void onCommandEnter() {
        saveCommand();
        command.requestFocus();
    }

    @FXML
    public void addConnectList(ActionEvent actionEvent) {
        String ip = connectIp.getText();
        String username = connectUser.getText();
        String password = connectUser.getText();
        String desc = connectDesc.getText();
        connectIp.clear();
        connectUser.clear();
        connectPwd.clear();
        commandDesc.clear();
        dbService.insertConnect(new SSHConnInfo(ip,username,password,desc));
        updateConnect();
    }

    private class TabThread extends Thread {

        private Tab tab;
        private String pid;
        private AtomicBoolean isrun = new AtomicBoolean(true);

        public TabThread(Tab tab, String pid) {
            this.tab = tab;
            this.pid = pid;
        }

        @Override
        public void run() {
            while (isrun.get()) {
                List<Map<String, Progress>> records = dbService.getRecentRecord();
                HashMap<String, Series> tabMap = seriesMap.get(tab);

                Platform.runLater(() -> {
                    for (String field : tabMap.keySet()) {
                        try {
                            List<Data> newdata = getSeriesData(records, pid, field);
                            tabMap.get(field).getData().clear();
                            tabMap.get(field).getData().addAll(newdata);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            isrun.set(false);
        }
    }
}
