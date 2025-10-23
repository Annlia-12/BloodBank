package blood;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SignupScreen extends JFrame {
    private JTextField username, fullname, phone;
    private JPasswordField password;
    private JComboBox<String> roleBox;

    public SignupScreen() {
        setTitle("Signup — Blood Bank");
        setSize(520,360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.PRIMARY_BG);
        root.add(UIUtils.createHeader("Create Account", 64), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.PRIMARY_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,12,10,12);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx=0; gbc.gridy=0; form.add(new JLabel("Username:"), gbc);
        gbc.gridx=1; username = UIUtils.createField(18); form.add(username, gbc);

        gbc.gridx=0; gbc.gridy++; form.add(new JLabel("Password:"), gbc);
        gbc.gridx=1; password = UIUtils.createPasswordField(18); form.add(password, gbc);

        gbc.gridx=0; gbc.gridy++; form.add(new JLabel("Full name:"), gbc);
        gbc.gridx=1; fullname = UIUtils.createField(18); form.add(fullname, gbc);

        gbc.gridx=0; gbc.gridy++; form.add(new JLabel("Phone:"), gbc);
        gbc.gridx=1; phone = UIUtils.createField(14); form.add(phone, gbc);

        gbc.gridx=0; gbc.gridy++; form.add(new JLabel("Role:"), gbc);
        gbc.gridx=1; roleBox = new JComboBox<>(new String[]{"donor","requester"}); roleBox.setFont(UIUtils.REGULAR_FONT); form.add(roleBox, gbc);

        gbc.gridx=0; gbc.gridy++; gbc.gridwidth=2; gbc.anchor = GridBagConstraints.CENTER;
        JPanel btns = new JPanel(); btns.setOpaque(false);
        JButton create = UIUtils.createPrimaryButton("Create");
        JButton back = UIUtils.createSecondaryButton("Back to Login");
        btns.add(create); btns.add(Box.createHorizontalStrut(8)); btns.add(back);
        form.add(btns, gbc);

        root.add(form, BorderLayout.CENTER);
        add(root);

        create.addActionListener(e -> createUser());
        back.addActionListener(e -> { dispose(); new LoginScreen().setVisible(true);});
    }

    private void createUser() {
        String u = username.getText().trim();
        String pw = new String(password.getPassword());
        String fn = fullname.getText().trim();
        String ph = phone.getText().trim();
        String role = (String) roleBox.getSelectedItem();

        if (u.isEmpty() || pw.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter username and password."); return; }

        try (Connection conn = DBConnection.getConnection()) {
            String insert = "INSERT INTO users (username,password,fullname,role) VALUES (?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, u);
                ps.setString(2, pw);
                ps.setString(3, fn);
                ps.setString(4, role);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Account created. You can login now.");
                dispose();
                new LoginScreen().setVisible(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating user: " + ex.getMessage());
        }
    }
}
