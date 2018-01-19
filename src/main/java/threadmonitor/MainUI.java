package threadmonitor;/*
 * Copyright (c) 2018年01月05日 by XuanWu Wireless Technology Co.Ltd. 
 *             All rights reserved                         
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * @Description
 * @Author <a href="mailto:haosonglin@wxchina.com">songlin.Hao</a>
 * @Date 2018/1/5
 * @Version 1.0.0
 */
public class MainUI extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Monitor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
