package service ;

import model.Account;
import model.Transaction;
import model.type.AccountType;
import model.type.TransactionStatus;
import model.type.TransactionType;
import repository.impl.TransactionRepositoryImp;

import java.math.BigDecimal;
import java.util.List;
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

        if (fromOpt.isEmpty()) {
            System.out.println(" Source account not found !");
            return false;
        }

        Account fromAccount = fromOpt.get();
        Account toAccount = null;

        boolean sameClient = false;
        boolean isExternal = false;
        BigDecimal finalAmount = amount;
        BigDecimal fees = BigDecimal.ZERO;


        if (toOpt.isPresent()) {
            toAccount = toOpt.get();
            sameClient = fromAccount.getClientId().equals(toAccount.getClientId());
        } else {
            isExternal = true;
        }
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
            if (fromAccount.getType() == AccountType.CURRENT ) {
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

                if (isExternal) {
                    System.out.println(" External transfer detected → Fees applied: " + fees);
                } else {
                    System.out.println(" Interbank transfer detected → Fees applied: " + fees);
                }
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
        accountService.updateAccount(fromAccount);
        if (!isExternal) {
            toAccount.setBalance(toAccount.getBalance().add(finalAmount));
            accountService.updateAccount(toAccount);
        }

        Transaction outTx ;

        if (isExternal) {
            outTx = new Transaction(fromAccountId, userId, TransactionType.EXTERNAL_TRANSFER, amount);
        } else {
            outTx = new Transaction(fromAccountId, userId, TransactionType.TRANSFEROUT, amount);
        }
        try {
            transactionRepository.save(outTx);
            if (outTx.getId() == null) {
                System.out.println("Erreur : l'ID de la transaction outTx n'a pas été généré !");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la sauvegarde de outTx : " + e.getMessage());
            return false;
        }
        if (!isExternal) {
            Transaction inTx = new Transaction(toAccountId, userId, TransactionType.TRANSFERIN, finalAmount);
            transactionRepository.save(inTx);
        }
        if (fees.compareTo(BigDecimal.ZERO) > 0) {
            try {
                System.out.println("Enregistrement des frais avec transfer_id : " + outTx.getId());
                BankRevenueService.getInstance().recordRevenue(
                        "TRANSFER",
                        isExternal ? "EXTERNAL_FEES" : "INTERBANK_FEES",
                        fees,
                        fromAccount.getCurrency(),
                        outTx.getId(),
                        isExternal ? "Fees collected for external transfer" : "Bank fees collected for transfer"
                );
            } catch (Exception e) {
                System.out.println("Erreur lors de l'enregistrement des frais : " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public List<Transaction> getTransactionHistory(UUID userId, String accountId) {
        if (accountId != null && !accountId.isEmpty()) {
            return transactionRepository.findByAccountId(accountId);
        } else {
            return transactionRepository.findByUserId(userId);
        }
    }




}