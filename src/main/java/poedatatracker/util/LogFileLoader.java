package poedatatracker.util;

import poedatatracker.core.models.ItemCategory;
import poedatatracker.core.models.ReceivedCurrency;
import poedatatracker.core.models.ShopSale;
import poedatatracker.core.models.SoldItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogFileLoader {

    private List<String> currencyNames;
    private String delimiter;

    public LogFileLoader(String delimiter, Collection<? extends String> currencyNames) {
        this.delimiter = delimiter;
        this.currencyNames = new ArrayList<>(currencyNames);
    }

    public List<ShopSale> load(String logFilePath) throws IOException {
        LocalDate saleDate = parseDateFromFileName(logFilePath);

        String contents = new String(Files.readAllBytes(Paths.get(logFilePath)));
        int soldIndex = contents.indexOf("Sold:");
        if (soldIndex == -1) {
            return Collections.emptyList();
        }
        contents = contents.substring(soldIndex + "Sold:".length());
        String[] lines = contents.split("\n");
        List<ShopSale> sales = new ArrayList<>();
        for (var line: lines) {
            line = line.strip();
            if (line.isBlank() || line.equalsIgnoreCase("sold:")) {
                continue;
            }
            if (line.equalsIgnoreCase("bought:")) {
                break;
            }
            sales.add(parse(saleDate, line));
        }
        return sales;
    }

    private LocalDate parseDateFromFileName(String path) {
        String filename = new File(path).getName();
        String regex = "\\d{1,2}_\\d{1,2}_\\d{4}";
        Matcher m = Pattern.compile(regex).matcher(filename);
        if (m.find()) {
            return LocalDate.parse(m.group(), DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        } else throw new IllegalArgumentException("unknown date format in file name: " + filename);
    }

    private ShopSale parse(LocalDate saleDate, String saleString) {
        String[] parts = saleString.split(delimiter);
        ItemCategory category = ItemCategory.valueOf(parts[1].toUpperCase().strip().replace(' ', '_').replace("SUPPORT", "SKILL"));
        String name = parts[0];
        int amount = 1;
        if (parts[0].matches("\\-\\s*\\d+x.*")) {
            var amountStr = parts[0].
                    chars().
                    dropWhile(c -> c == '-' || Character.isWhitespace(c)).
                    takeWhile(c -> c != 'x').
                    mapToObj(Character::toString).
                    collect(Collectors.joining(""));
            amount = Integer.parseInt(amountStr);
            name = parts[0].
                    chars().
                    dropWhile(c -> c != 'x').
                    skip(1).
                    mapToObj(Character::toString).
                    collect(Collectors.joining("")).
                    strip();
        } else {
            name = parts[0].substring(1).strip();
        }
        List<ReceivedCurrency> currencies = new ArrayList<>();
        String[] currencyInfo = parts[2].strip().split(" ");
        for (int i = 0; i < currencyInfo.length; i += 2) {
            int currencyAmount = Integer.parseInt(currencyInfo[i].strip());
            String targetName = currencyInfo[i + 1].strip();
            Optional<String> currencyNameOpt = currencyNames.stream().filter(cn -> cn.toLowerCase().contains(targetName)).findFirst();
            currencyNameOpt.ifPresent(currencyName -> {
                var receivedCurrency = new ReceivedCurrency(currencyName, currencyAmount);
                currencies.add(receivedCurrency);
            });
        }
        var item = new SoldItem(name, amount, category);
        var sale = new ShopSale(0L, saleDate, currencies, item);
        item.setSale(sale);
        currencies.forEach(c -> c.setSale(sale));
        return sale;
    }
}
