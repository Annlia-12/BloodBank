package blood;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestBloodScreen extends JPanel {
    private int userId;
    private JTextField patientNameField, cityField;
    private JComboBox<String> bloodGroupBox;
    private JSpinner unitsSpinner;

    public RequestBloodScreen(int userId) {
        this.userId = userId;
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(10,10));
        add(UIUtils.createHeader("Create Blood Request", 58), BorderLayout.NORTH);
        add(makeForm(), BorderLayout.CENTER);
    }

    private JPanel makeForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,12,10,12);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx=0; gbc.gridy=0; p.add(new JLabel("Patient Name:"), gbc);
        gbc.gridx=1; patientNameField = UIUtils.createField(16); p.add(patientNameField, gbc);

        gbc.gridx=0; gbc.gridy++; p.add(new JLabel("Blood Group:"), gbc);
        gbc.gridx=1; bloodGroupBox = new JComboBox<>(new String[]{"A+","A-","B+","B-","AB+","AB-","O+","O-"}); bloodGroupBox.setFont(UIUtils.REGULAR_FONT); p.add(bloodGroupBox, gbc);

        gbc.gridx=0; gbc.gridy++; p.add(new JLabel("Units needed:"), gbc);
        gbc.gridx=1; unitsSpinner = new JSpinner(new SpinnerNumberModel(1,1,10,1)); p.add(unitsSpinner, gbc);

        gbc.gridx=0; gbc.gridy++; p.add(new JLabel("City:"), gbc);
        gbc.gridx=1; cityField = UIUtils.createField(12); p.add(cityField, gbc);

        gbc.gridx=0; gbc.gridy++; gbc.gridwidth=2; gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttons = new JPanel(); buttons.setOpaque(false);
        JButton requestBtn = UIUtils.createPrimaryButton("Create Request");
        JButton quickMatchBtn = UIUtils.createSecondaryButton("Create & Auto-Match");
        buttons.add(requestBtn); buttons.add(Box.createHorizontalStrut(8)); buttons.add(quickMatchBtn);
        p.add(buttons, gbc);

        requestBtn.addActionListener(e -> createRequest(false));
        quickMatchBtn.addActionListener(e -> createRequest(true));
        return p;
    }

    private void createRequest(boolean autoMatch) {
        String patient = patientNameField.getText().trim();
        String group = (String) bloodGroupBox.getSelectedItem();
        int units = (Integer) unitsSpinner.getValue();
        String city = cityField.getText().trim();
        if (patient.isEmpty() || city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill patient name and city.");
            return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String insert = "INSERT INTO blood_request (requester_user_id, patient_name, blood_group, units_needed, city) VALUES (?,?,?,?,?)";
                int requestId;
                try (PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, userId);
                    ps.setString(2, patient);
                    ps.setString(3, group);
                    ps.setInt(4, units);
                    ps.setString(5, city);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        requestId = rs.getInt(1);
                    }
                }

                if (autoMatch) {
                    String find = "SELECT id FROM donors WHERE blood_group = ? AND (city = ? OR available_for_camp = 1) AND active = 1";
                    try (PreparedStatement ps2 = conn.prepareStatement(find)) {
                        ps2.setString(1, group);
                        ps2.setString(2, city);
                        try (ResultSet rs = ps2.executeQuery()) {
                            List<Integer> donors = new ArrayList<>();
                            while (rs.next()) donors.add(rs.getInt("id"));
                            for (int did : donors) {
                                String msg = "Donor matched for request " + requestId + " (group " + group + ")";
                                try (PreparedStatement ps3 = conn.prepareStatement("INSERT INTO notifications (donor_id, request_id, message) VALUES (?,?,?)")) {
                                    ps3.setInt(1, did);
                                    ps3.setInt(2, requestId);
                                    ps3.setString(3, msg);
                                    ps3.executeUpdate();
                                }
                            }
                        }
                    }
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "Request created. " + (autoMatch ? "Auto-match attempted." : ""));
                patientNameField.setText(""); cityField.setText("");
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
