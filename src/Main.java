import model.Client;
import model.User;
import model.type.AccountType;
import repository.repositoryInterface.ClientRepository;
import service.*;
import model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

public class Main {
    private static AuthService authService = new AuthService();
    private static UserService userService = new UserService();
    private static TransactionService transactionService = new TransactionService();

    private static AccountService accountService = null;
    private static ClientService clientService = new ClientService();
    private static User currentUser = null ;

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            processChoice(choice);
        }



    }

    public static void displayMenu(){
   if(currentUser == null ){
       System.out.println("1. Login ");
       System.out.println("5. Exit ");
       }else {
       System.out.println("Connected as : " + currentUser.getFullName() + " << " + currentUser.getRole() +" >>" );
       if("ADMIN".equals(currentUser.getRole())){
           System.out.println("2. Create a user ");
           System.out.println("3. List User");
       }
       if ("TELLER".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole())) {
           System.out.println("6. Create Account for Client");
           System.out.println("7. Create Client");
           System.out.println("8. Deposit ");
           System.out.println("9. Withdraw ");
           System.out.println("10. transfer ");
       }
       System.out.println("4. Logout");
       System.out.println("5. Exit");
   }
    }

    private static int getUserChoice() {
        System.out.print("Select an option : ");
         try {
             String input = scanner.nextLine().trim();
             return Integer.parseInt(input);
         }catch (NumberFormatException e){
             System.out.println("please select an number !");
             return 0;
         }

    }

    private static void processChoice(int choice )
    {
        switch (choice) {
            case 1 :
                if(currentUser == null){
                    System.out.println("Enter email : ");
                    String email = scanner.nextLine();

                    System.out.println("Enter password : ");
                    String  password = scanner.nextLine();

                    currentUser = authService.authenticate(email, password);
                    if(currentUser != null){
                        accountService = AccountService.getInstance(currentUser.getRole());
                        System.out.println("Connected successful ! ");
                    } else {
                        System.out.println("Connection failed. Please check your credentials.");
                    }

                } else {
                    System.out.println("You are already logged in.");
                }
                break;


            case 2 :
                if (currentUser != null && "ADMIN".equals(currentUser.getRole())){
                    System.out.println("Enter fullname :");
                    String fullname = scanner.nextLine();
                    System.out.println("Enter email :");
                    String email = scanner.nextLine();
                    System.out.println("Enter password :");
                    String password = scanner.nextLine();
                    System.out.println("Enter role (TELLER/MANAGER/AUDITOR) :");
                    String role = scanner.nextLine().toUpperCase();
                    User newUser = new User( fullname, email, password, role);
                    if (userService.createUser(newUser, currentUser.getEmail())) {
                        System.out.println("User created successful !");
                    } else {
                        System.out.println("Failed to create user");
                    }
                } else {
                    System.out.println("just an admin can create users !");
                }
                break;
            case 3 :
                 if(currentUser != null && "ADMIN".equals(currentUser.getRole())) {
                         userService.listeUsers(currentUser.getEmail());
                     }else{
                         System.out.println("Access denied: You are not an administrator.");
                     }

                break;
            case 4 :
                if (currentUser != null){

                    System.out.println("Logout of "+ currentUser.getFullName());
                    currentUser = null ;
                }else {
                    System.out.println("You are not connected!");
                }

                break;
            case 5 :
                System.out.println("By !");
                scanner.close();
                System.exit(0);
                break ;
            case 6:
                if (currentUser != null && ("TELLER".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole()))) {
                    System.out.println("Does the client already have a client account? (yes/no): ");
                    String response = scanner.nextLine().toLowerCase();

                    if ("yes".equals(response)) {
                        List<Client> clientsWithAccounts = clientService.listClientsWithAccounts();
                        if (clientsWithAccounts.isEmpty()) {
                            System.out.println("No clients with accounts found.");
                            break;
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
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a number.");
                            break;
                        }

                        UUID selectedClientId = clientsWithAccounts.get(clientChoice).getId();
                        Client selectedClient = clientsWithAccounts.get(clientChoice);

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
                            break;
                        }

                        System.out.println("Available account types to create: " + availableTypes);
                        System.out.println("Enter account type (from available types): ");
                        String typeInput = scanner.nextLine().toUpperCase();
                        AccountType selectedType = AccountType.valueOf(typeInput);
                        if (!availableTypes.contains(selectedType)) {
                            System.out.println("Invalid account type. Please select from available types.");
                            break;
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
                            BigDecimal balance = new BigDecimal(scanner.nextLine());
                            System.out.println("Enter currency (MAD/EUR/USD): ");
                            String currency = scanner.nextLine();

                            if (accountService.createAccount(newClient.getId(), AccountType.valueOf(type), balance, currency)) {
                                System.out.println("Account created successfully for client ID: " + newClient.getId());
                            } else {
                                System.out.println("Failed to create account.");
                            }
                        } else {
                            System.out.println("Failed to create client.");
                        }
                    } else {
                        System.out.println("Please enter 'yes' or 'no'.");
                    }
                } else {
                    System.out.println("Access denied: Only TELLER or ADMIN can create accounts!");
                }
                break;
            case 7:
                if (currentUser != null && ("TELLER".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole()))) {
                    System.out.println("Enter full name: ");
                    String fullName = scanner.nextLine();
                    System.out.println("Enter email: ");
                    String email = scanner.nextLine();
                    System.out.println("Enter phone: ");
                    String phone = scanner.nextLine();
                    Client newClient = new Client(fullName, email, phone);
                    if (clientService.createClient(newClient)) {
                        System.out.println("Client created successfully with ID : " + newClient.getId() + "!");
                    } else {
                        System.out.println("Failed to create client.");
                    }
                } else {
                    System.out.println("Access denied: Only TELLER or ADMIN can create clients!");
                }
                break;
            case 8 :
                if (currentUser != null && ("TELLER".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole()))) {
                    System.out.println("Enter account ID: ");
                    String accountId = scanner.nextLine();

                    System.out.println("Enter deposit amount: ");
                    BigDecimal depositAmount = new BigDecimal(scanner.nextLine());
                    boolean result = transactionService.deposit(accountId, currentUser.getId(), depositAmount);
                    if (result) {
                        System.out.println(" Deposit successful!");
                    } else {
                        System.out.println(" Deposit failed!");
                    }
                } else {
                    System.out.println("Access denied!");
                }
                break;
            case 9 :
                if (currentUser != null && ("TELLER".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole()))) {
                    System.out.println("Enter account ID: ");
                    String accountId = scanner.nextLine();

                    System.out.println("Enter withdrawal amount: ");
                    BigDecimal withdrawAmount;
                    try {
                        withdrawAmount = new BigDecimal(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println(" Invalid amount format! "+ e.getMessage());
                        break;
                    }

                    boolean result = transactionService.withdraw(accountId, currentUser.getId(), withdrawAmount);
                    if (result) {
                        System.out.println("Withdrawal successful!");
                    } else {
                        System.out.println("Withdrawal failed!");
                    }
                } else {
                    System.out.println("Access denied!");
                }
                break;

            case 10 :
                if (currentUser != null && ("TELLER".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole()))) {
                    System.out.println("Enter FROM account ID: ");
                    String fromAccountId = scanner.nextLine();

                    System.out.println("Enter TO account ID: ");
                    String toAccountId = scanner.nextLine();

                    System.out.println("Enter transfer amount: ");
                    BigDecimal transferAmount;
                    try {
                        transferAmount = new BigDecimal(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid amount format! " + e.getMessage());
                        break;
                    }

                    boolean result = transactionService.transfer(fromAccountId, toAccountId, transferAmount, currentUser.getId());
                    if (result) {
                        System.out.println(" Transfer completed successfully!");
                    } else {
                        System.out.println("Please check rules and balance.");
                    }
                } else {
                    System.out.println("Access denied: Only TELLER or ADMIN can perform transfers!");
                }
                break;
            default:
                break;

        }


    }




}