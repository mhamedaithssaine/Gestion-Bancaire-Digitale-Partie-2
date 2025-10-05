package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Credit {
    private UUID id;
    private UUID clientId;
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime requestedAt;
    private String status; // PENDING, APPROVED, REJECTED

    public Credit() {}

    public Credit(UUID clientId, String accountId, BigDecimal amount, String currency) {
        this.id = UUID.randomUUID();
        this.clientId = clientId;
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
        this.requestedAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}