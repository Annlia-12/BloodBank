package bloodbank;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class NotificationsScreen extends JFrame {
    JTable table;
    Connection con;
    String currentUser;

    public NotificationsScreen(Connection con, String currentUser) {
        this.con = con;
        this.currentUser = currentUser;

        setTitle("Notifications");
        setSize(750, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Your Notifications", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"Type", "Message", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadNotifications(model);

        setVisible(true);
    }

    private void loadNotifications(DefaultTableModel model) {
        try {
            String query = """
                SELECT type, message, created_at 
                FROM notifications 
                WHERE username = ? 
                ORDER BY created_at DESC
            """;

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, currentUser);
            ResultSet rs = ps.executeQuery();

            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                model.addRow(new Object[]{
                        rs.getString("type"),
                        rs.getString("message"),
                        rs.getTimestamp("created_at")
                });
            }

            if (!hasData) {
                JOptionPane.showMessageDialog(this, "No notifications yet!", "Notifications", JOptionPane.INFORMATION_MESSAGE);
            }

            // Optional: mark all as read once viewed
            String markRead = "UPDATE notifications SET status = 'Read' WHERE username = ? AND status = 'Unread'";
            PreparedStatement psUpdate = con.prepareStatement(markRead);
            psUpdate.setString(1, currentUser);
            psUpdate.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading notifications: " + e.getMessage());
        }
    }
}
