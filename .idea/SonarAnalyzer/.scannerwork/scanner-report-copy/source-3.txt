import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.Locale;
import java.util.Map;

public class CalculatorController {

    public HBox langButtonBox;
    public Label lblResult;
    public Label lblPrice;
    public Label lblQuantity;
    public Button btnCalc;
    public Button btnAdd;
    public Label lblResultNum;
    public TextField textFieldQuantity;
    public TextField textFieldPrice;
    private Locale currentLocale = new Locale("en","UK");
    private Map<String, String> localizedStrings;

    Calculator calculator = new Calculator();

    @FXML
    public void initialize() {
        setLanguage(currentLocale);

        Button engButton = createLanguageButton("English", new Locale("en", "UK"));
        Button sweButton = createLanguageButton("Swedish", new Locale("sv", "SE"));
        Button jpaButton = createLanguageButton("日本語", new Locale("ja", "JP"));
        Button araButton = createLanguageButton("العربية", new Locale("ar", "SA"));

        langButtonBox.getChildren().addAll(engButton, sweButton, jpaButton, araButton);

        lblResultNum.setText("");
    }

    private Button createLanguageButton(String text, Locale locale) {
        Button button = new Button(text);
        button.setOnAction(e -> setLanguage(locale));
        HBox.setMargin(button, new javafx.geometry.Insets(5, 10, 5, 10));
        return button;
    }

    private void setLanguage(Locale locale) {
        currentLocale = locale;
        lblResult.setText(""); // Clear previous result

        // Load localized strings
        localizedStrings = LocalizationService.getLocalizedStrings(locale);

        // Update all UI text
        lblQuantity.setText(localizedStrings.getOrDefault("itemNumberPrompt", "Enter the number of items to purchase:"));
        lblPrice.setText(localizedStrings.getOrDefault("itemPricePrompt", "Enter the price for item:"));
        lblResult.setText(localizedStrings.getOrDefault("totalCostMessage", "Total cost:"));
        btnAdd.setText(localizedStrings.getOrDefault("addItemPrompt", "Calculate Average Speed"));
        btnCalc.setText(localizedStrings.getOrDefault("calcItemPrompt", "Calculate Total"));

        // Apply text direction based on language
//        applyTextDirection(locale);
    }

    public void calculateTotal(ActionEvent actionEvent) {
        Double total = calculator.getTotal(currentLocale.toString());

        lblResultNum.setText(Double.toString(total));
        System.out.println("Total cost: " + total);
    }

    public void addToTotal(ActionEvent actionEvent) {
        calculator.getCurrentItemPrice(textFieldPrice.getText());
        calculator.getCurrentItemQuantity(textFieldQuantity.getText());

        textFieldPrice.setText("");
        textFieldQuantity.setText("");
    }
}
