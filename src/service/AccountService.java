package service;


import model.Account;
import model.type.AccountType;
import repository.impl.AccountRepositoryImp;
import repository.repositoryInterface.AccountRepository;

import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountService {
    private static AccountService instance ;
    private final AccountRepository accountRepository;
    private final String currentRole;

    public AccountService(String currentRole) {
        this.accountRepository = new AccountRepositoryImp();
        this.currentRole = currentRole;
    }


    public static AccountService getInstance(String currentRole){
        if (instance == null){
            instance = new AccountService(currentRole);
        }
        return  instance;
    }

    public boolean createAccount(UUID clientId, AccountType type, BigDecimal initialBalance, String currency) {
        if (!isAuthorized()) {
            System.out.println("Access denied: Only TELLER and ADMIN can create an account.");
            return false;
        }
        if ( initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Invalid initial balance.");
            return false;
        }
        if (type == AccountType.CURRENT && initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Initial balance must be >= 0 for a current account.");
            return false;
        }
        if (type == AccountType.SAVINGS && initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Initial balance must be >= 0 for a savings account.");
            return false;
        }

        Account account = new Account(null, type, initialBalance, currency, clientId);
        return accountRepository.createAccount(account);
    }

    public List<Account> listAccountsForClient(UUID clientId) {
        return accountRepository.listAccountsForClient(clientId);
    }

    public Optional<Account> findByAccountId(String accountId) {
        return accountRepository.findAccountById(accountId);
    }

    public boolean updateAccount(Account account) {
        if (!isAuthorized()) {
            System.out.println("Access denied: Only TELLER and ADMIN can update an account.");
            return false;
        }
        return accountRepository.updateAccount(account);
    }

    public boolean hasAccount(UUID clientId) {
        return accountRepository.hasAccount(clientId);
    }
    private boolean isAuthorized() {
        return "TELLER".equals(currentRole) || "ADMIN".equals(currentRole);
    }
}
