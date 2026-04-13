import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        URL mainUi = Main.class.getResource("/mainUI.fxml");
        if (mainUi == null) {
            throw new IllegalStateException("Could not load /mainUI.fxml from classpath.");
        }

        Scene scene = new Scene(FXMLLoader.load(mainUi));
        stage.setTitle("calculator.PriceCalculator");
        stage.setScene(scene);
        stage.show();
    }
}
