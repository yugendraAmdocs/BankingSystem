import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Admin {

    private static Connection connection;

    public Admin(Connection connection) {
        this.connection = connection;
    }

    public static void addEmployee(Scanner scanner) {
        String sql = "Insert into employees (first_name,last_name,DOB,street_address,city,state,Zipcode,sex,branch_id,dept_id) values(?,?,?,?,?,?,?,?,?,?)";
        scanner.nextLine();
        System.out.print("Employee First Name: ");
        String first_name = scanner.nextLine();
        System.out.print("Employee Last Name: ");
        String last_name = scanner.nextLine();
        System.out.print("Employee's Gender: ");
        String sex = scanner.nextLine();
        System.out.print("Employee's DOB (yyyy-MM-dd): ");
        String dobStr = scanner.nextLine();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date utilDate;
        Date DOB;
        try {
            utilDate = dateFormat.parse(dobStr);
            DOB = new Date(utilDate.getTime()); // Convert java.util.Date to java.sql.Date
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please enter date in yyyy-MM-dd format.");
            return; // Exit method if date parsing fails
        }
        System.out.print("Employee's street address: ");
        String street_address = scanner.nextLine();
        System.out.print("Employee's city: ");
        String city = scanner.nextLine();
        System.out.print("Employee's state: ");
        String state = scanner.nextLine();
        System.out.print("Employee Zipcode: ");
        int Zipcode = 0;
        try {
            Zipcode = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input for Zipcode. Please enter a valid number.");
            return; // Exit method if input mismatch occurs
        }
        System.out.print("Branch id: ");
        int branch_id = 0;
        try {
            branch_id = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input for Branch id. Please enter a valid number.");
            return; // Exit method if input mismatch occurs
        }
        System.out.print("Employee's Department id: ");
        int dept_id = 0;
        try {
            dept_id = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input for Department id. Please enter a valid number.");
            return; // Exit method if input mismatch occurs
        }

        scanner.nextLine(); // Consume newline left-over
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, first_name);
            stmt.setString(2, last_name);
            stmt.setDate(3, DOB);
            stmt.setString(4, street_address);
            stmt.setString(5, city);
            stmt.setString(6, state);
            stmt.setInt(7, Zipcode);
            stmt.setString(8, sex);
            stmt.setInt(9, branch_id);
            stmt.setInt(10, dept_id);

            stmt.executeUpdate();

            // Retrieve newly inserted employee ID
            int empId = 0;
            String empIdQr = "select * from employees where first_name=?";
            try (PreparedStatement empIdStmnt = connection.prepareStatement(empIdQr)) {
                empIdStmnt.setString(1, first_name);
                ResultSet results = empIdStmnt.executeQuery();
                while (results.next()) {
                    empId = results.getInt("employee_id");
                }
            } catch (SQLException e) {
                System.out.println("Error retrieving employee ID: " + e.getMessage());
            }

            // Insert into login table
            String password = "default";
            String loginQr = "insert into login(id,password,admin_access) values(?,?,?)";
            try (PreparedStatement Loginstmnt = connection.prepareStatement(loginQr)) {
                Loginstmnt.setInt(1, empId);
                Loginstmnt.setString(2, password);
                Loginstmnt.setString(3, "NO");
                Loginstmnt.executeUpdate();
                System.out.println("Employee added successfully with ID: " + empId + ", password: " + password);
            } catch (SQLException e) {
                System.out.println("Error adding login information: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public static void addBranches(Scanner scanner) {
        String sql = "Insert into branches (branch_name,street_address,city,Zipcode,phone_number,state) values(?,?,?,?,?,?)";
        scanner.nextLine();

        System.out.print("Enter Branch name: ");
        String branch_name = scanner.nextLine();
        System.out.print("Branch's street address: ");
        String street_address = scanner.nextLine();
        System.out.print("Branch's city: ");
        String city = scanner.nextLine();
        System.out.print("Branch's state: ");
        String state = scanner.nextLine();
        int Zipcode = 0;
        try {
            System.out.print("Branch Zipcode: ");
            Zipcode = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input for Zipcode. Please enter a valid number.");
            return; // Exit method if input mismatch occurs
        }
        scanner.nextLine(); // Consume newline left-over
        System.out.print("Branch Phone number: ");
        String phone = scanner.nextLine();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, branch_name);
            stmt.setString(2, street_address);
            stmt.setString(3, city);
            stmt.setInt(4, Zipcode);
            stmt.setString(5, phone);
            stmt.setString(6, state);

            stmt.executeUpdate();
            System.out.println("Branch successfully added!!!");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
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
    public static void addAccountType(Scanner scanner) {
        String sql = "INSERT INTO account_type (account) VALUES (?)";
        scanner.nextLine();

        System.out.print("Add new Account Type: ");
        String typeName = scanner.nextLine();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, typeName);
            stmt.executeUpdate();
            System.out.println("Account type successfully added");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public static void addLoanType(Scanner scanner) {
        String sql = "INSERT INTO loan_type (loan, interest) VALUES (?, ?)";
        scanner.nextLine();

        System.out.print("Enter new loan type: ");
        String typeName = scanner.nextLine();
        int interest = 0;
        try {
            System.out.print("Enter interest : ");
            interest = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input for Interest. Please enter a valid number.");
            return; // Exit method if input mismatch occurs
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, typeName);
            stmt.setInt(2, interest);
            stmt.executeUpdate();
            System.out.println("Loan type successfully added");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public static void addDepartment(Scanner scanner) {
        String insertDepartmentSQL = "INSERT INTO departments (dept_name) VALUES (?)";
        scanner.nextLine();

        System.out.print("Add new Department: ");
        String departmentName = scanner.nextLine();

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertDepartmentSQL)) {
            preparedStatement.setString(1, departmentName);
            preparedStatement.executeUpdate();
            System.out.println("Department successfully added");

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    // Close connection when done
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}