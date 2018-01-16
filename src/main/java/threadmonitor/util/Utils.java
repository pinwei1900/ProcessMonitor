package threadmonitor.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;

public class Utils {

    public static String dateToString(Date time){
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        return formatter.format(time);
    }

    public static String getHMS(Date date){
        return new SimpleDateFormat("hh:mm:ss").format(date.getTime());
    }
    public static String getYMD(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
    }

    public static Number strToNum(String str) throws ParseException {
        return NumberFormat.getInstance().parse(str);
    }

    public static String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public static boolean askQuestion(String message) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<Boolean>();
        completableFuture.runAsync(() -> {
            ThreadHelper.runActionLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.YES, ButtonType.NO);
                ButtonType buttonType = alert.showAndWait().orElse(ButtonType.NO);
                completableFuture.complete(buttonType == ButtonType.YES);
            });
        });

        return completableFuture.join();
    }

    public static String askInput(String message) {
        CompletableFuture<String> completableFuture = new CompletableFuture<String>();
        completableFuture.runAsync(() -> {
            ThreadHelper.runActionLater(() -> {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setContentText(message);
                Optional<String> optional = inputDialog.showAndWait();
                completableFuture.complete(optional.orElseGet(() -> null));
            });
        });
        return completableFuture.join();
    }
}
