import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Main extends Application {
    private static Scanner scanner = new Scanner(System.in);

    static Locale promptLanguage() {
        System.out.println("To select English, type 1.");
        System.out.println("Suomen kielen valitsemiseksi, kirjoita 2.");
        System.out.println("Fyr att välja svenska, skriv 3.");
        System.out.println("日本語を選択するには4を押してください。");
        switch (scanner.nextLine()) {
            case "1":
                return new Locale("en", "UK");
            case "2":
                return new Locale("fi", "FI");
            case "3":
                return new Locale("sv", "SE");
            case "4":
                return new Locale("ja", "JP");
            default:
                System.out.println("Invalid selection. Defaulting to English.");
                return Locale.ENGLISH;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        URL mainUi = Main.class.getResource("/mainUI.fxml");
        if (mainUi == null) {
            throw new IllegalStateException("Could not load /mainUI.fxml from classpath.");
        }

        Scene scene = new Scene(FXMLLoader.load(mainUi));
        stage.setTitle("Calculator");
        stage.setScene(scene);
        stage.show();

//        Calculator calculator = new Calculator();
//        boolean gettingTotalQuantity = true;
//        boolean gettingQuantity = true;
//        boolean gettingPrice = false;
//        int totalQuantity = 0;
//        Locale selectedLocale = promptLanguage();
//        ResourceBundle resourceBundle = ResourceBundle.getBundle("MessagesBundle", selectedLocale);
//
//        while (gettingTotalQuantity) {
//            System.out.println(resourceBundle.getString("itemNumberPrompt"));
//            System.out.println(resourceBundle.getString("checkOutPrompt"));
//            System.out.println();
//            try {
//                String input = scanner.nextLine();
//                totalQuantity = Integer.parseInt(input);
//                if (totalQuantity > 0) {
//                    gettingTotalQuantity = false;
//                }
//            } catch (NumberFormatException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        for (int i = 0; i < totalQuantity; i++) {
//            while (gettingQuantity) {
//                System.out.println(resourceBundle.getString("itemQuantityPrompt"));
//                System.out.println(resourceBundle.getString("checkOutPrompt"));
//                System.out.println();
//                Calculator.PriceResult priceResult = calculator.getCurrentItemPrice();
//                if (priceResult.isValid()) {
//                    if (priceResult.isZero()) {
//                        gettingQuantity = false;
//                        gettingPrice = false;
//                    } else {
//                        gettingQuantity = false;
//                        gettingPrice = true;
//                    }
//                }
//            }
//            while (gettingPrice) {
//                System.out.println(resourceBundle.getString("itemPricePrompt"));
//                System.out.println(resourceBundle.getString("checkOutPrompt"));
//                System.out.println();
//                Calculator.QuantityResult quantityResult = calculator.getCurrentItemQuantity();
//                if (quantityResult.isValid()) {
//                    if (quantityResult.isZero()) {
//                        gettingPrice = false;
//                        gettingQuantity = false;
//                    } else {
//                        gettingPrice = false;
//                        gettingQuantity = true;
//                    }
//                }
//            }
//        }
//        System.out.println(resourceBundle.getString("totalCostMessage") + " " + calculator.getTotal());
    }
}
