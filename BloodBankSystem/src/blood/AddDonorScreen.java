package blood;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddDonorScreen extends JPanel {
    private int userId;
    private JTextField fullnameField, phoneField, cityField;
    private JComboBox<String> bloodGroupBox;
    private JRadioButton maleRb, femaleRb, otherRb;
    private JCheckBox campCheck;

    public AddDonorScreen(int userId) {
        this.userId = userId;
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(12,12));
        add(createContent(), BorderLayout.CENTER);
    }

    private JPanel createContent() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(UIUtils.createHeader("Register as Donor", 56), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx=0; gbc.gridy=0; form.add(new JLabel("Full name:"), gbc);
        gbc.gridx=1; fullnameField = UIUtils.createField(18); form.add(fullnameField, gbc);

        gbc.gridx=0; gbc.gridy++; form.add(new JLabel("Phone:"), gbc);
        gbc.gridx=1; phoneField = UIUtils.createField(12); form.add(phoneField, gbc);

        gbc.gridx=0; gbc.gridy++; form.add(new JLabel("City:"), gbc);
        gbc.gridx=1; cityField = UIUtils.createField(14); form.add(cityField, gbc);

        gbc.gridx=0; gbc.gridy++; form.add(new JLabel("Blood group:"), gbc);
        gbc.gridx=1; bloodGroupBox = new JComboBox<>(new String[]{"A+","A-","B+","B-","AB+","AB-","O+","O-"}); bloodGroupBox.setFont(UIUtils.REGULAR_FONT); form.add(bloodGroupBox, gbc);

        gbc.gridx=0; gbc.gridy++; form.add(new JLabel("Gender:"), gbc);
        gbc.gridx=1;
        JPanel gender = new JPanel(new FlowLayout(FlowLayout.LEFT,6,0));
        gender.setOpaque(false);
        maleRb = new JRadioButton("Male"); femaleRb = new JRadioButton("Female"); otherRb = new JRadioButton("Other");
        maleRb.setBackground(Color.WHITE); femaleRb.setBackground(Color.WHITE); otherRb.setBackground(Color.WHITE);
        ButtonGroup g = new ButtonGroup(); g.add(maleRb); g.add(femaleRb); g.add(otherRb);
        gender.add(maleRb); gender.add(femaleRb); gender.add(otherRb);
        form.add(gender, gbc);

        gbc.gridx=0; gbc.gridy++; form.add(new JLabel("Available for camps:"), gbc);
        gbc.gridx=1; campCheck = new JCheckBox("Yes (show in camps)"); campCheck.setSelected(true); campCheck.setBackground(Color.WHITE); form.add(campCheck, gbc);

        gbc.gridx=0; gbc.gridy++; gbc.gridwidth=2; gbc.anchor = GridBagConstraints.CENTER;
        JPanel btns = new JPanel(); btns.setOpaque(false);
        JButton save = UIUtils.createPrimaryButton("Register as Donor");
        JButton clear = UIUtils.createSecondaryButton("Clear");
        btns.add(save); btns.add(Box.createHorizontalStrut(10)); btns.add(clear);
        form.add(btns, gbc);

        p.add(form, BorderLayout.CENTER);

        save.addActionListener(e -> registerDonor());
        clear.addActionListener(e -> clearForm());
        return p;
    }

    private void clearForm() {
        fullnameField.setText("");
        phoneField.setText("");
        cityField.setText("");
        bloodGroupBox.setSelectedIndex(0);
        maleRb.setSelected(false);
        femaleRb.setSelected(false);
        otherRb.setSelected(false);
        campCheck.setSelected(true);
    }

    private void registerDonor() {
        String fullname = fullnameField.getText().trim();
        String phone = phoneField.getText().trim();
        String city = cityField.getText().trim();
        String bloodGroup = (String) bloodGroupBox.getSelectedItem();
        String gender = maleRb.isSelected() ? "Male" : femaleRb.isSelected() ? "Female" : otherRb.isSelected() ? "Other" : null;
        boolean forCamp = campCheck.isSelected();

        if (fullname.isEmpty() || phone.isEmpty() || gender==null) {
            JOptionPane.showMessageDialog(this, "Please fill name, phone and choose gender.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String insert = "INSERT INTO donors (user_id, fullname, phone, gender, blood_group, city, available_for_camp) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, userId);
                    ps.setString(2, fullname);
                    ps.setString(3, phone);
                    ps.setString(4, gender);
                    ps.setString(5, bloodGroup);
                    ps.setString(6, city);
                    ps.setBoolean(7, forCamp);
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            int donorId = rs.getInt(1);
                            String up = "UPDATE blood_stock SET units = units + 1 WHERE blood_group = ?";
                            try (PreparedStatement ps2 = conn.prepareStatement(up)) {
                                ps2.setString(1, bloodGroup);
                                int updated = ps2.executeUpdate();
                                if (updated == 0) {
                                    try (PreparedStatement ps3 = conn.prepareStatement("INSERT INTO blood_stock (blood_group, units) VALUES (?,1)")) {
                                        ps3.setString(1, bloodGroup);
                                        ps3.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                }
                conn.commit();
                JOptionPane.showMessageDialog(this, "Donor registered and stock updated for " + bloodGroup);
                clearForm();
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }
}
