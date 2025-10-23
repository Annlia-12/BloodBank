package bloodbank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginScreen extends JFrame implements ActionListener {
    JTextField userField;
    JPasswordField passField;
    JLabel msgLabel;
    JButton loginButton, resetButton, signupButton;
    Connection con;

    public LoginScreen() {
        con = BloodBank.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Cannot connect to database. Exiting...");
            System.exit(0);
        }

        setTitle("Blood Bank Login");
        setSize(420, 320);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(153, 0, 0));
        JLabel title = new JLabel("Blood Bank System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Center
        JPanel center = new JPanel(new GridLayout(4, 2, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        center.add(new JLabel("Username:"));
        userField = new JTextField();
        center.add(userField);

        center.add(new JLabel("Password:"));
        passField = new JPasswordField();
        center.add(passField);

        msgLabel = new JLabel("", SwingConstants.CENTER);
        msgLabel.setForeground(Color.RED);
        center.add(msgLabel);
        center.add(new JLabel(""));

        loginButton = new JButton("Login");
        resetButton = new JButton("Reset");
        loginButton.setBackground(new Color(153, 0, 0));
        loginButton.setForeground(Color.WHITE);
        resetButton.setBackground(new Color(200, 200, 200));
        center.add(loginButton);
        center.add(resetButton);
        add(center, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout());
        JLabel signupText = new JLabel("Don't have an account?");
        signupButton = new JButton("Sign Up");
        signupButton.setForeground(new Color(153, 0, 0));
        footer.add(signupText);
        footer.add(signupButton);
        add(footer, BorderLayout.SOUTH);

        loginButton.addActionListener(this);
        resetButton.addActionListener(this);
        signupButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton)
            checkLogin();
        else if (e.getSource() == resetButton) {
            userField.setText("");
            passField.setText("");
            msgLabel.setText("");
        } else if (e.getSource() == signupButton)
            new SignUpScreen(con); // We'll create this page
    }

    private void checkLogin() {
        String u = userField.getText().trim();
        String p = new String(passField.getPassword());

        if (u.isEmpty() || p.isEmpty()) {
            msgLabel.setText("Enter username and password.");
            return;
        }

        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, u);
            ps.setString(2, p);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                JOptionPane.showMessageDialog(this, "Welcome " + u + "!");

                dispose();
                new MainMenu(con, u, role); // MainMenu will be created next
            } else {
                msgLabel.setText("Invalid credentials.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}
