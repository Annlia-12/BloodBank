package bloodbankmanagement;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class BloodBankManagementSystem extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel sidebar;
    private Color darkRed = new Color(139, 49, 52);
    private Color lightBlue = new Color(139, 172, 191);
    private Color backgroundGray = new Color(220, 227, 231);
    
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/blood_bank_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    private Connection connection;
    private int currentUserId = -1;
    private String currentUsername = "";
    private String currentUserRole = "";
    
    public BloodBankManagementSystem() {
        setTitle("Blood Bank Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize database connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Database connection failed: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        // Show login screen first
        showLoginScreen();
    }
    
    private void showLoginScreen() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        
        JPanel loginPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(200, 215, 220, 100));
                int[] xPos = {100, 450, 750};
                int[] yPos = {50, 100, 50};
                for (int i = 0; i < 3; i++) {
                    drawBloodDrop(g2d, xPos[i], yPos[i], 250);
                }
            }

			private void drawBloodDrop(Graphics2D g2d, int i, int j, int k) {
				// TODO Auto-generated method stub
				
			}
        };
        loginPanel.setBackground(backgroundGray);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Login form container
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel iconLabel = new JLabel("💧", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel titleLabel = new JLabel("Blood Bank Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(darkRed);
        JLabel subtitleLabel = new JLabel("Login to Continue", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setForeground(Color.GRAY);
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);
        formPanel.add(headerPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Username field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setMaximumSize(new Dimension(300, 35));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(userLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Password field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(300, 35));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(passLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Login button
        JButton loginButton = createButton("Login", darkRed, true);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(300, 40));
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username and password", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (authenticateUser(username, password)) {
                loadMainApplication();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", 
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(loginButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Signup link
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        signupPanel.setBackground(Color.WHITE);
        JLabel signupLabel = new JLabel("Don't have an account? ");
        signupLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel signupLink = new JLabel("Sign Up");
        signupLink.setFont(new Font("Arial", Font.BOLD, 12));
        signupLink.setForeground(darkRed);
        signupLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showSignupScreen();
            }
        });
        signupPanel.add(signupLabel);
        signupPanel.add(signupLink);
        formPanel.add(signupPanel);
        
        loginPanel.add(formPanel);
        add(loginPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    private JButton createButton(String string, Color darkRed2, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	private void showSignupScreen() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        
        JPanel signupPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(200, 215, 220, 100));
                int[] xPos = {100, 450, 750};
                int[] yPos = {50, 100, 50};
                for (int i = 0; i < 3; i++) {
                    drawBloodDrop(g2d, xPos[i], yPos[i], 250);
                }
            }

			private void drawBloodDrop(Graphics2D g2d, int i, int j, int k) {
				// TODO Auto-generated method stub
				
			}
        };
        signupPanel.setBackground(backgroundGray);
        
        // Signup form container
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel iconLabel = new JLabel("💧", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 50));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(darkRed);
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(titleLabel);
        formPanel.add(headerPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Create scroll pane for form
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(Color.WHITE);
        
        JTextField fullNameField = createSignupField(fieldsPanel, "Full Name:");
        JTextField usernameField = createSignupField(fieldsPanel, "Username:");
        JPasswordField passwordField = createSignupPasswordField(fieldsPanel, "Password:");
        JPasswordField confirmPassField = createSignupPasswordField(fieldsPanel, "Confirm Password:");
        JTextField emailField = createSignupField(fieldsPanel, "Email:");
        JTextField contactField = createSignupField(fieldsPanel, "Contact Number:");
        JTextField addressField = createSignupField(fieldsPanel, "Address:");
        
        JScrollPane scrollPane = new JScrollPane(fieldsPanel);
        scrollPane.setBorder(null);
        scrollPane.setMaximumSize(new Dimension(400, 350));
        formPanel.add(scrollPane);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        
        JButton signupButton = createButton("Sign Up", darkRed, true);
        signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupButton.setMaximumSize(new Dimension(300, 40));
        signupButton.addActionListener(e -> {
            String fullName = fullNameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());
            String email = emailField.getText().trim();
            String contact = contactField.getText().trim();
            String address = addressField.getText().trim();
            
            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || 
                email.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (registerUser(fullName, username, password, email, contact, address)) {
                JOptionPane.showMessageDialog(this, "Account created successfully! Please login.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                showLoginScreen();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Username may already exist.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(signupButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Login link
        JPanel loginLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginLinkPanel.setBackground(Color.WHITE);
        JLabel loginLabel = new JLabel("Already have an account? ");
        loginLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel loginLink = new JLabel("Login");
        loginLink.setFont(new Font("Arial", Font.BOLD, 12));
        loginLink.setForeground(darkRed);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showLoginScreen();
            }
        });
        loginLinkPanel.add(loginLabel);
        loginLinkPanel.add(loginLink);
        formPanel.add(loginLinkPanel);
        
        signupPanel.add(formPanel);
        add(signupPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    private JTextField createSignupField(JPanel parent, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(300, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createRigidArea(new Dimension(0, 5)));
        parent.add(field);
        parent.add(Box.createRigidArea(new Dimension(0, 10)));
        return field;
    }
    
    private JPasswordField createSignupPasswordField(JPanel parent, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(300, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createRigidArea(new Dimension(0, 5)));
        parent.add(field);
        parent.add(Box.createRigidArea(new Dimension(0, 10)));
        return field;
    }
    
    private boolean authenticateUser(String username, String password) {
        try {
            String hashedPassword = hashPassword(password);
            String query = "SELECT user_id, username, user_role FROM users WHERE username = ? AND password_hash = ? AND is_active = TRUE";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                currentUserId = rs.getInt("user_id");
                currentUsername = rs.getString("username");
                currentUserRole = rs.getString("user_role");
                
                // Update last login
                String updateQuery = "UPDATE users SET last_login = NOW() WHERE user_id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setInt(1, currentUserId);
                updateStmt.executeUpdate();
                
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean registerUser(String fullName, String username, String password, 
                                 String email, String contact, String address) {
        try {
            String hashedPassword = hashPassword(password);
            String query = "INSERT INTO users (username, password_hash, full_name, email, contact_number, address, user_role) " +
                          "VALUES (?, ?, ?, ?, ?, ?, 'donor')";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, fullName);
            pstmt.setString(4, email);
            pstmt.setString(5, contact);
            pstmt.setString(6, address);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return password;
        }
    }
    
    private void loadMainApplication() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(200, 215, 220, 100));
                int[] xPos = {100, 450, 750};
                int[] yPos = {50, 100, 50};
                for (int i = 0; i < 3; i++) {
                    drawBloodDrop(g2d, xPos[i], yPos[i], 250);
                }
            }

			private void drawBloodDrop(Graphics2D g2d, int i, int j, int k) {
				// TODO Auto-generated method stub
				
			}
        };
        contentPanel.setBackground(backgroundGray);
        
        createSidebar();
        contentPanel.add(sidebar, BorderLayout.WEST);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false);
        
        mainPanel.add(createMyProfileScreen(), "myProfile");
        mainPanel.add(createDonorListScreen(), "donorList");
        mainPanel.add(createRequestBloodScreen(), "requestBlood");
        mainPanel.add(createDonateBloodScreen(), "donateBlood");
        mainPanel.add(createBloodRequestStatusScreen(), "requestStatus");
        
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        add(contentPanel);
        
        cardLayout.show(mainPanel, "myProfile");
        revalidate();
        repaint();
    }
    
    private Component createDonateBloodScreen() {
		// TODO Auto-generated method stub
		return null;
	}

	private Component createBloodRequestStatusScreen() {
		// TODO Auto-generated method stub
		return null;
	}

	private void createSidebar() {
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(240, 240, 240));
        sidebar.setPreferredSize(new Dimension(220, 700));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        addMenuItem("Home", "myProfile", false);
        addMenuItem("Donor List", "donorList", false);
        addMenuItem("Request Blood", "requestBlood", false);
        addMenuItem("My Profile", "myProfile", true);
        
        JButton logoutBtn = new JButton("📋 Logout");
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(200, 50));
        logoutBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(true);
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.setBackground(new Color(240, 240, 240));
        logoutBtn.setForeground(Color.DARK_GRAY);
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", "Logout", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                currentUserId = -1;
                currentUsername = "";
                currentUserRole = "";
                showLoginScreen();
            }
        });
        sidebar.add(logoutBtn);
    }
    
    private void addMenuItem(String text, String cardName, boolean selected) {
        JButton btn = new JButton("  " + text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 50));
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        
        String iconText = "";
        if (text.equals("Home")) iconText = "🏠 ";
        else if (text.equals("Donor List")) iconText = "💧 ";
        else if (text.equals("Request Blood")) iconText = "🩸 ";
        else if (text.equals("My Profile")) iconText = "☑ ";
        
        btn.setText(iconText + text);
        
        if (selected) {
            btn.setBackground(darkRed);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(240, 240, 240));
            btn.setForeground(Color.DARK_GRAY);
        }
        
        if (cardName != null) {
            btn.addActionListener(e -> {
                cardLayout.show(mainPanel, cardName);
                updateSidebarSelection(btn);
            });
        }
        
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    private void updateSidebarSelection(JButton selectedBtn) {
        for (Component c : sidebar.getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                if (!btn.getText().contains("Logout")) {
                    btn.setBackground(new Color(240, 240, 240));
                    btn.setForeground(Color.DARK_GRAY);
                }
            }
        }
        selectedBtn.setBackground(darkRed);
        selectedBtn.setForeground(Color.WHITE);
    }
    
    private JPanel createMyProfileScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JPanel header = createHeader("Blood Bank Management System", false);
        panel.add(header, BorderLayout.NORTH);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Load user data from database
        try {
            String query = "SELECT full_name, username, contact_number, address FROM users WHERE user_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                content.add(createProfileField("Full Name:", rs.getString("full_name")));
                content.add(createProfileField("Username:", rs.getString("username")));
                content.add(createProfileField("Contact Info:", rs.getString("contact_number")));
                content.add(createProfileField("Address:", rs.getString("address")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
        btnPanel.setOpaque(false);
        btnPanel.add(createButton("Edit Profile", darkRed, true));
        btnPanel.add(createButton("Change Password", lightBlue, true));
        content.add(btnPanel);
        
        content.add(createQuickActionButtons());
        
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }
    
    private Component createQuickActionButtons() {
		// TODO Auto-generated method stub
		return null;
	}

	private Component createProfileField(String string, String string2) {
		// TODO Auto-generated method stub
		return null;
	}

	private JPanel createHeader(String string, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	private JPanel createDonorListScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JPanel header = createHeader("Blood Bank Management System", false);
        panel.add(header, BorderLayout.NORTH);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        JLabel title = new JLabel("Registered Donors");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setOpaque(false);
        JTextField searchField = new JTextField("🔍 Search by Name...", 30);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchPanel.add(searchField);
        searchPanel.add(createButton("Add New Donor", lightBlue, true));
        content.add(searchPanel);
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Load donor data from database
        String[] columns = {"Name", "Blood Group", "Contact Information", "Last Donation", "Next Eligible"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        try {
            String query = "SELECT u.full_name, d.blood_group, u.contact_number, d.last_donation_date, d.next_eligible_date " +
                          "FROM donors d JOIN users u ON d.user_id = u.user_id WHERE u.is_active = TRUE";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("full_name"),
                    rs.getString("blood_group"),
                    rs.getString("contact_number"),
                    rs.getDate("last_donation_date"),
                    rs.getDate("next_eligible_date")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(200, 215, 225));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 200));
        content.add(scrollPane);
        
        content.add(createQuickActionButtons());
        
        panel.add(content, BorderLayout.CENTER);
        return panel;
	}
    
    
    private JPanel createRequestBloodScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JPanel header = createHeader("Blood Bank Management System", true);
        panel.add(header, BorderLayout.NORTH);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        JLabel title = new JLabel("Request Blood Form");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 30)));
        
        JTextField patientNameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField bloodGroupField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField quantity