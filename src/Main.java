import model.User;
import model.type.AccountType;
import repository.repositoryInterface.ClientRepository;
import service.AuthService;
import service.ClientService;
import service.UserService;
import model.Account;
import service.AccountService;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static AuthService authService = new AuthService();
    private static UserService userService = new UserService();
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
            case 6 :
                if (currentUser != null && ("TELLER".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole()))) {
                    System.out.println("Enter client ID: ");
                    UUID clientId = UUID.fromString(scanner.nextLine());
                    System.out.println("Enter account type (CURRENT/SAVINGS/CREDIT): ");
                    String type = scanner.nextLine().toUpperCase();
                    System.out.println("Enter initial balance: ");
                    BigDecimal balance = new BigDecimal(scanner.nextLine());
                    System.out.println("Enter currency (MAD/EUR/USD): ");
                    String currency = scanner.nextLine();

                    if (accountService.createAccount(clientId, AccountType.valueOf(type), balance, currency)) {
                        System.out.println("Account created successfully for client ID: " + clientId);
                    } else {
                        System.out.println("Failed to create account.");
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

                    if (clientService.createClient(fullName, email, phone)) {
                        System.out.println("Client created successfully!");
                    } else {
                        System.out.println("Failed to create client.");
                    }
                } else {
                    System.out.println("Access denied: Only TELLER or ADMIN can create clients!");
                }
                break;
            default:
                break;

        }


    }




}