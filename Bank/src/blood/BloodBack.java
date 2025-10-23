package blood;

import java.sql.*;

public class BloodBank {
    private static Connection con;

    public static Connection getConnection() {
        if (con != null) return con;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bloodbank", "root", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}
