 package bloodbank;

import java.sql.*;
public class BloodBank {
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // localhost, user=root, password=""
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/blood", "root", "");
        } catch (Exception e) {
            System.out.println("Database Connection Failed: " + e.getMessage());
            return null;
        }
    }
}
