package blood;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProfileScreen extends JPanel {
    private int userId;
    private JLabel unameLabel, roleLabel;
    private JTextArea infoArea;

    public ProfileScreen(int userId) {
        this.userId = userId;
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(10,10));
        add(UIUtils.createHeader("My Profile", 58), BorderLayout.NORTH);

        JPanel top = new JPanel(new GridLayout(2,1));
        top.setBackground(Color.WHITE);
        unameLabel = new JLabel("Username:");
        unameLabel.setFont(UIUtils.HEADER_FONT);
        roleLabel = new JLabel("Role:");
        roleLabel.setFont(UIUtils.REGULAR_FONT);
        top.add(unameLabel); top.add(roleLabel);
        add(top, BorderLayout.NORTH);

        infoArea = new JTextArea(8,40);
        infoArea.setEditable(false);
        infoArea.setFont(UIUtils.REGULAR_FONT);
        add(new JScrollPane(infoArea), BorderLayout.CENTER);

        loadProfile();
    }

    private void loadProfile() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT username,fullname,role,created_at FROM users WHERE id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    unameLabel.setText("Username: " + rs.getString("username"));
                    roleLabel.setText("Role: " + rs.getString("role"));
                    infoArea.setText("Full name: " + rs.getString("fullname") +
                            "\nCreated at: " + rs.getTimestamp("created_at"));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }
}
