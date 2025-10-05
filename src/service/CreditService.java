package service;

import model.Credit;
import repository.impl.CreditRepositoryImp;
import repository.repositoryInterface.CreditRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CreditService {
    private static CreditService instance;
    private final CreditRepository creditRepository;

    private CreditService() {
        this.creditRepository = CreditRepositoryImp.getInstance();
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
        Credit credit = new Credit(clientId, accountId, amount, currency);
        return creditRepository.createCredit(credit);
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
        return creditRepository.approveOrRejectCredit(creditId, status);
    }
}