package threadmonitor.controller;

import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import threadmonitor.entry.SSHConnInfo;
import threadmonitor.services.DbService;
import threadmonitor.view.MainTab;

public class Controller {
    private DbService dbService = new DbService();
    @FXML
    private ListView<SSHConnInfo> connectList;
    @FXML
    private TextField connectUser;
    @FXML
    private  TextField connectPwd;
    @FXML
    private TextField connectDesc;
    @FXML
    private TextField connectIp;
    @FXML
    private TabPane connectTabPane;

    @FXML
    public void initialize() {
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
        loadConnects();
        addListening();
    }

    private void loadConnects() {
        List<SSHConnInfo> connInfos = dbService.queryAllConnect();
        ObservableList<SSHConnInfo> connList = FXCollections.observableArrayList(connInfos);
        connectList.setItems(connList);
    }

    private void addListening() {
        connectList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                SSHConnInfo connInfo = connectList.getSelectionModel().getSelectedItem();
                MainTab newTab = new MainTab(connInfo);
                String desc = connInfo.getConnectDesc();
                newTab.setText(desc);
                connectTabPane.getTabs().add(newTab);
                connectTabPane.getSelectionModel().select(newTab);
            }
        });
    }

    @FXML
    public void addConnectList() {
        String ip = connectIp.getText();
        String username = connectUser.getText();
        String password = connectUser.getText();
        String desc = connectDesc.getText();
        connectIp.clear();
        connectUser.clear();
        connectPwd.clear();
        connectDesc.clear();
        dbService.insertConnect(new SSHConnInfo(ip,username,password,desc));
        updateConnect();
    }

    private void updateConnect() {
        Platform.runLater(() -> {
            List<SSHConnInfo> connInfos = dbService.queryAllConnect();
            connectList.getItems().clear();
            connectList.getItems().addAll(connInfos);
        });
    }
}
