import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Calculator {
    private double total = 0;
    private double currentValue = -1;
    private int totalItems = 0;
    private final List<CartItem> pendingItems = new ArrayList<>();

    private record CartItem(double price, int quantity, double subtotal) {}

    public record PriceResult(boolean isValid, boolean isZero) {}

    public record QuantityResult(boolean isValid, boolean isZero) {}

    public PriceResult getCurrentItemPrice(String input) {
        try {
            double enteredValue = Double.parseDouble(input);
            if (enteredValue > 0) {
                currentValue = enteredValue;
                return new PriceResult(true, false);
            } else if (enteredValue == 0) {
                return new PriceResult(true, true);
            } else {
                return new PriceResult(false, false);
            }
        } catch (NumberFormatException e) {
            return new PriceResult(false, false);
        }
    }

    public QuantityResult getCurrentItemQuantity(String input) {
        try {
            int enteredValue = Integer.parseInt(input);
            if (enteredValue > 0) {
                // Quantity is accepted only after a valid price has been staged.
                if (currentValue <= 0) {
                    return new QuantityResult(false, false);
                }

                double subtotal = currentValue * enteredValue;
                pendingItems.add(new CartItem(currentValue, enteredValue, subtotal));

                total += subtotal;
                currentValue = 0;
                totalItems += 1;


                return new QuantityResult(true, false);
            } else if (enteredValue == 0) {
                return new QuantityResult(true, true);
            } else {
                return new QuantityResult(false, false);
            }
        } catch (NumberFormatException e) {
            return new QuantityResult(false, false);
        }
    }

    public double getTotal(String currentLang) {

        if (pendingItems.isEmpty()) {
            return total;
        }

        double checkoutTotal = total;

        String recordQuery = "INSERT INTO cart_records (total_items, total_cost, language, created_at) VALUES (?, ?, ?, ?)";
        String itemQuery = "INSERT INTO cart_items (cart_record_id, price, quantity, subtotal) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            int cartRecordId;
            try (PreparedStatement statement = connection.prepareStatement(recordQuery, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, totalItems);
                statement.setDouble(2, total);
                statement.setString(3, currentLang);
                statement.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        throw new SQLException("Failed to create cart record id.");
                    }
                    cartRecordId = generatedKeys.getInt(1);
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(itemQuery)) {
                for (CartItem item : pendingItems) {
                    statement.setInt(1, cartRecordId);
                    statement.setDouble(2, item.price());
                    statement.setInt(3, item.quantity());
                    statement.setDouble(4, item.subtotal());
                    statement.addBatch();
                }
                statement.executeBatch();
            }

            connection.commit();

            // Reset state for the next cart after a successful checkout.
            pendingItems.clear();
            total = 0;
            totalItems = 0;
            currentValue = -1;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to persist cart and item rows.", e);
        }


        return checkoutTotal;
    }
}
