package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Credit {
    private UUID id;
    private UUID clientId;
    private BigDecimal amount;
    private int durationMonths;
    private BigDecimal interestRate;
    private String incomeProof;
    private BigDecimal monthlyPayment;
    private LocalDateTime submissionDate;
    private LocalDateTime decisionDate;
    private String status;
    private String comments;

    public Credit(UUID clientId, BigDecimal amount, int durationMonths, BigDecimal interestRate, String incomeProof) {
        this.id = UUID.randomUUID();
        this.clientId = clientId;
        this.amount = amount;
        this.durationMonths = durationMonths;
        this.interestRate = interestRate;
        this.incomeProof = incomeProof;
        this.submissionDate = LocalDateTime.now();
        this.status = "PENDING";
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public String getIncomeProof() {
        return incomeProof;
    }

    public void setIncomeProof(String incomeProof) {
        this.incomeProof = incomeProof;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDateTime getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(LocalDateTime decisionDate) {
        this.decisionDate = decisionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
