package helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocalizationService {
    private static void applyDefaultStrings(Map<String, String> strings) {
        strings.put("itemNumberPrompt", "Enter the number of items to purchase:");
        strings.put("itemPricePrompt", "Enter the price of the item:");
        strings.put("itemQuantityPrompt", "Enter the quantity of the item:");
        strings.put("totalCostMessage", "Total cost:");
        strings.put("addItemPrompt", "Add Item");
        strings.put("calcItemPrompt", "Calculate Total");
    }

    /**
     * Get localized strings for a specific locale
     */
    public static Map<String, String> getLocalizedStrings(Locale locale) {
        Map<String, String> strings = new HashMap<>();

        String query = "SELECT * FROM localized_strings WHERE locale = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, locale.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String key = resultSet.getString("key");
                    String translation = resultSet.getString("translation");
                    strings.put(key, translation);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load localized strings from database for locale: " + locale);
            applyDefaultStrings(strings);
            return strings;
        }

        if (strings.isEmpty()) {
            // Unknown locale in DB: return defaults instead of an empty map.
            applyDefaultStrings(strings);
        }

//        try {
//            ResourceBundle bundle = ResourceBundle.getBundle(
//                    "MessagesBundle",
//                    locale
//            );
//
//            // Extract all keys
//            for (String key : bundle.keySet()) {
//                strings.put(key, bundle.getString(key));
//            }
//        } catch (Exception e) {
//            System.err.println("Failed to load resource bundle for locale: " + locale);
//            // Fallback to English
//            try {
//                ResourceBundle fallback = ResourceBundle.getBundle(
//                        "MessagesBundle",
//                        new Locale("en", "UK")
//                );
//                for (String key : fallback.keySet()) {
//                    strings.put(key, fallback.getString(key));
//                }
//            } catch (Exception ex) {
//                // Use hardcoded defaults as last resort
//                strings.put("itemNumberPrompt","Enter the number of items to purchase:");
//                strings.put("itemPricePrompt","Enter the price of the item:");
//                strings.put("itemQuantityPrompt","Enter the quantity of the item:");
//                strings.put("totalCostMessage","Total cost:");
//                strings.put("checkout","Enter the quantity of the item:");
//            }
//        }
        return strings;
    }
}