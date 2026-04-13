package CalculatorTests;

import javafx.scene.layout.HBox;
import ui.CalculatorController;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CalculatorControllerTest {
    private CalculatorController calculatorController;

    @BeforeAll
    static void initJavaFx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // JavaFX toolkit already initialized.
        }
    }

    @BeforeEach
    void setUp() {
        calculatorController = new CalculatorController();

        calculatorController.textFieldPrice = new TextField();
        calculatorController.textFieldQuantity = new TextField();
        calculatorController.lblResultNum = new Label();
        calculatorController.btnCalc = new Button();
        calculatorController.lblResult = new Label();
        calculatorController.lblQuantity = new Label();
        calculatorController.lblPrice = new Label();
        calculatorController.btnAdd = new Button();
        calculatorController.langButtonBox = new HBox();
    }

    @AfterEach
    void tearDown() {
        calculatorController = null;
    }

    @Test
    void initTest() {
        calculatorController.initialize();
        Assertions.assertEquals( 4, calculatorController.langButtonBox.getChildren().size());
    }

    @Test
    void calculateTotalTest() {

        // Simulate user input for price and quantity
        calculatorController.textFieldPrice.setText("10");
        calculatorController.textFieldQuantity.setText("2");

        calculatorController.addToTotal();
        calculatorController.calculateTotal();

        // Assert that the result label shows the correct total
        Assertions.assertEquals("20.0", calculatorController.lblResultNum.getText());
    }
}
