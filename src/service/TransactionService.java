package service ;

import model.Account;
import model.Transaction;
import model.type.AccountType;
import model.type.TransactionStatus;
import model.type.TransactionType;
import repository.impl.TransactionRepositoryImp;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class TransactionService {

    private static TransactionRepositoryImp transactionRepository = TransactionRepositoryImp.getInstance();
    private final AccountService accountService = AccountService.getInstance("TELLER");

    public boolean deposit(String accountId, UUID tellerId, BigDecimal amount) {
        try {
            Optional<Account> optionalAccount = accountService.findByAccountId(accountId);
            if (optionalAccount.isEmpty()) {
                System.out.println("Compte introuvable !");
                return false;
            }

            Account account = optionalAccount.get();

            if (account.getType() == AccountType.CREDIT) {
                System.out.println(" Impossible de déposer dans un compte Crédit !");
                return false;
            }

//            BigDecimal newBalance = account.getBalance().add(amount);
            account.setNewBalance(amount);

            boolean updated = accountService.updateAccount(account);
            if (!updated) {
                System.out.println(" Erreur lors de la mise à jour du solde !");
                return false;
            }

            Transaction transaction = new Transaction(
                    accountId,
                    tellerId,
                    TransactionType.DEPOSIT,
                    amount
            );
            transaction.setStatus(TransactionStatus.COMPLETED);

            boolean saved = transactionRepository.save(transaction);
            if (!saved) {
                System.out.println(" Erreur lors de l'enregistrement de la transaction !");
                return false;
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean withdraw(String accountId, UUID tellerId, BigDecimal amount) {
        try {
            Optional<Account> optionalAccount = accountService.findByAccountId(accountId);
            if (optionalAccount.isEmpty()) {
                System.out.println(" Account not found!");
                return false;
            }

            Account account = optionalAccount.get();

            if (account.getType() != AccountType.CURRENT) {
                System.out.println("Withdrawals are only allowed from CURRENT accounts!");
                return false;
            }

            if (account.getBalance().compareTo(amount) < 0) {
                System.out.println(" Insufficient balance for this withdrawal!");
                return false;
            }

            BigDecimal newBalance = account.getBalance().subtract(amount);
            account.setBalance(newBalance);

            boolean updated = accountService.updateAccount(account);
            if (!updated) {
                System.out.println(" Error while updating account balance!");
                return false;
            }

            Transaction transaction = new Transaction(
                    accountId,
                    tellerId,
                    TransactionType.WITHDRAW,
                    amount
            );
            transaction.setStatus(TransactionStatus.COMPLETED);

            boolean saved = transactionRepository.save(transaction);
            if (!saved) {
                System.out.println(" Error while saving transaction!");
                return false;
            }

            System.out.println(" Withdrawal completed successfully!");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean transfer(String fromAccountId, String toAccountId, BigDecimal amount, UUID userId) {
        Optional<Account> fromOpt = accountService.findByAccountId(fromAccountId);
        Optional<Account> toOpt = accountService.findByAccountId(toAccountId);

        if (fromOpt.isEmpty() || toOpt.isEmpty()) {
            System.out.println(" One of the accounts does not exist!");
            return false;
        }

        Account fromAccount = fromOpt.get();
        Account toAccount = toOpt.get();

        boolean sameClient = fromAccount.getClientId().equals(toAccount.getClientId());

        BigDecimal finalAmount = amount;

        if (sameClient) {
            if (!(
                    (fromAccount.getType() == AccountType.SAVINGS && toAccount.getType() == AccountType.CURRENT) ||
                            (fromAccount.getType() == AccountType.CURRENT && toAccount.getType() == AccountType.SAVINGS) ||
                            (fromAccount.getType() == AccountType.CURRENT && toAccount.getType() == AccountType.CREDIT)
            )) {
                System.out.println("Invalid transfer type for the same client!");
                return false;
            }
        } else {
            if (fromAccount.getType() == AccountType.CURRENT && toAccount.getType() == AccountType.CURRENT) {
                BigDecimal fees;
                if (amount.compareTo(BigDecimal.valueOf(500)) <= 0) {
                    fees = amount.multiply(BigDecimal.valueOf(0.05));
                } else if (amount.compareTo(BigDecimal.valueOf(1000)) <= 0) {
                    fees = amount.multiply(BigDecimal.valueOf(0.10));
                } else if (amount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
                    fees = amount.multiply(BigDecimal.valueOf(0.30));
                } else {
                    fees = amount.multiply(BigDecimal.valueOf(0.50));
                }
                finalAmount = amount.subtract(fees);
                BankRevenueService.getInstance().recordRevenue(
                        "TRANSFER",
                        "INTERBANK_FEES",
                        fees,
                        fromAccount.getCurrency(),
                        Transaction.,
                        "Bank fees collected for transfer"
                );
                System.out.println(" Fees applied: " + fees + " | Final transferred: " + finalAmount);
            } else {
                System.out.println(" Transfer not allowed between these account types!");
                return false;
            }
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            System.out.println(" Insufficient balance for transfer!");
            return false;
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(finalAmount));

        accountService.updateAccount(fromAccount);
        accountService.updateAccount(toAccount);

        Transaction outTx = new Transaction(fromAccountId, userId, TransactionType.TRANSFEROUT, amount);
        Transaction inTx = new Transaction(toAccountId, userId, TransactionType.TRANSFERIN, finalAmount);

        transactionRepository.save(outTx);
        transactionRepository.save(inTx);

        return true;
    }




}