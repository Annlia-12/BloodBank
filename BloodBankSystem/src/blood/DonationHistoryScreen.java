package blood;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DonationHistoryScreen extends JPanel {
    private int userId;
    private JTable table;
    private DefaultTableModel model;

    public DonationHistoryScreen(int userId) {
        this.userId = userId;
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(10,10));
        add(UIUtils.createHeader("Donation History", 58), BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Donor ID","Hospital","Blood Group","Units","Date"},0) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        JButton refresh = UIUtils.createPrimaryButton("Refresh");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Color.WHITE);
        bottom.add(refresh);
        add(bottom, BorderLayout.SOUTH);
        refresh.addActionListener(e -> loadHistory());
        loadHistory();
    }

    private void loadHistory() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String q = "SELECT dh.donor_id, IFNULL(h.name,'-') as hospital, dh.blood_group, dh.units, dh.donated_at FROM donation_history dh " +
                       "LEFT JOIN hospitals h ON dh.hospital_id = h.id " +
                       "WHERE dh.donor_id IN (SELECT id FROM donors WHERE user_id = ?)";
            try (PreparedStatement ps = conn.prepareStatement(q)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getInt(1),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getInt(4),
                                rs.getTimestamp(5)
                        });
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }
}
