package repository.repositoryInterface;

import model.Account;

import java.util.Optional;

public interface AccountRepository {
    boolean createAccount(Account account);
    Optional<Account> findByAccountId(String accountId);
}
