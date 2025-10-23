package blood;

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
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Center
        JPanel center = new JPanel(new GridLayout(4, 2, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        center.setBackground(Color.WHITE);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        center.add(userLabel);

        userField = new JTextField();
        center.add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        center.add(passLabel);

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
        resetButton.setBackground(new Color(220, 220, 220));

        loginButton.setFocusPainted(false);
        resetButton.setFocusPainted(false);

        center.add(loginButton);
        center.add(resetButton);

        add(center, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout());
        footer.setBackground(Color.WHITE);
        JLabel signupText = new JLabel("Don't have an account?");
        signupButton = new JButton("Sign Up");
        signupButton.setForeground(new Color(153, 0, 0));
        signupButton.setBackground(Color.WHITE);
        signupButton.setBorder(BorderFactory.createLineBorder(new Color(153, 0, 0)));
        signupButton.setFocusPainted(false);
        footer.add(signupText);
        footer.add(signupButton);
        add(footer, BorderLayout.SOUTH);

        // Add actions
        loginButton.addActionListener(this);
        resetButton.addActionListener(this);
        signupButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) checkLogin();
        else if (e.getSource() == resetButton) {
            userField.setText("");
            passField.setText("");
            msgLabel.setText("");
        } else if (e.getSource() == signupButton) {
            new SignUpScreen(con);
        }
    }

    private void checkLogin() {
        String u = userField.getText().trim();
        String p = new String(passField.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            msgLabel.setText("Enter username and password.");
            return;
        }

        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM admin WHERE username=? AND password=?");
            ps.setString(1, u);
            ps.setString(2, p);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dispose();
                new MainMenu(con, u);
            } else msgLabel.setText("Invalid credentials.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}
