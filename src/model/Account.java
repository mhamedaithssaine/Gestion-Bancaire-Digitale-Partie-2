package model;

import java.math.BigDecimal;
import java.util.UUID;
import model.type.AccountType;

public class Account {
    private String accountId; // Ex. BK-2025-0001
    private AccountType type;
    private BigDecimal balance;
    private String currency;
    private UUID clientId;

    public Account(String accountId, AccountType type, BigDecimal balance, String currency, UUID clientId) {
        this.accountId = accountId;
        this.type = type;
        this.balance = balance;
        this.currency = currency;
        this.clientId = clientId;
    }


    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }
}
