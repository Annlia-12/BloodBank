package blood;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class NotificationsScreen extends JPanel {
    private int userId;
    private JTable table;
    private DefaultTableModel model;

    public NotificationsScreen(int userId) {
        this.userId = userId;
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(10,10));
        add(UIUtils.createHeader("Notifications", 58), BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Notif ID","Donor ID","Request ID","Message","Status"},0) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setBackground(Color.WHITE);
        JButton refresh = UIUtils.createPrimaryButton("Refresh");
        JButton accept = UIUtils.createPrimaryButton("Accept");
        JButton decline = UIUtils.createSecondaryButton("Decline");
        buttons.add(refresh); buttons.add(accept); buttons.add(decline);
        add(buttons, BorderLayout.SOUTH);

        refresh.addActionListener(e -> loadNotifications());
        accept.addActionListener(e -> updateSelected("accepted"));
        decline.addActionListener(e -> updateSelected("declined"));
        loadNotifications();
    }

    private void loadNotifications() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String q = "SELECT n.id, n.donor_id, n.request_id, n.message, n.status FROM notifications n " +
                       "JOIN donors d ON n.donor_id = d.id WHERE d.user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(q)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getInt("id"),
                                rs.getInt("donor_id"),
                                rs.getInt("request_id"),
                                rs.getString("message"),
                                rs.getString("status")
                        });
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    private void updateSelected(String newStatus) {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select a notification."); return; }
        int notifId = (int) model.getValueAt(r,0);
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE notifications SET status=? WHERE id=?")) {
                    ps.setString(1, newStatus);
                    ps.setInt(2, notifId);
                    ps.executeUpdate();
                }
                if ("accepted".equals(newStatus)) {
                    try (PreparedStatement ps = conn.prepareStatement("SELECT request_id FROM notifications WHERE id=?")) {
                        ps.setInt(1, notifId);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                int requestId = rs.getInt(1);
                                try (PreparedStatement ps2 = conn.prepareStatement("UPDATE blood_request SET status='matched' WHERE id=?")) {
                                    ps2.setInt(1, requestId);
                                    ps2.executeUpdate();
                                }
                            }
                        }
                    }
                }
                conn.commit();
                JOptionPane.showMessageDialog(this, "Notification updated to " + newStatus);
                loadNotifications();
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
            } finally { conn.setAutoCommit(true); }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }
}
