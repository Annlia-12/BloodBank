package bloodbank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RequestBloodScreen extends JFrame implements ActionListener {
    private JTextField nameField, unitsField;
    private JComboBox<String> bloodGroupCombo, locationCombo;
    private JButton submitButton, backButton;
    private Connection con;
    private String currentUser;

    private final String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private final String[] locations = {
        "Thiruvananthapuram", "Kollam", "Pathanamthitta", "Alappuzha",
        "Kottayam", "Idukki", "Ernakulam", "Thrissur",
        "Palakkad", "Malappuram", "Kozhikode", "Wayanad",
        "Kannur", "Kasaragod"
    };

    public RequestBloodScreen(Connection con, String currentUser) {
        this.con = con;
        this.currentUser = currentUser;

        setTitle("Request Blood");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new GridLayout(7, 2, 10, 10));

        add(new JLabel("Requester Name:"));
        nameField = new JTextField(currentUser);
        nameField.setEditable(false);
        add(nameField);

        add(new JLabel("Blood Group:"));
        bloodGroupCombo = new JComboBox<>(bloodGroups);
        add(bloodGroupCombo);

        add(new JLabel("Units Required:"));
        unitsField = new JTextField();
        add(unitsField);

        add(new JLabel("Location:"));
        locationCombo = new JComboBox<>(locations);
        add(locationCombo);

        submitButton = new JButton("Submit Request");
        submitButton.setBackground(new Color(180, 0, 0));
        submitButton.setForeground(Color.WHITE);

        backButton = new JButton("Back");
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);

        add(submitButton);
        add(backButton);

        submitButton.addActionListener(this);
        backButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            handleBloodRequest();
        }
    }

    private void handleBloodRequest() {
        try {
            String requester = nameField.getText().trim();
            String bloodGroup = bloodGroupCombo.getSelectedItem().toString();
            String location = locationCombo.getSelectedItem().toString();
            int requestedUnits = Integer.parseInt(unitsField.getText().trim());

            if (requestedUnits <= 0) {
                JOptionPane.showMessageDialog(this, "Units must be greater than zero.");
                return;
            }

            // 1️⃣ Check available stock
            String checkStock = "SELECT units FROM blood_stock WHERE blood_group = ?";
            PreparedStatement psCheck = con.prepareStatement(checkStock);
            psCheck.setString(1, bloodGroup);
            ResultSet rs = psCheck.executeQuery();

            int availableUnits = 0;
            if (rs.next()) availableUnits = rs.getInt("units");

            String status;
            if (availableUnits >= requestedUnits) {
                // 2️⃣ Reduce stock
                String updateStock = "UPDATE blood_stock SET units = units - ? WHERE blood_group = ?";
                PreparedStatement psUpdate = con.prepareStatement(updateStock);
                psUpdate.setInt(1, requestedUnits);
                psUpdate.setString(2, bloodGroup);
                psUpdate.executeUpdate();

                status = "Approved";

                // 3️⃣ Notify matching donors
                String notifyDonors = """
                    INSERT INTO notifications(username, type, message)
                    SELECT donor_name, 'Blood Accepted',
                    CONCAT('Your donated ', ?, ' units of ', ?, ' have been matched with a request in ', ?)
                    FROM donation_history
                    WHERE blood_group = ? AND location = ? AND status = 'Pending'
                """;
                PreparedStatement psNotif = con.prepareStatement(notifyDonors);
                psNotif.setInt(1, requestedUnits);
                psNotif.setString(2, bloodGroup);
                psNotif.setString(3, location);
                psNotif.setString(4, bloodGroup);
                psNotif.setString(5, location);
                psNotif.executeUpdate();

                // 4️⃣ Update donation history
                String updateDonation = "UPDATE donation_history SET status = 'Matched' WHERE blood_group = ? AND location = ? AND status = 'Pending'";
                PreparedStatement psMatch = con.prepareStatement(updateDonation);
                psMatch.setString(1, bloodGroup);
                psMatch.setString(2, location);
                psMatch.executeUpdate();

            } else {
                // Not enough stock
                status = "Pending";

                // Notify requester
                String notifyPending = "INSERT INTO notifications(username, type, message) VALUES (?, 'Stock Unavailable', ?)";
                PreparedStatement psNotif = con.prepareStatement(notifyPending);
                psNotif.setString(1, requester);
                psNotif.setString(2, "Your request for " + requestedUnits + " units of " + bloodGroup + " is pending due to low stock.");
                psNotif.executeUpdate();
            }

            // 5️⃣ Save request record correctly (fixed column names)
            String insertRequest = """
                INSERT INTO blood_requests(requester_name, blood_group, units, location, status, request_date)
                VALUES (?, ?, ?, ?, ?, NOW())
            """;
            PreparedStatement psReq = con.prepareStatement(insertRequest);
            psReq.setString(1, requester);
            psReq.setString(2, bloodGroup);
            psReq.setInt(3, requestedUnits);
            psReq.setString(4, location);
            psReq.setString(5, status);
            psReq.executeUpdate();

            JOptionPane.showMessageDialog(this, "Blood request submitted! Status: " + status);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for units.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
