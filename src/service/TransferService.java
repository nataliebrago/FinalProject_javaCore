package service;

import exception.InvalidAccountException;
import exception.InvalidAmountException;
import models.Account;
import models.Transaction;

import java.math.BigDecimal;
import java.util.Map;

public class TransferService {
    public void processTransfer(Transaction transaction, Map<String, Account> accounts) throws InvalidAccountException, InvalidAmountException {
        Account from = accounts.get(transaction.getFromAccount());
        Account to = accounts.get(transaction.getToAccount());

        if (from == null || to == null) {
            throw new InvalidAccountException("Неверный номер счета: " + (from == null ? transaction.getFromAccount() : transaction.getToAccount()));
        }
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {  // Изменено: сравнение с BigDecimal
            throw new InvalidAmountException("Сумма должна быть положительной: " + transaction.getAmount());
        }
        if (from.getBalance().compareTo(transaction.getAmount()) < 0) {  // Изменено: сравнение баланса
            throw new InvalidAmountException("Недостаточно средств на счете " + from.getAccountNumber());
        }

        from.setBalance(from.getBalance().subtract(transaction.getAmount()));  // Изменено: вычитание
        to.setBalance(to.getBalance().add(transaction.getAmount()));  // Изменено: сложение
    }
}