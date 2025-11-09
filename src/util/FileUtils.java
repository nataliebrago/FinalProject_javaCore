package util;

import exception.FileProcessingException;
import models.Account;

import java.io.*;
import java.math.BigDecimal;  // Добавлен импорт
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class FileUtils {
    private static final String ACCOUNTS_FILE = "resources/accounts.txt";
    private static final String INPUT_DIR = "input";
    private static final String ARCHIVE_DIR = "archive";

    public static Map<String, Account> loadAccounts() throws FileProcessingException {
        Map<String, Account> accounts = new HashMap<>();
        try {
            if (!Files.exists(Paths.get(ACCOUNTS_FILE))) {
                throw new FileProcessingException("Файл accounts.txt не найден.");
            }
            Files.lines(Paths.get(ACCOUNTS_FILE)).forEach(line -> {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    accounts.put(parts[0], new Account(parts[0], new BigDecimal(parts[1])));  // Изменено: парсинг в BigDecimal
                }
            });
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка загрузки accounts.txt");
        }
        return accounts;
    }

    public static void saveAccounts(Map<String, Account> accounts) throws FileProcessingException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (Account acc : accounts.values()) {
                writer.write(acc.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка сохранения accounts.txt");
        }
    }

    public static File[] getInputFiles() {
        File dir = new File(INPUT_DIR);
        if (!dir.exists()) dir.mkdir();
        return dir.listFiles((d, name) -> name.endsWith(".txt"));
    }

    public static void moveToArchive(File file) throws FileProcessingException {
        File archiveDir = new File(ARCHIVE_DIR);
        if (!archiveDir.exists()) archiveDir.mkdir();
        try {
            Files.move(file.toPath(), Paths.get(ARCHIVE_DIR, file.getName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка перемещения файла в archive: " + file.getName());
        }
    }
}