package threadmonitor.util;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application{

    @Override
    public void start(Stage primaryStage) {
        Button button = new Button();
        button.setText("Open a window");
        button.setOnAction(e -> new AlertBox().display("title", "message"));

        AnchorPane layout = new AnchorPane();
        layout.getChildren().add(button);

        Scene scene=new Scene(layout,300,300);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}