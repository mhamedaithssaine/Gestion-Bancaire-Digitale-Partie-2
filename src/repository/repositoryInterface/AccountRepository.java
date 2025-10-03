package repository.repositoryInterface;

import model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    boolean createAccount(Account account);
    Optional<Account> findAccountById(String accountId);
    List<Account> listAccountsForClient(UUID clientId);
    boolean hasAccount(UUID clientId);
    boolean updateAccount(Account account);

}
