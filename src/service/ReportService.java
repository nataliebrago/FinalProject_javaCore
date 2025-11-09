package service;

import models.ReportEntry;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {
    private static final String REPORT_FILE = "resources/transfers_report.txt";

    public void addEntry(String fileName, String description, String status) {
        ReportEntry entry = new ReportEntry(LocalDateTime.now(), fileName, description, status);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REPORT_FILE, true))) {
            writer.write(entry.toString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Ошибка записи в отчет: " + e.getMessage());
        }
    }

    public void printReport() {
        try {
            if (!Files.exists(Paths.get(REPORT_FILE))) {
                System.out.println("Отчет не найден.");
                return;
            }
            List<String> lines = Files.readAllLines(Paths.get(REPORT_FILE));
            lines.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Ошибка чтения отчета: " + e.getMessage());
        }
    }

    // вывод истории по диапазону дат
    public void printReportByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            if (!Files.exists(Paths.get(REPORT_FILE))) {
                System.out.println("Отчет не найден.");
                return;
            }
            List<String> lines = Files.readAllLines(Paths.get(REPORT_FILE));
            List<String> filteredLines = lines.stream()
                    .filter(line -> {
                        try {
                            // Парсинг даты из строки (формат: YYYY-MM-DDTHH:MM:SS)
                            String dateStr = line.split(" \\| ")[0];  // Извлекаем timestamp
                            LocalDate entryDate = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate();
                            return !entryDate.isBefore(startDate) && !entryDate.isAfter(endDate);
                        } catch (Exception e) {
                            return false;  // Если не удается распарсить, пропускаем
                        }
                    })
                    .collect(Collectors.toList());
            if (filteredLines.isEmpty()) {
                System.out.println("Нет записей в указанном диапазоне.");
            } else {
                filteredLines.forEach(System.out::println);
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения отчета: " + e.getMessage());
        }
    }
}