import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;

public class OracleDbUtil {

    // Database credentials
    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:orcl"; // Change to your database URL
    private static final String USERNAME = "system"; // Change to your database username
    private static final String PASSWORD = "mani1305"; // Change to your database password

    // Establish database connection
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("Connection established successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to establish connection.");
            e.printStackTrace();
        }
        return connection;
    }

//    public static void readmg() {
//        String sql = "SELECT * FROM mg";
//
//        try (Connection connection = getConnection();
//             PreparedStatement statement = connection.prepareStatement(sql);
//             ResultSet resultSet = statement.executeQuery()) {
//
//            if (!resultSet.isBeforeFirst()) {
//                System.out.println("No data found in the table.");
//            }
//
//            while (resultSet.next()) {
//                System.out.println("Processing result set...");
//                int id = resultSet.getInt("id");
//                String name = resultSet.getString("name");
//                String department = resultSet.getString("department");
//                double salary = resultSet.getDouble("salary");
//                System.out.println("ID: " + id + ", Name: " + name + ", Department: " + department + ", Salary: " + salary);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        readmg();
//    }
}