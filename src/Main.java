import model.*;
import model.type.AccountType;
import repository.repositoryInterface.ClientRepository;
import service.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

public class Main {
    private static AuthService authService = new AuthService();
    private static UserService userService = new UserService();
    private static TransactionService transactionService = new TransactionService();
    private static CreditService creditService = CreditService.getInstance();
    private static AccountService accountService = null;
    private static ClientService clientService = new ClientService();
    private static User currentUser = null;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            processChoice(choice);
        }
    }

    public static void displayMenu() {
        if (currentUser == null) {
            System.out.println("=== Non connecté ===");
            System.out.println("1. Login ");
            System.out.println("5. Exit ");
        } else {
            try {
                System.out.println("=== Logged in as: " + currentUser.getFullName() + " <<" + currentUser.getRole() + ">> ===");
                if ("ADMIN".equals(currentUser.getRole())) {
                    System.out.println("Utilisateurs:");
                    System.out.println("1. Create a user");
                    System.out.println("2. List User");
                    System.out.println("3. Update User");
                    System.out.println("Comptes:");
                    System.out.println("4. Create Account for Client");
                    System.out.println("5. Create Client");
                    System.out.println("6. List accounts for client");
                    System.out.println("7. Update profile");
                    System.out.println("8. Change password");
                    System.out.println("9. Close account");
                    System.out.println("10. Update account status");
                    System.out.println("Transactions:");
                    System.out.println("11. Deposit");
                    System.out.println("12. Withdraw");
                    System.out.println("13. Transfer");
                    System.out.println("14. Transaction history");
                    System.out.println("Crédits:");
                    System.out.println("15. Request credit");
                    System.out.println("16. Credit follow-up");
                    System.out.println("17. Repayment");
                    System.out.println("18. Approve/Reject credit");
                    System.out.println("Compte:");
                    System.out.println("19. Logout");
                    System.out.println("20. Exit");
                } else if ("TELLER".equals(currentUser.getRole())) {
                    System.out.println("Comptes:");
                    System.out.println("1. Create Account for Client");
                    System.out.println("2. Create Client");
                    System.out.println("3. List accounts for client");
                    System.out.println("4. Update profile");
                    System.out.println("5. Change password");
                    System.out.println("6. Close account");
                    System.out.println("Transactions:");
                    System.out.println("7. Deposit");
                    System.out.println("8. Withdraw");
                    System.out.println("9. Transfer");
                    System.out.println("10. Transaction history");
                    System.out.println("Crédits:");
                    System.out.println("11. Request credit");
                    System.out.println("12. Credit follow-up");
                    System.out.println("13. Repayment");
                    System.out.println("Compte:");
                    System.out.println("14. Logout");
                    System.out.println("15. Exit");
                } else if ("MANAGER".equals(currentUser.getRole())) {
                    System.out.println("Comptes:");
                    System.out.println("1. Create Account for Client");
                    System.out.println("2. Create Client");
                    System.out.println("3. List accounts for client");
                    System.out.println("4. Update profile");
                    System.out.println("5. Change password");
                    System.out.println("6. Close account");
                    System.out.println("7. Update account status");
                    System.out.println("Transactions:");
                    System.out.println("8. Deposit");
                    System.out.println("9. Withdraw");
                    System.out.println("10. Transfer");
                    System.out.println("11. Transaction history");
                    System.out.println("Crédits:");
                    System.out.println("12. Request credit");
                    System.out.println("13. Credit follow-up");
                    System.out.println("14. Repayment");
                    System.out.println("15. Approve/Reject credit");
                    System.out.println("Compte:");
                    System.out.println("16. Logout");
                    System.out.println("17. Exit");
                }
            } catch (NullPointerException e) {
                System.out.println("Error: User session is invalid. Please log in again.");
                currentUser = null;
            }
        }
    }

    private static int getUserChoice() {
        System.out.print("Select an option: ");
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Please select a number!");
            return 0;
        }
    }

    private static void processChoice(int choice) {
        if (currentUser == null) {
            switch (choice) {
                case 1:
                    System.out.println("Voulez-vous vraiment faire Login ? (yes/no)");
                    if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                        System.out.println("Enter email: ");
                        String email = scanner.nextLine();
                        System.out.println("Enter password: ");
                        String password = scanner.nextLine();
                        currentUser = authService.authenticate(email, password);
                        if (currentUser != null) {
                            accountService = AccountService.getInstance(currentUser.getRole());
                            System.out.println("Connected successfully!");
                        } else {
                            System.out.println("Connection failed. Please check your credentials.");
                        }
                    }
                    break;
                case 5:
                    System.out.println("Voulez-vous vraiment faire Exit ? (yes/no)");
                    if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                        System.out.println("Bye!");
                        scanner.close();
                        System.exit(0);
                    }
                    break;
                default:
                    System.out.println("Invalid option! Please select a valid number.");
                    break;
            }
        } else {
            switch (currentUser.getRole()) {
                case "ADMIN":
                    switch (choice) {
                        case 1:
                            System.out.println("Voulez-vous vraiment faire Create a user ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter fullname: ");
                                String fullname = scanner.nextLine();
                                System.out.println("Enter email: ");
                                String email = scanner.nextLine();
                                System.out.println("Enter password: ");
                                String password = scanner.nextLine();
                                System.out.println("Enter role (TELLER/MANAGER/AUDITOR): ");
                                String role = scanner.nextLine().toUpperCase();
                                User newUser = new User(fullname, email, password, role);
                                if (userService.createUser(newUser, currentUser.getEmail())) {
                                    System.out.println("User created successfully!");
                                } else {
                                    System.out.println("Failed to create user");
                                }
                            }
                            break;
                        case 2:
                            System.out.println("Voulez-vous vraiment faire List User ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                userService.listeUsers(currentUser.getEmail());
                            }
                            break;
                        case 3:
                            System.out.println("Voulez-vous vraiment faire Update User ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter user ID to update: ");
                                String userIdInput = scanner.nextLine();
                                System.out.println("Enter new full name: ");
                                String newFullName = scanner.nextLine();
                                System.out.println("Enter new email: ");
                                String newEmail = scanner.nextLine();
                                try {
                                    if (userService.updateProfile(UUID.fromString(userIdInput), newFullName, newEmail)) {
                                        System.out.println("User profile updated successfully!");
                                    } else {
                                        System.out.println("Failed to update user profile.");
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 4:
                            System.out.println("Voulez-vous vraiment faire Create Account for Client ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                handleAccountCreation(4);
                            }
                            break;
                        case 5:
                            System.out.println("Voulez-vous vraiment faire Create Client ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                handleClientCreation(5);
                            }
                            break;
                        case 6:
                            System.out.println("Voulez-vous vraiment faire List accounts for client ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter client ID: ");
                                String clientIdInput = scanner.nextLine();
                                try {
                                    List<Account> accounts = accountService.listAccountsForClient(UUID.fromString(clientIdInput));
                                    if (accounts.isEmpty()) {
                                        System.out.println("No accounts found for this client.");
                                    } else {
                                        System.out.println("Accounts for client ID " + clientIdInput + ":");
                                        accounts.forEach(account ->
                                                System.out.println("Account ID: " + account.getAccountId() + ", Type: " + account.getType() + ", Balance: " + account.getBalance() + " " + account.getCurrency())
                                        );
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 7:
                            System.out.println("Voulez-vous vraiment faire Update profile ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Updating profile for user: " + currentUser.getFullName());
                                System.out.println("Enter new full name: ");
                                String newFullNameAdmin = scanner.nextLine();
                                System.out.println("Enter new email: ");
                                String newEmailAdmin = scanner.nextLine();
                                if (userService.updateProfile(currentUser.getId(), newFullNameAdmin, newEmailAdmin)) {
                                    System.out.println("Profile updated successfully!");
                                    currentUser.setFullName(newFullNameAdmin);
                                    currentUser.setEmail(newEmailAdmin);
                                } else {
                                    System.out.println("Failed to update profile.");
                                }
                            }
                            break;
                        case 8:
                            System.out.println("Voulez-vous vraiment faire Change password ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Changing password for user: " + currentUser.getFullName());
                                System.out.println("Enter new password: ");
                                String newPassword = scanner.nextLine();
                                if (userService.changePassword(currentUser.getId(), newPassword)) {
                                    System.out.println("Password changed successfully!");
                                } else {
                                    System.out.println("Failed to change password.");
                                }
                            }
                            break;
                        case 9:
                            System.out.println("Voulez-vous vraiment faire Close account ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter account ID to close: ");
                                String accountId = scanner.nextLine();
                                if (accountService.closeAccount(accountId)) {
                                    System.out.println("Account closed successfully!");
                                } else {
                                    System.out.println("Failed to close account.");
                                }
                            }
                            break;
                        case 10:
                            System.out.println("Voulez-vous vraiment faire Update account status ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter account ID to update status: ");
                                String accountIdStatus = scanner.nextLine();
                                System.out.println("Enter new status (active/inactive): ");
                                String newStatus = scanner.nextLine().toLowerCase();
                                boolean isActive = "active".equals(newStatus);
                                if (accountService.updateAccountStatus(accountIdStatus, isActive)) {
                                    System.out.println("Account status updated to " + (isActive ? "active" : "inactive") + " successfully!");
                                } else {
                                    System.out.println("Failed to update account status.");
                                }
                            }
                            break;
                        case 11:
                            System.out.println("Voulez-vous vraiment faire Deposit ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter account ID: ");
                                String depositAccountId = scanner.nextLine();
                                System.out.println("Enter deposit amount: ");
                                try {
                                    BigDecimal depositAmount = new BigDecimal(scanner.nextLine());
                                    boolean result = transactionService.deposit(depositAccountId, currentUser.getId(), depositAmount);
                                    if (result) {
                                        System.out.println("Deposit successful!");
                                    } else {
                                        System.out.println("Deposit failed!");
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 12:
                            System.out.println("Voulez-vous vraiment faire Withdraw ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter account ID: ");
                                String withdrawAccountId = scanner.nextLine();
                                System.out.println("Enter withdrawal amount: ");
                                try {
                                    BigDecimal withdrawAmount = new BigDecimal(scanner.nextLine());
                                    boolean result = transactionService.withdraw(withdrawAccountId, currentUser.getId(), withdrawAmount);
                                    if (result) {
                                        System.out.println("Withdrawal successful!");
                                    } else {
                                        System.out.println("Withdrawal failed!");
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 13:
                            System.out.println("Voulez-vous vraiment faire Transfer ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Is this an internal or external transfer? (Enter 'internal' or 'external'): ");
                                String transferType = scanner.nextLine().trim().toLowerCase();
                                String fromAccountId = null;
                                String toAccountId = null;
                                BigDecimal transferAmount = null;
                                if ("internal".equals(transferType)) {
                                    System.out.println("Enter FROM account ID: ");
                                    fromAccountId = scanner.nextLine();
                                    System.out.println("Enter TO account ID: ");
                                    toAccountId = scanner.nextLine();
                                } else if ("external".equals(transferType)) {
                                    System.out.println("Enter FROM account ID: ");
                                    fromAccountId = scanner.nextLine();
                                    toAccountId = null;
                                } else {
                                    System.out.println("Invalid transfer type! Please enter 'internal' or 'external'.");
                                    break;
                                }
                                System.out.println("Enter transfer amount: ");
                                try {
                                    transferAmount = new BigDecimal(scanner.nextLine());
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                    break;
                                }
                                boolean result = transactionService.transfer(fromAccountId, toAccountId, transferAmount, currentUser.getId());
                                if (result) {
                                    System.out.println("Transfer completed successfully!");
                                } else {
                                    System.out.println("Please check rules and balance.");
                                }
                            }
                            break;
                        case 14:
                            System.out.println("Voulez-vous vraiment faire Transaction history ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter account ID (leave blank to view all transactions for user): ");
                                String accountIdHistory = scanner.nextLine().trim();
                                List<Transaction> transactions = transactionService.getTransactionHistory(currentUser.getId(), accountIdHistory);
                                if (transactions.isEmpty()) {
                                    System.out.println("No transactions found.");
                                } else {
                                    System.out.println("Transaction history:");
                                    transactions.forEach(tx ->
                                            System.out.println("ID: " + tx.getId() + ", Type: " + tx.getTransactionType() + ", Amount: " + tx.getAmount() + ", Date: " + tx.getCreatedAt())
                                    );
                                }
                            }
                            break;
                        case 15:
                            System.out.println("Voulez-vous vraiment faire Request credit ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter client ID: ");
                                String clientIdInputCredit = scanner.nextLine();
                                System.out.println("Enter account ID (must be CREDIT type): ");
                                String accountIdCredit = scanner.nextLine();
                                System.out.println("Enter credit amount: ");
                                try {
                                    BigDecimal creditAmount = new BigDecimal(scanner.nextLine());
                                    System.out.println("Enter currency (MAD/EUR/USD): ");
                                    String currency = scanner.nextLine();
                                    if (creditService.requestCredit(UUID.fromString(clientIdInputCredit), accountIdCredit, creditAmount, currency)) {
                                        System.out.println("Credit request submitted successfully!");
                                    } else {
                                        System.out.println("Failed to submit credit request.");
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 16:
                            System.out.println("Voulez-vous vraiment faire Credit follow-up ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter client ID: ");
                                String clientIdFollowUp = scanner.nextLine();
                                try {
                                    List<Credit> credits = creditService.creditFollowUp(UUID.fromString(clientIdFollowUp));
                                    if (credits.isEmpty()) {
                                        System.out.println("No credit requests found for this client.");
                                    } else {
                                        System.out.println("Credit requests:");
                                        credits.forEach(credit ->
                                                System.out.println("ID: " + credit.getId() + ", Amount: " + credit.getAmount() + " " + credit.getCurrency() + ", Status: " + credit.getStatus() + ", Requested At: " + credit.getRequestedAt())
                                        );
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 17:
                            System.out.println("Voulez-vous vraiment faire Repayment ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter credit ID: ");
                                String creditIdInputRepay = scanner.nextLine();
                                System.out.println("Enter repayment amount: ");
                                try {
                                    UUID creditId = UUID.fromString(creditIdInputRepay);
                                    BigDecimal repaymentAmount = new BigDecimal(scanner.nextLine());
                                    if (creditService.repayCredit(creditId, repaymentAmount)) {
                                        System.out.println("Repayment recorded successfully!");
                                    } else {
                                        System.out.println("Failed to record repayment.");
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 18:
                            System.out.println("Voulez-vous vraiment faire Approve/Reject credit ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter credit ID: ");
                                String creditIdInputApprove = scanner.nextLine();
                                System.out.println("Enter action (approve/reject): ");
                                String action = scanner.nextLine().toLowerCase();
                                try {
                                    UUID creditId = UUID.fromString(creditIdInputApprove);
                                    if (creditService.approveOrRejectCredit(creditId, action)) {
                                        System.out.println("Credit " + action + "d successfully!");
                                    } else {
                                        System.out.println("Failed to " + action + " credit.");
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 19:
                            System.out.println("Voulez-vous vraiment faire Logout ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Logging out " + currentUser.getFullName());
                                currentUser = null;
                            }
                            break;
                        case 20:
                            System.out.println("Voulez-vous vraiment faire Exit ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Bye!");
                                scanner.close();
                                System.exit(0);
                            }
                            break;
                        default:
                            System.out.println("Invalid option! Please select a valid number.");
                            break;
                    }
                    break;

                case "TELLER":
                    switch (choice) {
                        case 1:
                            System.out.println("Voulez-vous vraiment faire Create Account for Client ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                handleAccountCreation(1);
                            }
                            break;
                        case 2:
                            System.out.println("Voulez-vous vraiment faire Create Client ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                handleClientCreation(2);
                            }
                            break;
                        case 3:
                            System.out.println("Voulez-vous vraiment faire List accounts for client ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter client ID: ");
                                String clientIdInput = scanner.nextLine();
                                try {
                                    List<Account> accounts = accountService.listAccountsForClient(UUID.fromString(clientIdInput));
                                    if (accounts.isEmpty()) {
                                        System.out.println("No accounts found for this client.");
                                    } else {
                                        System.out.println("Accounts for client ID " + clientIdInput + ":");
                                        accounts.forEach(account ->
                                                System.out.println("Account ID: " + account.getAccountId() + ", Type: " + account.getType() + ", Balance: " + account.getBalance() + " " + account.getCurrency())
                                        );
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 4:
                            System.out.println("Voulez-vous vraiment faire Update profile ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Updating profile for user: " + currentUser.getFullName());
                                System.out.println("Enter new full name: ");
                                String newFullNameTeller = scanner.nextLine();
                                System.out.println("Enter new email: ");
                                String newEmailTeller = scanner.nextLine();
                                if (userService.updateProfile(currentUser.getId(), newFullNameTeller, newEmailTeller)) {
                                    System.out.println("Profile updated successfully!");
                                    currentUser.setFullName(newFullNameTeller);
                                    currentUser.setEmail(newEmailTeller);
                                } else {
                                    System.out.println("Failed to update profile.");
                                }
                            }
                            break;
                        case 5:
                            System.out.println("Voulez-vous vraiment faire Change password ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Changing password for user: " + currentUser.getFullName());
                                System.out.println("Enter new password: ");
                                String newPasswordTeller = scanner.nextLine();
                                if (userService.changePassword(currentUser.getId(), newPasswordTeller)) {
                                    System.out.println("Password changed successfully!");
                                } else {
                                    System.out.println("Failed to change password.");
                                }
                            }
                            break;
                        case 6:
                            System.out.println("Voulez-vous vraiment faire Close account ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter account ID to close: ");
                                String accountIdClose = scanner.nextLine();
                                if (accountService.closeAccount(accountIdClose)) {
                                    System.out.println("Account closed successfully!");
                                } else {
                                    System.out.println("Failed to close account.");
                                }
                            }
                            break;
                        case 7:
                            System.out.println("Voulez-vous vraiment faire Deposit ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter account ID: ");
                                String depositAccountIdTeller = scanner.nextLine();
                                System.out.println("Enter deposit amount: ");
                                try {
                                    BigDecimal depositAmount = new BigDecimal(scanner.nextLine());
                                    boolean result = transactionService.deposit(depositAccountIdTeller, currentUser.getId(), depositAmount);
                                    if (result) {
                                        System.out.println("Deposit successful!");
                                    } else {
                                        System.out.println("Deposit failed!");
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 8:
                            System.out.println("Voulez-vous vraiment faire Withdraw ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter account ID: ");
                                String withdrawAccountIdTeller = scanner.nextLine();
                                System.out.println("Enter withdrawal amount: ");
                                try {
                                    BigDecimal withdrawAmount = new BigDecimal(scanner.nextLine());
                                    boolean result = transactionService.withdraw(withdrawAccountIdTeller, currentUser.getId(), withdrawAmount);
                                    if (result) {
                                        System.out.println("Withdrawal successful!");
                                    } else {
                                        System.out.println("Withdrawal failed!");
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 9:
                            System.out.println("Voulez-vous vraiment faire Transfer ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Is this an internal or external transfer? (Enter 'internal' or 'external'): ");
                                String transferType = scanner.nextLine().trim().toLowerCase();
                                String fromAccountId = null;
                                String toAccountId = null;
                                BigDecimal transferAmount = null;
                                if ("internal".equals(transferType)) {
                                    System.out.println("Enter FROM account ID: ");
                                    fromAccountId = scanner.nextLine();
                                    System.out.println("Enter TO account ID: ");
                                    toAccountId = scanner.nextLine();
                                } else if ("external".equals(transferType)) {
                                    System.out.println("Enter FROM account ID: ");
                                    fromAccountId = scanner.nextLine();
                                    toAccountId = null;
                                } else {
                                    System.out.println("Invalid transfer type! Please enter 'internal' or 'external'.");
                                    break;
                                }
                                System.out.println("Enter transfer amount: ");
                                try {
                                    transferAmount = new BigDecimal(scanner.nextLine());
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                    break;
                                }
                                boolean result = transactionService.transfer(fromAccountId, toAccountId, transferAmount, currentUser.getId());
                                if (result) {
                                    System.out.println("Transfer completed successfully!");
                                } else {
                                    System.out.println("Please check rules and balance.");
                                }
                            }
                            break;
                        case 10:
                            System.out.println("Voulez-vous vraiment faire Transaction history ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter account ID (leave blank to view all transactions for user): ");
                                String accountIdHistoryTeller = scanner.nextLine().trim();
                                List<Transaction> transactions = transactionService.getTransactionHistory(currentUser.getId(), accountIdHistoryTeller);
                                if (transactions.isEmpty()) {
                                    System.out.println("No transactions found.");
                                } else {
                                    System.out.println("Transaction history:");
                                    transactions.forEach(tx ->
                                            System.out.println("ID: " + tx.getId() + ", Type: " + tx.getTransactionType() + ", Amount: " + tx.getAmount() + ", Date: " + tx.getCreatedAt())
                                    );
                                }
                            }
                            break;
                        case 11:
                            System.out.println("Voulez-vous vraiment faire Request credit ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter client ID: ");
                                String clientIdInputCreditTeller = scanner.nextLine();
                                System.out.println("Enter account ID (must be CREDIT type): ");
                                String accountIdCreditTeller = scanner.nextLine();
                                System.out.println("Enter credit amount: ");
                                try {
                                    BigDecimal creditAmount = new BigDecimal(scanner.nextLine());
                                    System.out.println("Enter currency (MAD/EUR/USD): ");
                                    String currency = scanner.nextLine();
                                    if (creditService.requestCredit(UUID.fromString(clientIdInputCreditTeller), accountIdCreditTeller, creditAmount, currency)) {
                                        System.out.println("Credit request submitted successfully!");
                                    } else {
                                        System.out.println("Failed to submit credit request.");
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 12:
                            System.out.println("Voulez-vous vraiment faire Credit follow-up ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter client ID: ");
                                String clientIdFollowUpTeller = scanner.nextLine();
                                try {
                                    List<Credit> credits = creditService.creditFollowUp(UUID.fromString(clientIdFollowUpTeller));
                                    if (credits.isEmpty()) {
                                        System.out.println("No credit requests found for this client.");
                                    } else {
                                        System.out.println("Credit requests:");
                                        credits.forEach(credit ->
                                                System.out.println("ID: " + credit.getId() + ", Amount: " + credit.getAmount() + " " + credit.getCurrency() + ", Status: " + credit.getStatus() + ", Requested At: " + credit.getRequestedAt())
                                        );
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 13:
                            System.out.println("Voulez-vous vraiment faire Repayment ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter credit ID: ");
                                String creditIdInputRepayTeller = scanner.nextLine();
                                System.out.println("Enter repayment amount: ");
                                try {
                                    UUID creditId = UUID.fromString(creditIdInputRepayTeller);
                                    BigDecimal repaymentAmount = new BigDecimal(scanner.nextLine());
                                    if (creditService.repayCredit(creditId, repaymentAmount)) {
                                        System.out.println("Repayment recorded successfully!");
                                    } else {
                                        System.out.println("Failed to record repayment.");
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 14:
                            System.out.println("Voulez-vous vraiment faire Logout ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Logging out " + currentUser.getFullName());
                                currentUser = null;
                            }
                            break;
                        case 15:
                            System.out.println("Voulez-vous vraiment faire Exit ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Bye!");
                                scanner.close();
                                System.exit(0);
                            }
                            break;
                        default:
                            System.out.println("Invalid option! Please select a valid number.");
                            break;
                    }
                    break;

                case "MANAGER":
                    switch (choice) {
                        case 1:
                            System.out.println("Voulez-vous vraiment faire Create Account for Client ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                handleAccountCreation(1);
                            }
                            break;
                        case 2:
                            System.out.println("Voulez-vous vraiment faire Create Client ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                handleClientCreation(2);
                            }
                            break;
                        case 3:
                            System.out.println("Voulez-vous vraiment faire List accounts for client ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter client ID: ");
                                String clientIdInputManager = scanner.nextLine();
                                try {
                                    List<Account> accounts = accountService.listAccountsForClient(UUID.fromString(clientIdInputManager));
                                    if (accounts.isEmpty()) {
                                        System.out.println("No accounts found for this client.");
                                    } else {
                                        System.out.println("Accounts for client ID " + clientIdInputManager + ":");
                                        accounts.forEach(account ->
                                                System.out.println("Account ID: " + account.getAccountId() + ", Type: " + account.getType() + ", Balance: " + account.getBalance() + " " + account.getCurrency())
                                        );
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 4:
                            System.out.println("Voulez-vous vraiment faire Update profile ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Updating profile for user: " + currentUser.getFullName());
                                System.out.println("Enter new full name: ");
                                String newFullNameManager = scanner.nextLine();
                                System.out.println("Enter new email: ");
                                String newEmailManager = scanner.nextLine();
                                if (userService.updateProfile(currentUser.getId(), newFullNameManager, newEmailManager)) {
                                    System.out.println("Profile updated successfully!");
                                    currentUser.setFullName(newFullNameManager);
                                    currentUser.setEmail(newEmailManager);
                                } else {
                                    System.out.println("Failed to update profile.");
                                }
                            }
                            break;
                        case 5:
                            System.out.println("Voulez-vous vraiment faire Change password ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Changing password for user: " + currentUser.getFullName());
                                System.out.println("Enter new password: ");
                                String newPasswordManager = scanner.nextLine();
                                if (userService.changePassword(currentUser.getId(), newPasswordManager)) {
                                    System.out.println("Password changed successfully!");
                                } else {
                                    System.out.println("Failed to change password.");
                                }
                            }
                            break;
                        case 6:
                            System.out.println("Voulez-vous vraiment faire Close account ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter account ID to close: ");
                                String accountIdCloseManager = scanner.nextLine();
                                if (accountService.closeAccount(accountIdCloseManager)) {
                                    System.out.println("Account closed successfully!");
                                } else {
                                    System.out.println("Failed to close account.");
                                }
                            }
                            break;
                        case 7:
                            System.out.println("Voulez-vous vraiment faire Update account status ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter account ID to update status: ");
                                String accountIdStatusManager = scanner.nextLine();
                                System.out.println("Enter new status (active/inactive): ");
                                String newStatusManager = scanner.nextLine().toLowerCase();
                                boolean isActive = "active".equals(newStatusManager);
                                if (accountService.updateAccountStatus(accountIdStatusManager, isActive)) {
                                    System.out.println("Account status updated to " + (isActive ? "active" : "inactive") + " successfully!");
                                } else {
                                    System.out.println("Failed to update account status.");
                                }
                            }
                            break;
                        case 8:
                            System.out.println("Voulez-vous vraiment faire Deposit ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter account ID: ");
                                String depositAccountIdManager = scanner.nextLine();
                                System.out.println("Enter deposit amount: ");
                                try {
                                    BigDecimal depositAmount = new BigDecimal(scanner.nextLine());
                                    boolean result = transactionService.deposit(depositAccountIdManager, currentUser.getId(), depositAmount);
                                    if (result) {
                                        System.out.println("Deposit successful!");
                                    } else {
                                        System.out.println("Deposit failed!");
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 9:
                            System.out.println("Voulez-vous vraiment faire Withdraw ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter account ID: ");
                                String withdrawAccountIdManager = scanner.nextLine();
                                System.out.println("Enter withdrawal amount: ");
                                try {
                                    BigDecimal withdrawAmount = new BigDecimal(scanner.nextLine());
                                    boolean result = transactionService.withdraw(withdrawAccountIdManager, currentUser.getId(), withdrawAmount);
                                    if (result) {
                                        System.out.println("Withdrawal successful!");
                                    } else {
                                        System.out.println("Withdrawal failed!");
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 10:
                            System.out.println("Voulez-vous vraiment faire Transfer ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Is this an internal or external transfer? (Enter 'internal' or 'external'): ");
                                String transferTypeManager = scanner.nextLine().trim().toLowerCase();
                                String fromAccountIdManager = null;
                                String toAccountIdManager = null;
                                BigDecimal transferAmountManager = null;
                                if ("internal".equals(transferTypeManager)) {
                                    System.out.println("Enter FROM account ID: ");
                                    fromAccountIdManager = scanner.nextLine();
                                    System.out.println("Enter TO account ID: ");
                                    toAccountIdManager = scanner.nextLine();
                                } else if ("external".equals(transferTypeManager)) {
                                    System.out.println("Enter FROM account ID: ");
                                    fromAccountIdManager = scanner.nextLine();
                                    toAccountIdManager = null;
                                } else {
                                    System.out.println("Invalid transfer type! Please enter 'internal' or 'external'.");
                                    break;
                                }
                                System.out.println("Enter transfer amount: ");
                                try {
                                    transferAmountManager = new BigDecimal(scanner.nextLine());
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                    break;
                                }
                                boolean result = transactionService.transfer(fromAccountIdManager, toAccountIdManager, transferAmountManager, currentUser.getId());
                                if (result) {
                                    System.out.println("Transfer completed successfully!");
                                } else {
                                    System.out.println("Please check rules and balance.");
                                }
                            }
                            break;
                        case 11:
                            System.out.println("Voulez-vous vraiment faire Transaction history ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter account ID (leave blank to view all transactions for user): ");
                                String accountIdHistoryManager = scanner.nextLine().trim();
                                List<Transaction> transactions = transactionService.getTransactionHistory(currentUser.getId(), accountIdHistoryManager);
                                if (transactions.isEmpty()) {
                                    System.out.println("No transactions found.");
                                } else {
                                    System.out.println("Transaction history:");
                                    transactions.forEach(tx ->
                                            System.out.println("ID: " + tx.getId() + ", Type: " + tx.getTransactionType() + ", Amount: " + tx.getAmount() + ", Date: " + tx.getCreatedAt())
                                    );
                                }
                            }
                            break;
                        case 12:
                            System.out.println("Voulez-vous vraiment faire Request credit ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter client ID: ");
                                String clientIdInputCreditManager = scanner.nextLine();
                                System.out.println("Enter account ID (must be CREDIT type): ");
                                String accountIdCreditManager = scanner.nextLine();
                                System.out.println("Enter credit amount: ");
                                try {
                                    BigDecimal creditAmount = new BigDecimal(scanner.nextLine());
                                    System.out.println("Enter currency (MAD/EUR/USD): ");
                                    String currency = scanner.nextLine();
                                    if (creditService.requestCredit(UUID.fromString(clientIdInputCreditManager), accountIdCreditManager, creditAmount, currency)) {
                                        System.out.println("Credit request submitted successfully!");
                                    } else {
                                        System.out.println("Failed to submit credit request.");
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 13:
                            System.out.println("Voulez-vous vraiment faire Credit follow-up ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter client ID: ");
                                String clientIdFollowUpManager = scanner.nextLine();
                                try {
                                    List<Credit> credits = creditService.creditFollowUp(UUID.fromString(clientIdFollowUpManager));
                                    if (credits.isEmpty()) {
                                        System.out.println("No credit requests found for this client.");
                                    } else {
                                        System.out.println("Credit requests:");
                                        credits.forEach(credit ->
                                                System.out.println("ID: " + credit.getId() + ", Amount: " + credit.getAmount() + " " + credit.getCurrency() + ", Status: " + credit.getStatus() + ", Requested At: " + credit.getRequestedAt())
                                        );
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 14:
                            System.out.println("Voulez-vous vraiment faire Repayment ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter credit ID: ");
                                String creditIdInputRepayManager = scanner.nextLine();
                                System.out.println("Enter repayment amount: ");
                                try {
                                    UUID creditId = UUID.fromString(creditIdInputRepayManager);
                                    BigDecimal repaymentAmount = new BigDecimal(scanner.nextLine());
                                    if (creditService.repayCredit(creditId, repaymentAmount)) {
                                        System.out.println("Repayment recorded successfully!");
                                    } else {
                                        System.out.println("Failed to record repayment.");
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 15:
                            System.out.println("Voulez-vous vraiment faire Approve/Reject credit ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Enter credit ID: ");
                                String creditIdInputApproveManager = scanner.nextLine();
                                System.out.println("Enter action (approve/reject): ");
                                String action = scanner.nextLine().toLowerCase();
                                try {
                                    UUID creditId = UUID.fromString(creditIdInputApproveManager);
                                    if (creditService.approveOrRejectCredit(creditId, action)) {
                                        System.out.println("Credit " + action + "d successfully!");
                                    } else {
                                        System.out.println("Failed to " + action + " credit.");
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input: " + e.getMessage());
                                }
                            }
                            break;
                        case 16:
                            System.out.println("Voulez-vous vraiment faire Logout ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Logging out " + currentUser.getFullName());
                                currentUser = null;
                            }
                            break;
                        case 17:
                            System.out.println("Voulez-vous vraiment faire Exit ? (yes/no)");
                            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                                System.out.println("Bye!");
                                scanner.close();
                                System.exit(0);
                            }
                            break;
                        default:
                            System.out.println("Invalid option! Please select a valid number.");
                            break;
                    }
                    break;

                default:
                    System.out.println("Role not recognized or not authorized!");
                    break;
            }
        }
    }

    private static void handleAccountCreation(int option) {
        if (currentUser != null && ("TELLER".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole()) || "MANAGER".equals(currentUser.getRole()))) {
            System.out.println("Does the client already have a client account? (yes/no): ");
            String response = scanner.nextLine().toLowerCase();
            if ("yes".equals(response)) {
                List<Client> clientsWithAccounts = clientService.listClientsWithAccounts();
                if (clientsWithAccounts.isEmpty()) {
                    System.out.println("No clients with accounts found.");
                    return;
                }
                System.out.println("Clients with accounts:");
                for (int i = 0; i < clientsWithAccounts.size(); i++) {
                    Client client = clientsWithAccounts.get(i);
                    System.out.println((i + 1) + ". ID: " + client.getId() + ", Name: " + client.getFullName());
                }
                System.out.println("Select client number (1-" + clientsWithAccounts.size() + "): ");
                int clientChoice;
                try {
                    clientChoice = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    if (clientChoice < 0 || clientChoice >= clientsWithAccounts.size()) {
                        System.out.println("Invalid selection.");
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid input: " + e.getMessage());
                    return;
                }
                UUID selectedClientId = clientsWithAccounts.get(clientChoice).getId();
                System.out.println("Existing accounts for client ID " + selectedClientId + ":");
                accountService.listAccountsForClient(selectedClientId).forEach(account ->
                        System.out.println("Account ID: " + account.getAccountId() + ", Type: " + account.getType())
                );
                AccountType[] allTypes = {AccountType.CURRENT, AccountType.SAVINGS, AccountType.CREDIT};
                Set<AccountType> existingTypes = accountService.listAccountsForClient(selectedClientId).stream()
                        .map(Account::getType)
                        .collect(java.util.stream.Collectors.toSet());
                List<AccountType> availableTypes = java.util.Arrays.stream(allTypes)
                        .filter(type -> !existingTypes.contains(type))
                        .collect(java.util.stream.Collectors.toList());
                if (availableTypes.isEmpty()) {
                    System.out.println("No more account types available for this client.");
                    return;
                }
                System.out.println("Available account types to create: " + availableTypes);
                System.out.println("Enter account type (from available types): ");
                String typeInput = scanner.nextLine().toUpperCase();
                try {
                    AccountType selectedType = AccountType.valueOf(typeInput);
                    if (!availableTypes.contains(selectedType)) {
                        System.out.println("Invalid account type. Please select from available types.");
                        return;
                    }
                    System.out.println("Enter initial balance: ");
                    BigDecimal balance = new BigDecimal(scanner.nextLine());
                    System.out.println("Enter currency (MAD/EUR/USD): ");
                    String currency = scanner.nextLine();
                    if (accountService.createAccount(selectedClientId, selectedType, balance, currency)) {
                        System.out.println("Account created successfully for client ID: " + selectedClientId);
                    } else {
                        System.out.println("Failed to create account.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid input: " + e.getMessage());
                }
            } else if ("no".equals(response)) {
                System.out.println("Creating a new client...");
                System.out.println("Enter full name: ");
                String fullName = scanner.nextLine();
                System.out.println("Enter email: ");
                String email = scanner.nextLine();
                System.out.println("Enter phone: ");
                String phone = scanner.nextLine();
                Client newClient = new Client(fullName, email, phone);
                if (clientService.createClient(newClient)) {
                    System.out.println("Client created successfully with ID: " + newClient.getId() + "! Now creating account...");
                    System.out.println("Enter account type (CURRENT/SAVINGS/CREDIT): ");
                    String type = scanner.nextLine().toUpperCase();
                    System.out.println("Enter initial balance: ");
                    try {
                        BigDecimal balance = new BigDecimal(scanner.nextLine());
                        System.out.println("Enter currency (MAD/EUR/USD): ");
                        String currency = scanner.nextLine();
                        if (accountService.createAccount(newClient.getId(), AccountType.valueOf(type), balance, currency)) {
                            System.out.println("Account created successfully for client ID: " + newClient.getId());
                        } else {
                            System.out.println("Failed to create account.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid input: " + e.getMessage());
                    }
                } else {
                    System.out.println("Failed to create client.");
                }
            } else {
                System.out.println("Please enter 'yes' or 'no'.");
            }
        } else {
            System.out.println("Access denied: Only TELLER, MANAGER, or ADMIN can create accounts!");
        }
    }

    private static void handleClientCreation(int option) {
        if (currentUser != null && ("TELLER".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole()) || "MANAGER".equals(currentUser.getRole()))) {
            System.out.println("Enter full name: ");
            String fullName = scanner.nextLine();
            System.out.println("Enter email: ");
            String email = scanner.nextLine();
            System.out.println("Enter phone: ");
            String phone = scanner.nextLine();
            Client newClient = new Client(fullName, email, phone);
            if (clientService.createClient(newClient)) {
                System.out.println("Client created successfully with ID: " + newClient.getId() + "!");
            } else {
                System.out.println("Failed to create client.");
            }
        } else {
            System.out.println("Access denied: Only TELLER, MANAGER, or ADMIN can create clients!");
        }
    }
}