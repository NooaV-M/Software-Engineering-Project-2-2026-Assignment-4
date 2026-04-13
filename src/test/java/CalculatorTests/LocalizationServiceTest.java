package CalculatorTests;

import helper.LocalizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Locale;
import java.util.Map;

class LocalizationServiceTest {
    @Test
     void getLocalizedStrings_fallsBackToHardcodedDefaultsWhenLocaleMissing() {
        Map<String, String> localizedStrings = LocalizationService.getLocalizedStrings(new Locale("xx", "XX"));

        Assertions.assertEquals("Enter the number of items to purchase:", localizedStrings.get("itemNumberPrompt"));
        Assertions.assertEquals("Enter the price of the item:", localizedStrings.get("itemPricePrompt"));
        Assertions.assertEquals("Enter the quantity of the item:", localizedStrings.get("itemQuantityPrompt"));
        Assertions.assertEquals("Total cost:", localizedStrings.get("totalCostMessage"));
        Assertions.assertEquals("Add Item", localizedStrings.get("addItemPrompt"));
        Assertions.assertEquals("Calculate Total", localizedStrings.get("calcItemPrompt"));
    }
}
