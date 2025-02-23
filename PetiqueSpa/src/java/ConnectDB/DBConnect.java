package ConnectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect {

    // Phương thức để thiết lập kết nối
    public static Connection getConnection() {
        Connection connection = null;

        try {
            // Đăng ký driver JDBC cho SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Chuỗi kết nối, tên người dùng và mật khẩu
            String url = "jdbc:sqlserver://localhost:1433;databaseName=PetiqueSpa;encrypt=false";
            String user = "sa";
            String password = "MyStrongPass123";

            // Mở kết nối đến cơ sở dữ liệu
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection successful!");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }

        return connection;
    }

    // Phương thức để đóng kết nối
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Failed to close connection!");
                e.printStackTrace();
            }
        }
    }
}
