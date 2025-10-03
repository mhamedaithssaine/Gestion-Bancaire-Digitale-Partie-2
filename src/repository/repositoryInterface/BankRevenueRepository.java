package repository.repositoryInterface;

import model.BankRevenue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankRevenueRepository {
    boolean save(BankRevenue revenue);
    Optional<BankRevenue> findById(UUID id);
    List<BankRevenue> findAll();
    List<BankRevenue> findByTransferId(UUID transferId);
}
