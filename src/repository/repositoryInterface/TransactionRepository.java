package repository.repositoryInterface;

import model.Transaction;
import model.type.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    boolean save(Transaction transaction);
    Optional<Transaction> findById(UUID id);
    List<Transaction> findAll();
    List<Transaction> findByAccountId(String accountId);
    List<Transaction> findByUserId(UUID userId);
    List<Transaction> findByType(TransactionType type);
    List<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end);
    List<Transaction> findByAccountAndDateRange(String accountId, LocalDateTime start, LocalDateTime end);
    BigDecimal getTotalBalance();
}
