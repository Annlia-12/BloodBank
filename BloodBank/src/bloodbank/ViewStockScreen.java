package bloodbank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewStockScreen extends JFrame {
    private JTable table;
    private Connection con;

    // Blood groups default units (if database is empty)
    private final String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private final int[] defaultUnits = {12, 8, 15, 5, 6, 3, 12, 7};

    public ViewStockScreen(Connection con) {
        this.con = con;

        setTitle("Blood Stock");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("Current Blood Stock", JLabel.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 20));
        heading.setForeground(new Color(200, 0, 0));
        add(heading, BorderLayout.NORTH);

        String[] columns = {"Blood Group", "Units Available"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadBloodStock(model);

        setVisible(true);
    }

    private void loadBloodStock(DefaultTableModel model) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM blood_stock");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                model.addRow(new Object[]{
                        rs.getString("blood_group"),
                        rs.getInt("units")
                });
            }

            // If no data exists, insert default blood stock
            if (!hasData) {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO blood_stock(blood_group, units) VALUES(?, ?)"
                );
                for (int i = 0; i < bloodGroups.length; i++) {
                    ps.setString(1, bloodGroups[i]);
                    ps.setInt(2, defaultUnits[i]);
                    ps.executeUpdate();
                    model.addRow(new Object[]{bloodGroups[i], defaultUnits[i]});
                }
            }

            rs.close();
            stmt.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading blood stock: " + ex.getMessage());
        }
    }
}
