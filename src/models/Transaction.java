package models;

import java.math.BigDecimal;

public class Transaction {
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;

    public Transaction(String fromAccount, String toAccount, BigDecimal amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}