package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BankRevenue {
    private UUID id;
    private String sourceType;
    private String sourceSubType;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime occurredAt;
    private UUID transferId;
    private String note;

    public BankRevenue() {}

    public BankRevenue(String sourceType, String sourceSubType, BigDecimal amount,
                       String currency, UUID transferId, String note) {
        this.id = UUID.randomUUID();
        this.sourceType = sourceType;
        this.sourceSubType = sourceSubType;
        this.amount = amount;
        this.currency = currency;
        this.occurredAt = LocalDateTime.now();
        this.transferId = transferId;
        this.note = note;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceSubType() {
        return sourceSubType;
    }

    public void setSourceSubType(String sourceSubType) {
        this.sourceSubType = sourceSubType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public UUID getTransferId() {
        return transferId;
    }

    public void setTransferId(UUID transferId) {
        this.transferId = transferId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
