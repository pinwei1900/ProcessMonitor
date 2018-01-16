package threadmonitor;/*
 * Copyright (c) 2018年01月05日 by XuanWu Wireless Technology Co.Ltd. 
 *             All rights reserved                         
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import threadmonitor.controller.Controller;
import threadmonitor.services.ProsessService;
import threadmonitor.services.SshService;

/**
 * @Description
 * @Author <a href="mailto:haosonglin@wxchina.com">songlin.Hao</a>
 * @Date 2018/1/5
 * @Version 1.0.0
 */
public class MainUI extends Application{
    private SshService sshService = new SshService();
    private ProsessService prosessService = new ProsessService();

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();

        Controller controller = loader.getController();
        controller.setProsessService(prosessService);

        startProsessThread(controller);
    }

    private void startConsoleThread() {
        new Thread(() -> {
            try {
                sshService.ConsoleTTY();
            } catch (Throwable ignored) {
            }
        },"console").start();
    }

    private void startProsessThread(Controller controller) {
        new Thread(() -> {
            try {
                prosessService.init();
                controller.init();
            } catch (Throwable ignored) {
            }
        },"prosess").start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
