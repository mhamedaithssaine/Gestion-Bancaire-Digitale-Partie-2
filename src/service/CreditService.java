package service;

import model.Account;
import model.Credit;
import repository.impl.AccountRepositoryImp;
import repository.impl.CreditRepositoryImp;
import repository.repositoryInterface.AccountRepository;
import repository.repositoryInterface.CreditRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CreditService {
    private static CreditService instance;
    private final CreditRepository creditRepository;
    private final AccountRepository accountRepository;

    private CreditService() {
        this.creditRepository = CreditRepositoryImp.getInstance();
        this.accountRepository = AccountRepositoryImp.getInstance();
    }

    public static CreditService getInstance() {
        if (instance == null) {
            instance = new CreditService();
        }
        return instance;
    }

    public boolean requestCredit(UUID clientId, String accountId, BigDecimal amount, String currency) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Credit amount must be positive.");
            return false;
        }

        // Check if the account is of type "CREDIT"
        Account account = accountRepository.findAccountById(accountId).orElse(null);
        if (account == null || !account.getType().equals(model.type.AccountType.CREDIT)) {
            System.out.println("Client must have a credit-type account to request a loan.");
            return false;
        }

        // Validate bank_revenue for October 2025
        BigDecimal revenue = getMonthlyRevenue(clientId);
        if (revenue == null || revenue.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Insufficient or no revenue recorded in bank_revenue for October 2025.");
            return false;
        }

        Credit credit = new Credit(clientId, accountId, amount, currency);
        credit.setStatus("PENDING");
        credit.setRequestedAt(LocalDateTime.of(2025, 10, 5, 21, 20)); // Current time
        return creditRepository.createCredit(credit);
    }

    private BigDecimal getMonthlyRevenue(UUID clientId) {
        // Simulated query for October 2025 revenue
        // Replace with actual database query: SELECT COALESCE(SUM(amount), 0) FROM bank_revenue WHERE client_id = ? AND date >= '2025-10-01' AND date < '2025-11-01'
        return new BigDecimal("1000.00"); // Dummy value for testing
    }

    public List<Credit> getCreditsByClient(UUID clientId) {
        return creditRepository.findByClientId(clientId);
    }

    public Credit getCreditById(UUID creditId) {
        return creditRepository.findById(creditId);
    }

    public List<Credit> creditFollowUp(UUID clientId) {
        return creditRepository.findByClientId(clientId);
    }

    public boolean repayCredit(UUID creditId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Repayment amount must be positive.");
            return false;
        }
        Credit credit = creditRepository.findById(creditId);
        if (credit == null) {
            System.out.println("Credit not found.");
            return false;
        }
        if (!"APPROVED".equals(credit.getStatus())) {
            System.out.println("Cannot repay a non-approved credit.");
            return false;
        }
        if (amount.compareTo(credit.getAmount()) > 0) {
            System.out.println("Repayment amount cannot exceed remaining credit.");
            return false;
        }
        return creditRepository.repayCredit(creditId, amount);
    }

    public boolean approveOrRejectCredit(UUID creditId, String action) {
        if (!"approve".equalsIgnoreCase(action) && !"reject".equalsIgnoreCase(action)) {
            System.out.println("Invalid action. Must be 'approve' or 'reject'.");
            return false;
        }
        String status = "approve".equalsIgnoreCase(action) ? "APPROVED" : "REJECTED";
        boolean result = creditRepository.approveOrRejectCredit(creditId, status);
        if (result && "APPROVED".equals(status)) {
            Credit credit = creditRepository.findById(creditId);
            if (credit != null) {
                TransactionService transactionService = new TransactionService(); // Assume exists
                boolean transferred = transactionService.deposit(credit.getAccountId(), null, credit.getAmount());
                if (!transferred) {
                    System.out.println("Failed to transfer funds to credit account.");
                    creditRepository.approveOrRejectCredit(creditId, "PENDING");
                    return false;
                }
                System.out.println("Funds transferred to account " + credit.getAccountId() + " at " + LocalDateTime.now());
            }
        }
        return result;
    }

    public void processMonthlyRepayment() {
        LocalDateTime now = LocalDateTime.of(2025, 10, 5, 21, 20); // Current time
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        List<Credit> credits = creditRepository.findAll();
        for (Credit credit : credits) {
            if ("APPROVED".equals(credit.getStatus()) && credit.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal revenue = getMonthlyRevenue(credit.getClientId());
                if (revenue != null && revenue.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal repaymentPercentage = new BigDecimal("0.10"); // 10% of revenue
                    BigDecimal repaymentAmount = revenue.multiply(repaymentPercentage).min(credit.getAmount());
                    if (repayCredit(credit.getId(), repaymentAmount)) {
                        System.out.println("Repayment of " + repaymentAmount + " processed for credit ID: " + credit.getId() +
                                " on " + now);
                    }
                } else {
                    System.out.println("No revenue for client ID: " + credit.getClientId() + " in October 2025.");
                }
            }
        }
    }
}