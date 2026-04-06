import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @AfterEach
    void tearDown() {
        calculator = null;
    }

    private static <T> T readPrivateField(Object target, String fieldName, Class<T> type) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return type.cast(field.get(target));
    }

    @Test
    void getCurrentItemPrice_acceptsPositiveNumberAndStagesValue() throws Exception {
        Calculator.PriceResult result = calculator.getCurrentItemPrice("5");

        assertTrue(result.isValid());
        assertFalse(result.isZero());
        assertEquals(5.0, readPrivateField(calculator, "currentValue", Double.class));
    }

    @Test
    void getCurrentItemPrice_rejectsZeroAndInvalidInput() {
        Calculator.PriceResult zeroResult = calculator.getCurrentItemPrice("0");
        Calculator.PriceResult invalidResult = calculator.getCurrentItemPrice("abc");

        assertTrue(zeroResult.isValid());
        assertTrue(zeroResult.isZero());
        assertFalse(invalidResult.isValid());
        assertFalse(invalidResult.isZero());
    }

    @Test
    void getCurrentItemQuantity_requiresAStagedPriceBeforeAcceptingQuantity() throws Exception {
        Calculator.QuantityResult quantityResult = calculator.getCurrentItemQuantity("2");

        assertFalse(quantityResult.isValid());
        assertFalse(quantityResult.isZero());
        assertEquals(0, readPrivateField(calculator, "totalItems", Integer.class));
        assertEquals(0.0, readPrivateField(calculator, "total", Double.class));
    }

    @Test
    void getCurrentItemQuantity_stagesItemInMemoryWhenPriceExists() throws Exception {
        calculator.getCurrentItemPrice("5");

        Calculator.QuantityResult quantityResult = calculator.getCurrentItemQuantity("2");

        assertTrue(quantityResult.isValid());
        assertFalse(quantityResult.isZero());
        assertEquals(1, readPrivateField(calculator, "totalItems", Integer.class));
        assertEquals(10.0, readPrivateField(calculator, "total", Double.class));
        assertEquals(0.0, readPrivateField(calculator, "currentValue", Double.class));

        Object pendingItems = readPrivateField(calculator, "pendingItems", Object.class);
        assertNotNull(pendingItems);
        assertEquals(1, ((java.util.List<?>) pendingItems).size());
    }

    @Test
    void getTotal_returnsCurrentTotalWhenNothingHasBeenStaged() {
        assertEquals(0.0, calculator.getTotal("en_UK"));
    }
}
