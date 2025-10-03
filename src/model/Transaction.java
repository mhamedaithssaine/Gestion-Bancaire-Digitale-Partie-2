package model;


import model.type.TransactionStatus;
import model.type.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {

    private UUID id;
    private String accountId;
    private UUID createdBy;
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private TransactionStatus status;

    public Transaction() {}

    public Transaction(String accountId, UUID createdBy, TransactionType transactionType,
                       BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.accountId = accountId;
        this.createdBy = createdBy;
        this.transactionType = transactionType;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
        this.status = TransactionStatus.COMPLETED;
    }

    // Getters and Setters
    public UUID getId() { return
            this.id; }
    public void setId(UUID id) { this.id = id; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
}