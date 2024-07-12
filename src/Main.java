
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.PreparedStatement;

public class Main {
    private static Connection connection;

    public static void main(String[] args) {
        connection = OracleDbUtil.getConnection();
        if (connection == null) {
            System.out.println("Failed to connect to the database.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            int option = 0;
            boolean validOption = false;

            while (!validOption) {
                try {
                    System.out.println("1. Login as Admin");
                    System.out.println("2. Login as Employee");
                    System.out.println("3. Login as Customer");
//                    System.out.println("4 . Reset Password ");
                    System.out.println("4. Exit");

                    System.out.print("Select an option: ");
                    option = scanner.nextInt();

                    if (option < 1 || option > 4) {
                        throw new InputMismatchException();
                    }

                    validOption = true; // valid option selected
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter an integer between 1 and 4.");
                    scanner.nextLine(); // clear invalid input
                }
            }

            if (option == 4) {
                System.out.println("Exiting...");
                break;
            }

            System.out.print("User ID: ");
            int userId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Password: ");
            String password = scanner.nextLine();

            if (authenticateUser(userId, password, getRole(option))) {
                switch (option) {
                    case 1:
                        adminOperations(scanner, userId);
                        break;
                    case 2:
                        employeeOperations(userId, scanner);
                        break;
                    case 3:
                        customerOperations(userId, scanner);
                        break;
                    default:
                        System.out.println("Invalid option selected.");
                        break;
                }
            } else {
                System.out.println("Invalid user ID or password.");
            }
        }

        scanner.close();
        closeConnection();
    }

    private static boolean authenticateUser(int userId, String password, String role) {
        String query = "SELECT * FROM login WHERE id = ? AND password = ? AND admin_access = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, role);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String getRole(int option) {
        switch (option) {
            case 1:
                return "yes";
            case 2:
            case 3:
                return "NO";
            default:
                return "";
        }
    }

    private static void adminOperations(Scanner scanner, int userId) {
        Admin admin = new Admin(connection);
        while (true) {
            int option = 0;
            boolean validOption = false;

            while (!validOption) {
                try {
                    System.out.println("Admin operations:");
                    System.out.println("1. Add Employee");
                    System.out.println("2. Add Branches");
                    System.out.println("3. Add Loan Type");
                    System.out.println("4. Add Department");
                    System.out.println("5. Add Account Type");
                    System.out.println("6. Reset Password");

                    System.out.println("7. Logout");

                    System.out.print("Select an option: ");
                    option = scanner.nextInt();

                    if (option < 1 || option > 7) {
                        throw new InputMismatchException();
                    }

                    validOption = true; // valid option selected
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter an integer between 1 and 6.");
                    scanner.nextLine(); // clear invalid input
                }
            }

            if (option == 7) {
                break;
            }

            switch (option) {
                case 1:
                    admin.addEmployee(scanner);
                    break;
                case 2:
                    admin.addBranches(scanner);
                    break;
                case 3:
                    admin.addLoanType(scanner);
                    break;
                case 4:
                    admin.addDepartment(scanner);
                    break;
                case 5:
                    admin.addAccountType(scanner);
                    break;
                case 6:
                    admin.ResetPassword(userId);
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    private static void employeeOperations(int userID, Scanner scanner) {
        Employee employee = new Employee(connection);
        while (true) {
            int option = 0;
            boolean validOption = false;

            while (!validOption) {
                try {
                    System.out.println("Employee operations:");
                    System.out.println("1. Reset password");
                    System.out.println("2. Add Customer");
                    System.out.println("3. Credit Card Approval");
                    System.out.println("4. View Employee Profile");
                    System.out.println("5. Loan Approval");
                    System.out.println("6. Logout");

                    System.out.print("Select an option: ");
                    option = scanner.nextInt();

                    if (option < 1 || option > 6) {
                        throw new InputMismatchException();
                    }

                    validOption = true; // valid option selected
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter an integer between 1 and 6.");
                    scanner.nextLine(); // clear invalid input
                }
            }

            if (option == 6) {
                break;
            }

            switch (option) {
                case 1:
                    employee.ResetPassword(userID);
                    break;
                case 2:
                    employee.addCustomer(userID);
                    break;
                case 3:
                    employee.creditCardApprove();
                    break;
                case 4:
                    employee.viewEmployeeProfile(userID);
                    break;
                case 5:
                    employee.loanApprove();
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    private static void customerOperations(int userID, Scanner scanner) {
        Customer customer = new Customer(connection);
        while (true) {
            int option = 0;
            boolean validOption = false;

            while (!validOption) {
                try {
                    System.out.println("Customer operations:");
                    System.out.println("1. Reset Password");
                    System.out.println("2. View Details");
                    System.out.println("3. Request for Loan");
                    System.out.println("4. Request for Credit Card");
                    System.out.println("5. Perform any Transaction");
                    System.out.println("6. Perform Credit Card Transaction");
                    System.out.println("7. Deposit Money into Account");
                    System.out.println("8. Withdraw Money from Account");
                    System.out.println("9. Logout");

                    System.out.print("Select an option: ");
                    option = scanner.nextInt();

                    if (option < 1 || option > 9) {
                        throw new InputMismatchException();
                    }

                    validOption = true; // valid option selected
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter an integer between 1 and 9.");
                    scanner.nextLine(); // clear invalid input
                }
            }

            if (option == 9) {
                break;
            }

            switch (option) {
                case 1:
                    customer.ResetPassword(userID);
                    break;
                case 2:
                    customer.viewCustomerDetails(userID);
                    break;
                case 3:
                    customer.insertLoanRequest(userID);
                    break;
                case 4:
                    customer.insertCreditCardRequest(userID);
                    break;
                case 5:
                    customer.performTransaction(userID);
                    break;
                case 6:
                    customer.performCreditCardTransaction(userID);
                    break;
                case 7:
                    customer.deposit(userID);
                    break;
                case 8:
                    customer.withdrawl(userID);
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    private static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}





















//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.InputMismatchException;
//import java.util.Scanner;
//import java.sql.PreparedStatement;
//
//public class Main {
//    private static Connection connection;
//
//    public static void main(String[] args) {
//        connection = OracleDbUtil.getConnection();
//        if (connection == null) {
//            System.out.println("Failed to connect to the database.");
//            return;
//        }
//
//        Scanner scanner = new Scanner(System.in);
//
//        while (true) {
////            System.out.println("1. Login as Admin");
////            System.out.println("2. Login as Employee");
////            System.out.println("3. Login as Customer");
////            System.out.println("4. Exit");
////
////            System.out.print("Select an option: ");
////
////            int option= scanner.nextInt();
////
//////            scanner.nextLine();  // Consume newline
////            try{
////                if(option>4){
////                    throw  new InputMismatchException();
////                }
////            }
////            catch(InputMismatchException e){
////                System.out.println("input mismatched.. give a integer input..");
////                continue;
////            }
//            int option = 0;
//            boolean validOption = false;
//
//            while (!validOption) {
//                try {
//                    System.out.println("1. Login as Admin");
//                    System.out.println("2. Login as Employee");
//                    System.out.println("3. Login as Customer");
//                    System.out.println("4. Exit");
//
//                    System.out.print("Select an option: ");
//                    option = scanner.nextInt();
//
//                    if (option < 1 || option > 4) {
//                        throw new InputMismatchException();
//                    }
//
//                    validOption = true; // valid option selected
//                } catch (InputMismatchException e) {
//                    System.out.println("Invalid input. Please enter an integer between 1 and 4.");
//                    scanner.nextLine(); // clear invalid input
//                }
//            }
//            if (option == 4) {
//                System.out.println("Exiting...");
//                break;
//            }
////            if(option>4){
////                System.out.println("please select valid option..");
////                continue;
////            }
//            System.out.print("User ID: ");
//            int userId = scanner.nextInt();
//            scanner.nextLine();
//            System.out.print("Password: ");
//            String password = scanner.nextLine();
//
//            if (authenticateUser(userId, password, getRole(option))) {
//                switch (option) {
//                    case 1:
//                        adminOperations(scanner);
//                        break;
//                    case 2:
//                        employeeOperations(userId, scanner);
//                        break;
//                    case 3:
//                        customerOperations(userId, scanner);
//                        break;
//                    default:
//                        System.out.println("Invalid option selected.");
//                        break;
//                }
//            } else {
//                System.out.println("Invalid user ID or password.");
//            }
//        }
//
//        scanner.close();
//        closeConnection();
//    }
//
//    private static boolean authenticateUser(int userId, String password, String role) {
//        String query = "SELECT * FROM login WHERE id = ? AND password = ? AND admin_access = ?";
//        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//            preparedStatement.setInt(1, userId);
//            preparedStatement.setString(2, password);
//            preparedStatement.setString(3, role);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            return resultSet.next();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    private static String getRole(int option) {
//        switch (option) {
//            case 1:
//                return "yes";
//            case 2:
//                return "NO";
//            case 3:
//                return "NO";
//            default:
//                return "";
//        }
//    }
//
//    private static void adminOperations(Scanner scanner) {
//        Admin admin = new Admin(connection);
//        while (true) {
//            System.out.println("Admin operations:");
//            System.out.println("1. Add Employee");
//            System.out.println("2. Add Branches");
//            System.out.println("3. Add Loan Type");
//            System.out.println("4. Add Department");
//            System.out.println("5. Add Account Type");
//
//            // Add other admin operations here
//            System.out.println("6. Logout");
//
//            System.out.print("Select an option: ");
//            int option = scanner.nextInt();
//            scanner.nextLine();  // Consume newline
//
//            if (option == 6) {
//                break;
//            }
//
//            switch (option) {
//                case 1:
//                    admin.addEmployee(scanner);
//                    break;
//                // Handle other admin operations here
//                case 2:
//                    admin.addBranches(scanner);
//                    break;
//
//                case 3:
//                    admin.addLoanType(scanner);
//                    break;
//
//                case 4:
//                    admin.addDepartment(scanner);
//                    break;
//
//                case 5:
//                    admin.addAccountType(scanner);
//                    break;
//
//                default:
//                    System.out.println("Invalid option.");
//                    break;
//            }
//        }
//    }
//
//    private static void employeeOperations(int userID, Scanner scanner) {
//        Employee employee = new Employee(connection);
//        while (true) {
//            System.out.println("Employee operations:");
//            System.out.println("1. Reset password");
//
//            System.out.println("2. Add Customer");
//            System.out.println("3. credit_card_approval");
//            System.out.println("4. view Employee Profile");
//            System.out.println("5. loan Approval");
//
//            // Add other employee operations here
//            System.out.println("6. Logout");
//
//            System.out.print("Select an option: ");
//            int option = scanner.nextInt();
//            scanner.nextLine();  // Consume newline
//
//            if (option == 6) {
//                break;
//            }
//
//            switch (option) {
//                case 1:
////                    System.out.print("Customer ID: ");
////                    String customerId = scanner.nextLine();
////                    System.out.print("Password: ");
////                    String password = scanner.nextLine();
//                    employee.ResetPassword(userID);
//                    break;
//
//                case 2:
////                    System.out.print("Customer ID: ");
////                    String customerId = scanner.nextLine();
////                    System.out.print("Password: ");
////                    String password = scanner.nextLine();
//                    employee.addCustomer(userID);
//                    break;
//
//                case 3:
//                    employee.credit_card_approve();
//                    break;
//
//
//                case 4:
//                    employee.viewEmployeeProfile(userID);
//                    break;
//
//                case 5:
//                    employee.loanApprove();
//                    break;
//
//                // Handle other employee operations here
//                default:
//                    System.out.println("Invalid option.");
//                    break;
//            }
//        }
//    }
//
//    private static void customerOperations(int userID, Scanner scanner) {
//        Customer customer = new Customer(connection);
//        while (true) {
//            System.out.println("Customer operations:");
//            System.out.println("1. Reset Password");
//
//            System.out.println("2. View Details");
//            System.out.println("3. Request for loan");
//            System.out.println("4. Request for credit card");
//            System.out.println("5. Perform any transaction");
//            System.out.println("6. Perform credit card transaction");
//            System.out.println("7. Deposit money into account");
//            System.out.println("8. Withdraw money from account");
//            // Add other customer operations here
//            System.out.println("9. Logout");
//
//            System.out.print("Select an option: ");
//            int option = scanner.nextInt();
//            scanner.nextLine();  // Consume newline
//
//            if (option == 9) {
//                break;
//            }
//
//            switch (option) {
//                case 1:
//                    customer.ResetPassword(userID);
//                    break;
//
//                case 2:
//                    customer.viewCustomerDetails(userID);
//                    break;
//
//                case 3:
//                    customer.insertLoanRequest(userID);
//                    break;
//
//                case 4:
//                    customer.insertCreditCardRequest(userID);
//                    break;
//
//                case 5:
//                    customer.performTransaction(userID);
//                    break;
//
//                case 6:
//                    customer.performCreditCardTransaction(userID);
//                    break;
//                // Handle other customer operations here
//                case 7:
//                    customer.deposit(userID);
//                    break;
//                case 8:
//                    customer.withdrawl(userID);
//                    break;
//                default:
//                    System.out.println("Invalid option.");
//                    break;
//            }
//        }
//    }
//
//    private static void closeConnection() {
//        if (connection != null) {
//            try {
//                connection.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
//



