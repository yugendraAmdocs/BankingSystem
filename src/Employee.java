import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Employee {
    private static Connection c;

    public Employee(Connection connection) {
        this.c = connection;
    }


    public static void ResetPassword(int userId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter new password: ");
        String password = scanner.nextLine();
        String updateLogin = "update login set password=? where id=?";
        try {
            PreparedStatement loginStmnt = c.prepareStatement(updateLogin);
            loginStmnt.setString(1, password);
            loginStmnt.setInt(2, userId);
            loginStmnt.executeUpdate();
            System.out.println("password updated succesfully..");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed to reset the password..");
        }
    }
    public static void viewEmployeeProfile(int Id) {
        String sql = "SELECT * FROM employees WHERE employee_id = ?";
        try (PreparedStatement stmnt = c.prepareStatement(sql)) {
            stmnt.setInt(1, Id);
            try (ResultSet results = stmnt.executeQuery()) {
                if (!results.next()) {
                    System.out.println("No employee found with ID: " + Id);
                } else {
                    do {
                        int employeeId = results.getInt("employee_id");
                        String firstName = results.getString("first_name");
                        String lastName = results.getString("last_name");
                        java.sql.Date dob = results.getDate("DOB");
                        String streetAddress = results.getString("street_address");
                        String city = results.getString("city");
                        String state = results.getString("state");
                        int zipcode = results.getInt("Zipcode");
                        String sex = results.getString("sex");
                        Integer branchId = (results.getObject("branch_id") != null) ? results.getInt("branch_id") : null;
                        Integer deptId = (results.getObject("dept_id") != null) ? results.getInt("dept_id") : null;

                        // Print employee details
                        System.out.println("Employee ID: " + employeeId);
                        System.out.println("First Name: " + firstName);
                        System.out.println("Last Name: " + lastName);
                        System.out.println("DOB: " + dob);
                        System.out.println("Street Address: " + streetAddress);
                        System.out.println("City: " + city);
                        System.out.println("State: " + state);
                        System.out.println("Zipcode: " + zipcode);
                        System.out.println("Sex: " + sex);
                        System.out.println("Branch ID: " + (branchId != null ? branchId : "NULL"));
                        System.out.println("Dept ID: " + (deptId != null ? deptId : "NULL"));
                        System.out.println("------------------------------------------");
                    } while (results.next());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving employee data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loanApprove() {
        String selectSQL = "SELECT request_id, customer_id, income, tenure, loan_type_id, amount FROM loan_requests WHERE status = 'PENDING'";
        String insertSQL = "INSERT INTO Loans (customer_id, interest, type_id, tenure, monthly_installment, total_payble) VALUES (?, ?, ?, ?, ?, ?)";
        String updateSQL = "UPDATE loan_requests SET status = 'approved' WHERE request_id = ?";

        try (PreparedStatement selectStmt = c.prepareStatement(selectSQL);
             ResultSet resultSet = selectStmt.executeQuery();
             PreparedStatement insertStmt = c.prepareStatement(insertSQL);
             PreparedStatement updateStmt = c.prepareStatement(updateSQL)) {

            boolean hasRequests = false;
            while (resultSet.next()) {
                hasRequests = true;
                int requestId = resultSet.getInt("request_id");
                int customerId = resultSet.getInt("customer_id");
                int income = resultSet.getInt("income");
                int tenure = resultSet.getInt("tenure");
                int loanTypeId = resultSet.getInt("loan_type_id");
                double amount = resultSet.getDouble("amount");

                if (income >= 100000) {
                    String interestSQL = "SELECT interest FROM loan_type WHERE type_id = ?";
                    try (PreparedStatement interestStmt = c.prepareStatement(interestSQL)) {
                        interestStmt.setInt(1, loanTypeId);
                        try (ResultSet interestRate = interestStmt.executeQuery()) {
                            if (interestRate.next()) {
                                double interest = interestRate.getDouble("interest");
                                double totalPayable = amount * Math.pow(1 + interest / 100, tenure);
                                double monthlyInstallment = totalPayable / (tenure * 12);

                                insertStmt.setInt(1, customerId);
                                insertStmt.setDouble(2, interest);
                                insertStmt.setInt(3, loanTypeId);
                                insertStmt.setInt(4, tenure);
                                insertStmt.setDouble(5, monthlyInstallment);
                                insertStmt.setDouble(6, totalPayable);
                                insertStmt.executeUpdate();

                                updateStmt.setInt(1, requestId);
                                updateStmt.executeUpdate();
                                System.out.println("Approved successfully for request_id " + requestId);
                            }
                        }
                    }
                }
            }
            if (!hasRequests) {
                System.out.println("No requests to approve");
            }
        } catch (SQLException e) {
            System.err.println("Error while processing loan approvals: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static java.sql.Date getExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        int randomNumber = (int) (Math.random() * ((10 - 4) + 1)) + 4;
        calendar.add(Calendar.YEAR, randomNumber);
        return new java.sql.Date(calendar.getTimeInMillis());
    }

    public static void creditCardApprove() {
        String selectSQL = "SELECT request_id, customer_id FROM cc_requests WHERE status = 'PENDING'";
        String insertSQL = "INSERT INTO credit_cards (max_limit, customer_id, due_amount, balance_amount, expiry_date) VALUES (?, ?, ?, ?, ?)";
        String updateSQL = "UPDATE cc_requests SET status = 'approved' WHERE request_id = ?";

        try (PreparedStatement selectStmt = c.prepareStatement(selectSQL);
             ResultSet resultSet = selectStmt.executeQuery();
             PreparedStatement insertStmt = c.prepareStatement(insertSQL);
             PreparedStatement updateStmt = c.prepareStatement(updateSQL)) {

            boolean hasRequests = false;
            while (resultSet.next()) {
                hasRequests = true;
                int requestId = resultSet.getInt("request_id");
                int customerId = resultSet.getInt("customer_id");

                double maxLimit = 50000.0; // Example max limit
                double dueAmount = 0.0;
                double balanceAmount = 0.0;
                java.sql.Date expiryDate = getExpiryDate();

                insertStmt.setDouble(1, maxLimit);
                insertStmt.setInt(2, customerId);
                insertStmt.setDouble(3, dueAmount);
                insertStmt.setDouble(4, balanceAmount);
                insertStmt.setDate(5, expiryDate);
                insertStmt.executeUpdate();

                updateStmt.setInt(1, requestId);
                updateStmt.executeUpdate();
                System.out.println("Approved successfully for request_id " + requestId);
            }
            if (!hasRequests) {
                System.out.println("No requests to approve");
            }
        } catch (SQLException e) {
            System.err.println("Error while processing credit card approvals: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addCustomer(int empId) {
        Scanner scanner = new Scanner(System.in);
        String sql = "INSERT INTO customers (first_name, last_name, date_of_birth, street_address, city, state, Zipcode, email, sex) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        System.out.print("Customer First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Customer Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("Customer's Gender: ");
        String sex = scanner.nextLine();
        System.out.print("Customer's DOB (dd-MM-yyyy): ");
        String dobStr = scanner.nextLine();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        java.util.Date utilDate;
        java.sql.Date dob;
        try {
            utilDate = dateFormat.parse(dobStr);
            dob = new java.sql.Date(utilDate.getTime()); // Convert java.util.Date to java.sql.Date
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please enter date in dd-MM-yyyy format.");
            return; // Exit method if date parsing fails
        }

        System.out.print("Customer's street address: ");
        String streetAddress = scanner.nextLine();
        System.out.print("Customer's city: ");
        String city = scanner.nextLine();
        System.out.print("Customer's state: ");
        String state = scanner.nextLine();
        System.out.print("Customer Zipcode: ");
        int zipcode = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        boolean isValidEmail = false;
        String email = "";
        while (!isValidEmail) {
            System.out.print("Customer email: ");
            email = scanner.nextLine();
            isValidEmail = EmailValidator.isValidEmail(email);
            if (!isValidEmail) {
                System.out.println("Invalid email format. Please try again.");
            }
        }

        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setDate(3, dob);
            stmt.setString(4, streetAddress);
            stmt.setString(5, city);
            stmt.setString(6, state);
            stmt.setInt(7, zipcode);
            stmt.setString(8, email);
            stmt.setString(9, sex);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while adding customer: " + e.getMessage());
            e.printStackTrace();
        }

        String selectQr= "select * from account_type";
        try{
            PreparedStatement selectStmnt= c.prepareStatement(selectQr);
            ResultSet results= selectStmnt.executeQuery();
            while(results.next()){
                System.out.println("the type id and account are: " + results.getInt("type_id")+ " "+ results.getString("account"));
            }

        }
        catch(SQLException e){
            e.printStackTrace();
        }
        String CustIdsql = "SELECT * FROM customers WHERE email = ?";

        // Initialize variables
        PreparedStatement selectStmnt = null;
        ResultSet resultSet = null;
        int customer_id=0;
        try {

            // Prepare statement for selection
            selectStmnt = c.prepareStatement(CustIdsql);
            selectStmnt.setString(1, email);

            // Execute the select statement
            resultSet = selectStmnt.executeQuery();
            resultSet.next();
            customer_id=resultSet.getInt("customer_id");
            String password= "default";
            String loginQr ="insert into login(id,password,admin_access) values(?,?,?)";
            try{
                PreparedStatement Loginstmnt = c.prepareStatement(loginQr);
                Loginstmnt.setInt(1,customer_id);
                Loginstmnt.setString(2,password);
                Loginstmnt.setString(3,"NO");
                Loginstmnt.executeUpdate();
            }
            catch(SQLException e){
                e.printStackTrace();
            }
            // Process the result
//            if (resultSet.next()) {
//                customer_id = resultSet.getInt("customer_id");
//                System.out.println("Customer ID: " + customer_id + "password: " + password);
//            } else {
//                System.out.println("No customer found with the given email address.");
//            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        int branchId=0;
        try{
            String bIdQr= "select * from employees where employee_id=?";
            PreparedStatement bIdStmnt = c.prepareStatement(bIdQr);
            bIdStmnt.setInt(1,empId);
            ResultSet emp_results= bIdStmnt.executeQuery();
            while(emp_results.next()){
                branchId=emp_results.getInt("branch_id");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        System.out.println("enter the type id of the account you want: " );
        int typeId= scanner.nextInt();
        try {
            String insertQr = " insert into accounts(account_balance, branch_id, date_opened, account_type_id, customer_id) values(?,?,?,?,?)";
            PreparedStatement insertStmnt = c.prepareStatement(insertQr);
            insertStmnt.setInt(1, 0);

            insertStmnt.setInt(2,branchId);
            Calendar calendar = Calendar.getInstance();
            java.sql.Date sqlDate = new java.sql.Date(calendar.getTimeInMillis());
            insertStmnt.setDate(3, sqlDate);
            insertStmnt.setInt(4,typeId);
            insertStmnt.setInt(5,customer_id);
            insertStmnt.executeUpdate();

        }
        catch(SQLException e){
            e.printStackTrace();

        }

    }

    private static class EmailValidator {
        private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        public static boolean isValidEmail(String email) {
            Pattern pattern = Pattern.compile(EMAIL_REGEX);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }
    }
}
