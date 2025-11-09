package service;

import exception.FileProcessingException;
import models.Transaction;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileParser {
    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("\\b(\\d{5}-\\d{5})\\b");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("\\b(\\d+\\.?\\d*)\\b");

    public List<Transaction> parseFile(File file) throws FileProcessingException {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String from = null, to = null;
            BigDecimal amount = null;

            while ((line = reader.readLine()) != null) {
                Matcher accountMatcher = ACCOUNT_PATTERN.matcher(line);
                Matcher amountMatcher = AMOUNT_PATTERN.matcher(line);

                if (from == null && accountMatcher.find()) {
                    from = accountMatcher.group(1);
                } else if ( to == null && accountMatcher.find() ) {
                    to = accountMatcher.group(1);
                }
                else if(amountMatcher.find() && amount == null) {
                    try {
                        amount = new BigDecimal(amountMatcher.group(1));
                    } catch (NumberFormatException e) {
                        throw new FileProcessingException("Неверный формат суммы в файле: " + file.getName());
                    }
                }

                if (from != null && to != null && amount != null) {
                    transactions.add(new Transaction(from, to, amount));
                    from = to = null;
                    amount = null;
                }
            }
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка чтения файла: " + file.getName());
        }
        return transactions;
    }
}