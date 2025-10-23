package lood;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;

/**
 * Blood Bank Management System (Swing + MySQL)
 * Features: Login • Sign Up • Profile Update • Donors • Stock • Requests
 */
public class Bloodbankmanagement implements ActionListener {

    JFrame loginFrame;
    JTextField userField;
    JPasswordField passField;
    JLabel msgLabel;
    JButton loginButton, resetButton, signupButton;
    Connection con;

    // ========================= MAIN LOGIN SCREEN =========================
    Bloodbankmanagement() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/bloodbank", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database not connected: " + e.getMessage());
        }

        loginFrame = new JFrame("Blood Bank Management System");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(420, 320);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel();
        header.setBackground(new Color(153, 0, 0));
        JLabel title = new JLabel("Blood Bank Management System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.add(title);
        loginFrame.add(header, BorderLayout.NORTH);

        // ===== CENTER FORM =====
        JPanel centerPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        centerPanel.add(new JLabel("Username:"));
        userField = new JTextField();
        centerPanel.add(userField);

        centerPanel.add(new JLabel("Password:"));
        passField = new JPasswordField();
        centerPanel.add(passField);

        msgLabel = new JLabel("", SwingConstants.CENTER);
        msgLabel.setForeground(Color.RED);
        centerPanel.add(msgLabel);
        centerPanel.add(new JLabel(""));

        loginButton = new JButton("Login");
        resetButton = new JButton("Reset");
        loginButton.setBackground(new Color(153, 0, 0));
        loginButton.setForeground(Color.WHITE);
        resetButton.setBackground(new Color(200, 200, 200));

        centerPanel.add(loginButton);
        centerPanel.add(resetButton);
        loginFrame.add(centerPanel, BorderLayout.CENTER);

        // ===== FOOTER =====
        JPanel footer = new JPanel(new FlowLayout());
        JLabel signUpText = new JLabel("Don't have an account?");
        signupButton = new JButton("Sign Up");
        signupButton.setForeground(new Color(153, 0, 0));
        footer.add(signUpText);
        footer.add(signupButton);
        loginFrame.add(footer, BorderLayout.SOUTH);

        loginButton.addActionListener(this);
        resetButton.addActionListener(this);
        signupButton.addActionListener(this);

        loginFrame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            checkLogin();
        } else if (e.getSource() == resetButton) {
            userField.setText("");
            passField.setText("");
            msgLabel.setText("");
        } else if (e.getSource() == signupButton) {
            new SignUp(con);
        }
    }

    // ========================= LOGIN CHECK =========================
    void checkLogin() {
        String u = userField.getText();
        String p = new String(passField.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            msgLabel.setText("Please enter username and password.");
            return;
        }
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM admin WHERE username=? AND password=?")) {
            ps.setString(1, u);
            ps.setString(2, p);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                loginFrame.dispose();
                new MainMenu(con, u);
            } else {
                msgLabel.setText("Invalid credentials");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    // ========================= SIGNUP UI =========================
    class SignUp extends JFrame implements ActionListener {
        JTextField emailField, userField;
        JPasswordField passField, confirmField;
        JButton registerButton, cancelButton;
        Connection con;

        SignUp(Connection con) {
            this.con = con;

            setTitle("Sign Up - Blood Bank Management System");
            setSize(420, 330);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JPanel header = new JPanel();
            header.setBackground(new Color(153, 0, 0));
            JLabel head = new JLabel("SIGN UP");
            head.setForeground(Color.WHITE);
            head.setFont(new Font("SansSerif", Font.BOLD, 18));
            header.add(head);
            add(header, BorderLayout.NORTH);

            JPanel center = new JPanel(new GridLayout(5, 2, 10, 10));
            center.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
            center.add(new JLabel("Email Name:"));
            emailField = new JTextField();
            center.add(emailField);

            center.add(new JLabel("Create Username:"));
            userField = new JTextField();
            center.add(userField);

            center.add(new JLabel("Password:"));
            passField = new JPasswordField();
            center.add(passField);

            center.add(new JLabel("Confirm Password:"));
            confirmField = new JPasswordField();
            center.add(confirmField);

            registerButton = new JButton("Sign Up");
            cancelButton = new JButton("Cancel");
            registerButton.setBackground(new Color(153, 0, 0));
            registerButton.setForeground(Color.WHITE);
            cancelButton.setBackground(new Color(200, 200, 200));
            center.add(registerButton);
            center.add(cancelButton);

            add(center, BorderLayout.CENTER);

            registerButton.addActionListener(this);
            cancelButton.addActionListener(this);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == cancelButton) {
                dispose();
                return;
            }
            String email = emailField.getText().trim();
            String username = userField.getText().trim();
            String pass = new String(passField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (email.isEmpty() || username.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }
            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!");
                return;
            }
            try (PreparedStatement ps =
                    con.prepareStatement("INSERT INTO admin(email, username, password) VALUES (?,?,?)")) {
                ps.setString(1, email);
                ps.setString(2, username);
                ps.setString(3, pass);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Account created successfully!");
                dispose();
            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Username already exists!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // ========================= MAIN MENU =========================
    class MainMenu extends JFrame implements ActionListener {
        JButton addDonor, viewStock, requestBlood, profileButton, exit;
        Connection con;
        String currentUser;

        MainMenu(Connection con, String username) {
            this.con = con;
            this.currentUser = username;
            setTitle("Blood Bank Main Menu - Welcome, " + username);
            setSize(400, 350);
            setLayout(new GridLayout(5, 1, 10, 10));
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            addDonor = new JButton("Add Donor");
            viewStock = new JButton("View Blood Stock");
            requestBlood = new JButton("Request Blood");
            profileButton = new JButton("Profile / Update Info");
            exit = new JButton("Exit");

            for (JButton b : new JButton[]{addDonor, viewStock, requestBlood, profileButton, exit}) {
                b.setBackground(new Color(220, 0, 0));
                b.setForeground(Color.WHITE);
                b.setFont(new Font("SansSerif", Font.BOLD, 14));
                add(b);
                b.addActionListener(this);
            }
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == addDonor) new AddDonor(con);
            else if (e.getSource() == viewStock) new ViewStock(con);
            else if (e.getSource() == requestBlood) new RequestBlood(con);
            else if (e.getSource() == profileButton) new ProfilePage(con, currentUser);
            else dispose();
        }
    }

    // ========================= ADD DONOR =========================
    class AddDonor extends JFrame implements ActionListener {
        JTextField nameField, bloodField, phoneField, lastField;
        JButton saveButton;
        Connection con;

        AddDonor(Connection con) {
            this.con = con;
            setTitle("Add Donor");
            setSize(400, 300);
            setLayout(new GridLayout(5, 2, 10, 10));
            setLocationRelativeTo(null);

            nameField = new JTextField();
            bloodField = new JTextField();
            phoneField = new JTextField();
            lastField = new JTextField();
            saveButton = new JButton("Save");
            saveButton.setBackground(new Color(153, 0, 0));
            saveButton.setForeground(Color.WHITE);
            saveButton.addActionListener(this);

            add(new JLabel("Name:")); add(nameField);
            add(new JLabel("Blood Group:")); add(bloodField);
            add(new JLabel("Phone:")); add(phoneField);
            add(new JLabel("Last Donation (YYYY-MM-DD):")); add(lastField);
            add(new JLabel("")); add(saveButton);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO donor(name, blood_group, phone, last_donation) VALUES (?,?,?,?)")) {
                ps.setString(1, nameField.getText());
                ps.setString(2, bloodField.getText().toUpperCase());
                ps.setString(3, phoneField.getText());
                ps.setString(4, lastField.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Donor added successfully!");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        }
    }

    // ========================= VIEW STOCK (UPDATED WITH COLOR) =========================
    class ViewStock extends JFrame {
        JTable table;
        Connection con;

        ViewStock(Connection con) {
            this.con = con;
            setTitle("View Blood Stock");
            setSize(420, 320);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // HEADER
            JPanel header = new JPanel();
            header.setBackground(new Color(153, 0, 0));
            JLabel title = new JLabel("View Blood Stock");
            title.setFont(new Font("SansSerif", Font.BOLD, 18));
            title.setForeground(Color.WHITE);
            header.add(title);
            add(header, BorderLayout.NORTH);

            // TABLE DATA
            String[] columns = {"Blood Group", "Units"};
            Object[][] data = new Object[0][2];

            try (Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                ResultSet rs = st.executeQuery("SELECT * FROM blood_stock");
                rs.last();
                int rows = rs.getRow();
                data = new Object[rows][2];
                rs.beforeFirst();
                int i = 0;
                while (rs.next()) {
                    data[i][0] = rs.getString("blood_group");
                    data[i][1] = rs.getInt("units");
                    i++;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }

            table = new JTable(data, columns);
            table.setRowHeight(25);
            table.setFont(new Font("SansSerif", Font.PLAIN, 14));
            table.setGridColor(new Color(220, 0, 0));

            JTableHeader th = table.getTableHeader();
            th.setFont(new Font("SansSerif", Font.BOLD, 14));
            th.setBackground(new Color(200, 0, 0));
            th.setForeground(Color.WHITE);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.getViewport().setBackground(Color.WHITE);

            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setBackground(new Color(255, 240, 240)); // light background
            centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
            centerPanel.add(scrollPane, BorderLayout.CENTER);
            add(centerPanel, BorderLayout.CENTER);

            // FOOTER
            JPanel footer = new JPanel();
            footer.setBackground(new Color(153, 0, 0));
            JLabel note = new JLabel("Blood Bank Management System");
            note.setForeground(Color.WHITE);
            footer.add(note);
            add(footer, BorderLayout.SOUTH);

            setVisible(true);
        }
    }

    // ========================= REQUEST BLOOD =========================
    class RequestBlood extends JFrame implements ActionListener {
        JTextField patientField, hospitalField, bloodField, unitsField;
        JButton submitButton;
        Connection con;

        RequestBlood(Connection con) {
            this.con = con;
            setTitle("Request Blood");
            setSize(400, 300);
            setLayout(new GridLayout(5, 2, 10, 10));
            setLocationRelativeTo(null);

            patientField = new JTextField();
            hospitalField = new JTextField();
            bloodField = new JTextField();
            unitsField = new JTextField();
            submitButton = new JButton("Submit");
            submitButton.setBackground(new Color(153, 0, 0));
            submitButton.setForeground(Color.WHITE);
            submitButton.addActionListener(this);

            add(new JLabel("Patient Name:")); add(patientField);
            add(new JLabel("Hospital:")); add(hospitalField);
            add(new JLabel("Blood Group:")); add(bloodField);
            add(new JLabel("Units Needed:")); add(unitsField);
            add(new JLabel("")); add(submitButton);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                String bg = bloodField.getText().toUpperCase();
                int reqUnits = Integer.parseInt(unitsField.getText());

                PreparedStatement check = con.prepareStatement("SELECT units FROM blood_stock WHERE blood_group=?");
                check.setString(1, bg);
                ResultSet rs = check.executeQuery();

                if (rs.next()) {
                    int available = rs.getInt(1);
                    String status;
                    if (available >= reqUnits) {
                        PreparedStatement update = con.prepareStatement(
                                "UPDATE blood_stock SET units=units-? WHERE blood_group=?");
                        update.setInt(1, reqUnits);
                        update.setString(2, bg);
                        update.executeUpdate();
                        status = "Approved";
                        JOptionPane.showMessageDialog(null, "Request approved. Blood issued!");
                    } else {
                        status = "Rejected - Not enough stock";
                        JOptionPane.showMessageDialog(null, "Requested units not available!");
                    }

                    PreparedStatement addReq = con.prepareStatement(
                            "INSERT INTO blood_request(patient_name, hospital, blood_group, units, status) VALUES (?,?,?,?,?)");
                    addReq.setString(1, patientField.getText());
                    addReq.setString(2, hospitalField.getText());
                    addReq.setString(3, bg);
                    addReq.setInt(4, reqUnits);
                    addReq.setString(5, status);
                    addReq.executeUpdate();

                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid blood group!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        }
    }

    // ========================= PROFILE PAGE =========================
    class ProfilePage extends JFrame implements ActionListener {
        JTextField emailField, userField;
        JPasswordField passField, confirmField;
        JButton updateButton, cancelButton;
        Connection con;
        String currentUser;

        ProfilePage(Connection con, String username) {
            this.con = con;
            this.currentUser = username;

            setTitle("My Profile - Blood Bank Management System");
            setSize(420, 320);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JPanel header = new JPanel();
            header.setBackground(new Color(153, 0, 0));
            JLabel head = new JLabel("My Profile");
            head.setForeground(Color.WHITE);
            head.setFont(new Font("SansSerif", Font.BOLD, 18));
            header.add(head);
            add(header, BorderLayout.NORTH);

            JPanel center = new JPanel(new GridLayout(5, 2, 10, 10));
            center.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
            center.add(new JLabel("Email:"));
            emailField = new JTextField();
            center.add(emailField);
            center.add(new JLabel("Username:"));
            userField = new JTextField();
            center.add(userField);
            center.add(new JLabel("New Password:"));
            passField = new JPasswordField();
            center.add(passField);
            center.add(new JLabel("Confirm Password:"));
            confirmField = new JPasswordField();
            center.add(confirmField);

            updateButton = new JButton("Update");
            cancelButton = new JButton("Cancel");
            updateButton.setBackground(new Color(153, 0, 0));
            updateButton.setForeground(Color.WHITE);
            cancelButton.setBackground(new Color(200, 200, 200));
            center.add(updateButton);
            center.add(cancelButton);

            add(center, BorderLayout.CENTER);

            updateButton.addActionListener(this);
            cancelButton.addActionListener(this);

            loadProfile();
            setVisible(true);
        }

        void loadProfile() {
            try (PreparedStatement ps = con.prepareStatement("SELECT email, username FROM admin WHERE username=?")) {
                ps.setString(1, currentUser);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    emailField.setText(rs.getString("email"));
                    userField.setText(rs.getString("username"));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading profile: " + ex.getMessage());
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == cancelButton) {
                dispose();
                return;
            }

            String email = emailField.getText().trim();
            String username = userField.getText().trim();
            String pass = new String(passField.getPassword());
            String confirm = new String(confirmField.getPassword());
          if (email.isEmpty() || username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email and username are required!");
                return;
            }
            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!");
                return;
            }

            try {
                if (pass.isEmpty()) {
                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE admin SET email=?, username=? WHERE username=?");
                    ps.setString(1, email);
                    ps.setString(2, username);
                    ps.setString(3, currentUser);
                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                dispose();
            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Username already exists!");
            }catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating profile: " + ex.getMessage());
            }
        }
    }

    // ========================= MAIN =========================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Bloodbankmanagement());
    }
}