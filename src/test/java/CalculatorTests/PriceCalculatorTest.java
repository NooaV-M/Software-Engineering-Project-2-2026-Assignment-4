package CalculatorTests;

import calculator.PriceCalculator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class PriceCalculatorTest {

    private PriceCalculator priceCalculator;

    @BeforeEach
    void setUp() {
        priceCalculator = new PriceCalculator();
    }

    @AfterEach
    void tearDown() {
        priceCalculator = null;
    }

    private static <T> T readPrivateField(Object target, String fieldName, Class<T> type) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return type.cast(field.get(target));
    }

    @Test
    void getCurrentItemPrice_acceptsPositiveNumberAndStagesValue() throws Exception {
        priceCalculator.getCurrentItemPrice("5");

        assertEquals(5.0, readPrivateField(priceCalculator, "currentValue", Double.class));
    }

    @Test
    void getCurrentItemPrice_keepsStateOnZeroAndThrowsOnInvalidInput() throws Exception {
        priceCalculator.getCurrentItemPrice("5");
        priceCalculator.getCurrentItemPrice("0");

        assertEquals(5.0, readPrivateField(priceCalculator, "currentValue", Double.class));

        assertThrows(NumberFormatException.class, () -> priceCalculator.getCurrentItemPrice("abc"));
    }

    @Test
    void getCurrentItemQuantity_throwsNumberFormatExceptionOnInvalidInput() throws Exception {
        assertThrows(NumberFormatException.class, () -> priceCalculator.getCurrentItemQuantity("abc"));

        assertEquals(0, readPrivateField(priceCalculator, "totalItems", Integer.class));
        assertEquals(0.0, readPrivateField(priceCalculator, "total", Double.class));

        List<?> pendingItems = readPrivateField(priceCalculator, "pendingItems", List.class);
        assertEquals(0, pendingItems.size());
    }

    @Test
    void getCurrentItemQuantity_requiresAStagedPriceBeforeAcceptingQuantity() throws Exception {
        priceCalculator.getCurrentItemQuantity("2");

        assertEquals(0, readPrivateField(priceCalculator, "totalItems", Integer.class));
        assertEquals(0.0, readPrivateField(priceCalculator, "total", Double.class));

        List<?> pendingItems = readPrivateField(priceCalculator, "pendingItems", List.class);
        assertEquals(0, pendingItems.size());
    }

    @Test
    void getCurrentItemQuantity_stagesItemInMemoryWhenPriceExists() throws Exception {
        priceCalculator.getCurrentItemPrice("5");

        priceCalculator.getCurrentItemQuantity("2");

        assertEquals(1, readPrivateField(priceCalculator, "totalItems", Integer.class));
        assertEquals(10.0, readPrivateField(priceCalculator, "total", Double.class));
        assertEquals(0.0, readPrivateField(priceCalculator, "currentValue", Double.class));

        Object pendingItems = readPrivateField(priceCalculator, "pendingItems", Object.class);
        assertNotNull(pendingItems);
        List<?> stagedItems = (List<?>) pendingItems;
        assertEquals(1, stagedItems.size());

        Object stagedItem = stagedItems.get(0);
        assertTrue(stagedItem.getClass().getSimpleName().contains("CartItem"));
    }

    @Test
    void getTotal_returnsCurrentTotalWhenNothingHasBeenStaged() {
        Assertions.assertEquals(0.0, priceCalculator.getTotal("en_UK"));
    }

    @Test
    void getTotal_returnsCurrentTotalWhenItemsHaveBeenStaged() {
        priceCalculator.getCurrentItemPrice("5");
        priceCalculator.getCurrentItemQuantity("2");

        Assertions.assertEquals(10.0, priceCalculator.getTotal("en_UK"));
    }

    @Test
    void getTotal_ThowsSQLExceptioonWhenDatabaseIsUnavailable() {
        Connection connection = mock(Connection.class);
        try {
            when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                    .thenThrow(new SQLException("boom"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        priceCalculator = new PriceCalculator(() -> connection);
        priceCalculator.getCurrentItemPrice("5");
        priceCalculator.getCurrentItemQuantity("2");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> priceCalculator.getTotal("en_UK"));
        assertTrue(ex.getMessage().contains("Failed to persist cart and item rows."));
        assertTrue(ex.getCause() instanceof SQLException);

    }

    @Test
    void getTotal_throwsWhenGeneratedKeysAreMissing() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement recordStatement = mock(PreparedStatement.class);
        ResultSet generatedKeys = mock(ResultSet.class);

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(recordStatement);
        when(recordStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(false);

        priceCalculator = new PriceCalculator(() -> connection);
        priceCalculator.getCurrentItemPrice("5");
        priceCalculator.getCurrentItemQuantity("2");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> priceCalculator.getTotal("en_UK"));
        assertTrue(ex.getCause() instanceof SQLException);
        assertTrue(ex.getCause().getMessage().contains("Failed to create cart record id."));
    }


}
