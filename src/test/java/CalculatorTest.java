import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CalculatorTest {

    private final InputStream originalIn = System.in;

    @AfterEach
    void tearDownStreams() {
        System.setIn(originalIn);
    }

    private void setInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void testGetCurrentItemPrice_WithNumber() {
        setInput("5\n");
        Calculator calculator = new Calculator();

        Calculator.PriceResult result = calculator.getCurrentItemPrice();

        assertTrue(result.isValid());
        assertFalse(result.isZero());
        assertEquals(0.0, calculator.getTotal());
    }

    @Test
    void testGetCurrentItemQuantity_UsesPriceAndQuantity() {
        setInput("5\n2\n");
        Calculator calculator = new Calculator();

        Calculator.PriceResult priceResult = calculator.getCurrentItemPrice();
        Calculator.QuantityResult quantityResult = calculator.getCurrentItemQuantity();

        assertTrue(priceResult.isValid());
        assertFalse(priceResult.isZero());
        assertTrue(quantityResult.isValid());
        assertFalse(quantityResult.isZero());
        assertEquals(10.0, calculator.getTotal());
    }
}
