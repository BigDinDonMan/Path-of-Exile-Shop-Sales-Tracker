import com.google.gson.Gson;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogFileLoaderTests {

    private static final String TEST_FILES_DIR = System.getProperty("user.dir") + File.separator + "test-files" + File.separator;
    private static LogFileLoader loader;
    private static ShopSaleComparer comparer;
    private static List<String> currencies;

    @BeforeClass
    public static void initializeResources() throws IOException {
        Gson g = new Gson();
        String path = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "currencies.json";
        byte[] fileBytes = Files.readAllBytes(Paths.get(path));
        String json = new String(fileBytes);
        currencies = new ArrayList<>(Arrays.asList(g.fromJson(json, String[].class)));
        comparer = new ShopSaleComparer();
    }

    @Test
    public void logFileLoaderTest_shouldSucceed_withTestFile1_everythingFine() throws IOException {
        loader = new LogFileLoader(",", currencies);
        var sales = loader.load(TEST_FILES_DIR + "shop_transactions_25_01_2021.txt");
        var date = LocalDate.of(2021, 1, 25);
        var expected = Arrays.asList(
                new ShopSale(
                        new SoldItem("Deafening Essence of Anger", 3, ItemCategory.CRAFTING_SUPPLY),
                        date,
                        new ReceivedCurrency("Chaos Orb", 30)
                ),
                new ShopSale(
                        new SoldItem("Ahn's Might", 1, ItemCategory.UNIQUE),
                        date,
                        new ReceivedCurrency("Chaos Orb", 2)
                ),
                new ShopSale(
                        new SoldItem("Bound Fossil", 3, ItemCategory.CRAFTING_SUPPLY),
                        date,
                        new ReceivedCurrency("Chaos Orb", 10)
                ),
                new ShopSale(
                        new SoldItem("Agnerod North", 1, ItemCategory.UNIQUE),
                        date,
                        new ReceivedCurrency("Chaos Orb", 5)
                )
        );
        Assert.assertEquals(expected.size(), sales.size());
        for (int i = 0; i < sales.size(); ++i) {
            var s1 = sales.get(i);
            var s2 = expected.get(i);
            Assert.assertTrue(comparer.salesEqual(s1, s2));
        }
    }

}
