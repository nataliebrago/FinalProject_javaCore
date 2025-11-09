package main;

import exception.FileProcessingException;
import exception.InvalidAccountException;
import exception.InvalidAmountException;
import models.Account;
import models.Transaction;
import service.FileParser;
import service.ReportService;
import service.TransferService;
import util.FileUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TransferService transferService = new TransferService();
        FileParser fileParser = new FileParser();
        ReportService reportService = new ReportService();

        while (true) {
            System.out.println("Введите 1 для парсинга файлов, 2 для вывода полного отчета, 3 для вывода истории по датам (или 'exit' для выхода):");
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) break;

            if ("1".equals(input)) {
                try {
                    Map<String, Account> accounts = FileUtils.loadAccounts();
                    File[] files = FileUtils.getInputFiles();
                    if (files == null || files.length == 0) {
                        System.out.println("Нет подходящих файлов в input.");
                        continue;
                    }

                    for (File file : files) {
                        try {
                            List<Transaction> transactions = fileParser.parseFile(file);
                            for (Transaction t : transactions) {
                                try {
                                    transferService.processTransfer(t, accounts);
                                    reportService.addEntry(file.getName(), "перевод с " + t.getFromAccount() + " на " + t.getToAccount() + " " + t.getAmount(), "успешно обработан");
                                } catch (InvalidAccountException | InvalidAmountException e) {
                                    reportService.addEntry(file.getName(), "перевод с " + t.getFromAccount() + " на " + t.getToAccount() + " " + t.getAmount(), "ошибка: " + e.getMessage());
                                }
                            }
                            FileUtils.moveToArchive(file);
                        } catch (FileProcessingException e) {
                            reportService.addEntry(file.getName(), "ошибка парсинга", e.getMessage());
                        }
                    }
                    FileUtils.saveAccounts(accounts);
                    System.out.println("Парсинг завершен.");
                } catch (FileProcessingException e) {
                    System.err.println("Ошибка: " + e.getMessage());
                }
            } else if ("2".equals(input)) {
                reportService.printReport();
            } else if ("3".equals(input)) {
                System.out.println("Введите начальную дату (YYYY-MM-DD):");
                String startStr = scanner.nextLine();
                System.out.println("Введите конечную дату (YYYY-MM-DD):");
                String endStr = scanner.nextLine();
                try {
                    LocalDate start = LocalDate.parse(startStr, DateTimeFormatter.ISO_LOCAL_DATE);
                    LocalDate end = LocalDate.parse(endStr, DateTimeFormatter.ISO_LOCAL_DATE);
                    reportService.printReportByDateRange(start, end);
                } catch (DateTimeParseException e) {
                    System.out.println("Неверный формат даты. Используйте YYYY-MM-DD.");
                }
            } else {
                System.out.println("Неверный ввод.");
            }
        }
    }
}