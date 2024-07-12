import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Customer {
    private static Connection connection;

    public Customer(Connection connection) {
        this.connection = connection;
    }

    public static void displayLoanTypes() {
        String sql = "SELECT * FROM loan_type";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            System.out.println("\nLoan Types:");
            System.out.println("-----------");
            while (resultSet.next()) {
                int loanTypeId = resultSet.getInt("type_id");
                String loanTypeName = resultSet.getString("loan");
                System.out.println(loanTypeId + ". " + loanTypeName);
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve loan types.");
            e.printStackTrace();
        }
    }

    public static void insertLoanRequest(int customerId) {
        displayLoanTypes();
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter Loan Type ID: ");
            int loanTypeId = scanner.nextInt();
            scanner.nextLine(); // Consume newline after nextInt()

            System.out.print("Enter Income: ");
            int income = scanner.nextInt();
            scanner.nextLine(); // Consume newline after nextInt()

            System.out.print("Enter Tenure: ");
            int tenure = scanner.nextInt();
            scanner.nextLine(); // Consume newline after nextInt()

            System.out.print("Enter Amount: ");
            int amount = scanner.nextInt();
            scanner.nextLine(); // Consume newline after nextInt()

            // Prepare SQL statement for insertion
            String sql = "INSERT INTO loan_requests (customer_id, income, tenure, loan_type_id, amount, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerId);
                statement.setInt(2, income);
                statement.setInt(3, tenure);
                statement.setInt(4, loanTypeId);
                statement.setInt(5, amount);
                statement.setString(6, "PENDING"); // Default status for loan request

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Loan request inserted successfully");
                } else {
                    System.out.println("Failed to insert loan request.");
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (SQLException e) {
            System.out.println("Failed to insert loan request.");
            e.printStackTrace();
        }
    }

    public static int getCustomerIdByEmail(String email) {
        int customerId = -1; // Default to -1 if customer not found

        String sql = "SELECT customer_id FROM customers WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                customerId = resultSet.getInt("customer_id");
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve customer ID.");
            e.printStackTrace();
        }
        return customerId;
    }

    public static void insertCreditCardRequest(int customerId) {
        try {
            if (customerId != -1) { // Customer found
                // Prepare SQL statement for insertion
                String ccCheckQr = "select * from credit_cards where customer_id=?";
                PreparedStatement ccCheckStmnt = connection.prepareStatement(ccCheckQr);
                ccCheckStmnt.setInt(1,customerId);
                ResultSet results = ccCheckStmnt.executeQuery();
                if(results.next()){
                    System.out.println("you already have a credit card..");
                    return;
                }
                String sql = "INSERT INTO cc_requests ( customer_id, status) VALUES (?, ?)";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, customerId);
                    statement.setString(2, "PENDING"); // Initial status for credit card request

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("Credit card request inserted successfully  ");
                    } else {
                        System.out.println("Failed to insert credit card request.");
                    }
                }
            } else {
                System.out.println("Customer not found: ");
            }
        } catch (SQLException e) {
            System.out.println("Failed to insert credit card request.");
            e.printStackTrace();
        }
    }

    public static void viewCustomerDetails(int customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                System.out.println("\nCustomer Details:");
                System.out.println("----------------");
                System.out.println("Customer ID: " + resultSet.getInt("customer_id"));
                System.out.println("Name: " + resultSet.getString("first_name") + " " + resultSet.getString("last_name"));
                System.out.println("Date of Birth: " + resultSet.getString("date_of_birth"));
                System.out.println("Address: " + resultSet.getString("street_address") + ", " +
                        resultSet.getString("city") + ", " +
                        resultSet.getString("state") + " - " +
                        resultSet.getInt("zipcode"));
                System.out.println("Email: " + resultSet.getString("email"));
                System.out.println("Sex: " + resultSet.getString("sex"));
            } else {
                System.out.println("Customer not found with ID: " + customerId);
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve customer details.");
            e.printStackTrace();
        }
    }

    public static void ResetPassword(int userId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter new password: ");
        String password = scanner.nextLine();
        String updateLogin = "UPDATE login SET password=? WHERE id=?";
        try (PreparedStatement loginStatement = connection.prepareStatement(updateLogin)) {
            loginStatement.setString(1, password);
            loginStatement.setInt(2, userId);
            loginStatement.executeUpdate();
            System.out.println("Password updated successfully..");
        } catch (SQLException e) {
            System.out.println("Failed to reset the password.");
            e.printStackTrace();
        }
    }

    public static void performTransaction(int senderId) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter Receiver's Customer ID: ");
            int receiverId = scanner.nextInt();
            scanner.nextLine(); // Consume newline after nextInt()

            System.out.print("Enter Amount to Transfer: ");
            double amount = scanner.nextDouble();
            scanner.nextLine(); // Consume newline after nextDouble()

            connection.setAutoCommit(false); // Start transaction

            // Check if receiver exists and get their current balance
            String checkReceiverSql = "SELECT * FROM accounts WHERE customer_id = ?";
            try (PreparedStatement checkReceiverStmt = connection.prepareStatement(checkReceiverSql)) {
                checkReceiverStmt.setInt(1, receiverId);
                ResultSet receiverRs = checkReceiverStmt.executeQuery();
                if (receiverRs.next()) {
                    double receiverBalance = receiverRs.getDouble("account_balance");

                    // Check if sender exists and get their current balance
                    String checkSenderSql = "SELECT * FROM accounts WHERE customer_id = ?";
                    try (PreparedStatement checkSenderStmt = connection.prepareStatement(checkSenderSql)) {
                        checkSenderStmt.setInt(1, senderId);
                        ResultSet senderRs = checkSenderStmt.executeQuery();
                        if (senderRs.next()) {
                            double senderBalance = senderRs.getDouble("account_balance");

                            if (senderBalance >= amount) {
                                // Update sender's balance
                                String updateSenderSql = "UPDATE accounts SET account_balance = ? WHERE customer_id = ?";
                                try (PreparedStatement updateSenderStmt = connection.prepareStatement(updateSenderSql)) {
                                    updateSenderStmt.setDouble(1, senderBalance - amount);
                                    updateSenderStmt.setInt(2, senderId);
                                    updateSenderStmt.executeUpdate();
                                }

                                // Update receiver's balance
                                String updateReceiverSql = "UPDATE accounts SET account_balance = ? WHERE customer_id = ?";
                                try (PreparedStatement updateReceiverStmt = connection.prepareStatement(updateReceiverSql)) {
                                    updateReceiverStmt.setDouble(1, receiverBalance + amount);
                                    updateReceiverStmt.setInt(2, receiverId);
                                    updateReceiverStmt.executeUpdate();
                                }

                                // Log transaction
                                String logTransactionSql = "INSERT INTO Banking_transactions (amount, sender_id, reciever_id, transaction_date) " +
                                        "VALUES (?, ?, ?, ?)";
                                try (PreparedStatement logTransactionStmt = connection.prepareStatement(logTransactionSql)) {
                                    logTransactionStmt.setDouble(1, amount);
                                    logTransactionStmt.setInt(2, senderId);
                                    logTransactionStmt.setInt(3, receiverId);
                                    logTransactionStmt.setString(4, "12-07-2024"); // Replace with actual date
                                    logTransactionStmt.executeUpdate();
                                }

                                connection.commit(); // Commit transaction
                                System.out.println("Transaction successful. Amount transferred: " + amount);
                            } else {
                                System.out.println("Insufficient balance. Transaction failed.");
                            }
                        } else {
                            System.out.println("Sender not found. Transaction failed.");
                        }
                    }
                } else {
                    System.out.println("Receiver not found. Transaction failed.");
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback(); // Rollback transaction in case of error
                }
                System.out.println("Transaction failed.");
                e.printStackTrace();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void performCreditCardTransaction(int customerId) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter Amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine(); // Consume newline after nextDouble()

            // Retrieve credit card details for the customer
            String creditCardQuery = "SELECT cc_number, due_amount, balance_amount FROM credit_cards WHERE customer_id = ?";
            try (PreparedStatement creditCardStatement = connection.prepareStatement(creditCardQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                creditCardStatement.setInt(1, customerId);
                ResultSet resultSet = creditCardStatement.executeQuery();
                boolean yes = false;
                if (resultSet.next()) {
                    yes = false;
                    int ccNumber = resultSet.getInt("cc_number");
                    double currentDueAmount = resultSet.getDouble("due_amount");
                    double currentBalanceAmount = resultSet.getDouble("balance_amount");

                    // Update credit card due_amount and balance_amount
                    resultSet.updateDouble("due_amount", currentDueAmount + amount);
                    resultSet.updateDouble("balance_amount", currentBalanceAmount - amount);
                    resultSet.updateRow();

                    // Insert transaction into cc_transactions table
                    String transactionInsertQuery = "INSERT INTO cc_transactions ( cc_number, amount) VALUES (?, ?)";

                    try (PreparedStatement transactionStatement = connection.prepareStatement(transactionInsertQuery)) {
                        transactionStatement.setInt(1, ccNumber);
                        transactionStatement.setDouble(2, amount);

                        int rowsInserted = transactionStatement.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("Credit card transaction recorded successfully.");
                        } else {
                            System.out.println("Failed to record credit card transaction.");
                        }
                    }
                } else {
                    System.out.println("No credit card found for customer with ID: " + customerId);
                }

            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (SQLException e) {
            System.out.println("Error performing credit card transaction.");
            e.printStackTrace();
        }
    }

    public static void deposit(int custId) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter amount to be deposited: ");
            double amount = scanner.nextDouble();
            scanner.nextLine(); // Consume newline after nextDouble()

            // Retrieve current balance
            double balance = 0;
            String balqr = "SELECT * FROM accounts WHERE customer_id=?";
            try (PreparedStatement balstmnt = connection.prepareStatement(balqr)) {
                balstmnt.setInt(1, custId);
                ResultSet results = balstmnt.executeQuery();
                while (results.next()) {
                    balance = results.getDouble("account_balance");
                }
            }

            // Update balance
            String qr = "UPDATE accounts SET account_balance=? WHERE customer_id=?";
            try (PreparedStatement stmnt = connection.prepareStatement(qr)) {
                stmnt.setDouble(1, balance + amount);
                stmnt.setInt(2, custId);
                stmnt.executeUpdate();
                System.out.println("Amount deposited: " + amount);
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (SQLException e) {
            System.out.println("Failed to deposit amount.");
            e.printStackTrace();
        }
    }

    public static void withdrawl(int custId) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter amount to be withdrawn: ");
            double amount = scanner.nextDouble();
            scanner.nextLine(); // Consume newline after nextDouble()

            // Retrieve current balance
            double balance = 0;
            String balqr = "SELECT * FROM accounts WHERE customer_id=?";
            try (PreparedStatement balstmnt = connection.prepareStatement(balqr)) {
                balstmnt.setInt(1, custId);
                ResultSet results = balstmnt.executeQuery();
                while (results.next()) {
                    balance = results.getDouble("account_balance");
                }
            }

            // Check if sufficient balance
            if (balance >= amount) {
                // Update balance
                String qr = "UPDATE accounts SET account_balance=? WHERE customer_id=?";
                try (PreparedStatement stmnt = connection.prepareStatement(qr)) {
                    stmnt.setDouble(1, balance - amount);
                    stmnt.setInt(2, custId);
                    stmnt.executeUpdate();
                    System.out.println("Amount withdrawal successful.");
                }
            } else {
                System.out.println("Insufficient balance.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (SQLException e) {
            System.out.println("Failed to withdraw amount.");
            e.printStackTrace();
        }
    }
}
