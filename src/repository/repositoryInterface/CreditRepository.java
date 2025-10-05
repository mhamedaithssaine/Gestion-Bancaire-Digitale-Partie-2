package repository.repositoryInterface;

import model.Credit;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


public interface CreditRepository {
    boolean createCredit(Credit credit);
    Credit findById(UUID creditId);
    List<Credit> findByClientId(UUID clientId);
    boolean repayCredit(UUID creditId, BigDecimal amount);
    boolean approveOrRejectCredit(UUID creditId, String status);
    List<Credit> findAll();
}
