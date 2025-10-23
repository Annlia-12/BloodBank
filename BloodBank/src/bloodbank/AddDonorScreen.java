package bloodbank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddDonorScreen extends JFrame implements ActionListener {
    private JTextField nameField, unitsField, healthField, contactField;
    private JComboBox<String> bloodGroupCombo, locationCombo;
    private JButton submitButton;
    private Connection con;

    // Blood groups
    private final String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    // Kerala districts
    private final String[] locations = {
        "Thiruvananthapuram", "Kollam", "Pathanamthitta", "Alappuzha",
        "Kottayam", "Idukki", "Ernakulam", "Thrissur",
        "Palakkad", "Malappuram", "Kozhikode", "Wayanad",
        "Kannur", "Kasaragod"
    };

    public AddDonorScreen(Connection con) {
        this.con = con;

        setTitle("Add Donor / Donate Blood");
        setSize(450, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // 🔴 Header panel
        JPanel header = new JPanel();
        header.setBackground(new Color(153, 0, 0));
        JLabel title = new JLabel("Donate Blood — Save Lives");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // 📋 Center form panel
        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        form.setBackground(Color.WHITE);

        form.add(new JLabel("Your Name:"));
        nameField = new JTextField();
        form.add(nameField);

        form.add(new JLabel("Blood Group:"));
        bloodGroupCombo = new JComboBox<>(bloodGroups);
        form.add(bloodGroupCombo);

        form.add(new JLabel("Units Donated:"));
        unitsField = new JTextField();
        form.add(unitsField);

        form.add(new JLabel("Location:"));
        locationCombo = new JComboBox<>(locations);
        form.add(locationCombo);

        form.add(new JLabel("Health Issues (if any):"));
        healthField = new JTextField();
        form.add(healthField);

        form.add(new JLabel("Contact Number:"));
        contactField = new JTextField();
        form.add(contactField);

        form.add(new JLabel(""));
        submitButton = new JButton("Submit Donation");
        submitButton.setBackground(new Color(153, 0, 0));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        submitButton.addActionListener(this);
        form.add(submitButton);

        add(form, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String name = nameField.getText().trim();
            String bloodGroup = bloodGroupCombo.getSelectedItem().toString();
            String location = locationCombo.getSelectedItem().toString();
            String healthIssues = healthField.getText().trim();
            String contact = contactField.getText().trim();

            int units;
            try {
                units = Integer.parseInt(unitsField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid number of units.");
                return;
            }

            if (name.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Contact cannot be empty.");
                return;
            }

            try {
                // 1️⃣ Insert into donors table
                String donorQuery = "INSERT INTO donors(name, blood_group, units, location, health_issues, contact) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement psDonor = con.prepareStatement(donorQuery);
                psDonor.setString(1, name);
                psDonor.setString(2, bloodGroup);
                psDonor.setInt(3, units);
                psDonor.setString(4, location);
                psDonor.setString(5, healthIssues);
                psDonor.setString(6, contact);
                psDonor.executeUpdate();

                // 2️⃣ Update blood stock table
                String stockCheck = "SELECT units FROM blood_stock WHERE blood_group = ?";
                PreparedStatement psCheck = con.prepareStatement(stockCheck);
                psCheck.setString(1, bloodGroup);
                ResultSet rs = psCheck.executeQuery();

                if (rs.next()) {
                    int currentUnits = rs.getInt("units");
                    String updateStock = "UPDATE blood_stock SET units = ? WHERE blood_group = ?";
                    PreparedStatement psUpdate = con.prepareStatement(updateStock);
                    psUpdate.setInt(1, currentUnits + units);
                    psUpdate.setString(2, bloodGroup);
                    psUpdate.executeUpdate();
                } else {
                    String insertStock = "INSERT INTO blood_stock(blood_group, units) VALUES(?, ?)";
                    PreparedStatement psInsert = con.prepareStatement(insertStock);
                    psInsert.setString(1, bloodGroup);
                    psInsert.setInt(2, units);
                    psInsert.executeUpdate();
                }

             // 3️⃣ Insert into donation_history (columns matched exactly)
                String historyQuery =
                  "INSERT INTO donation_history(donor_name, blood_group, units, location, health_issues, contact, donation_date) " +
                  "VALUES (?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement psHistory = con.prepareStatement(historyQuery);
                psHistory.setString(1, name);            // donor_name
                psHistory.setString(2, bloodGroup);      // blood_group
                psHistory.setInt(3, units);              // units
                psHistory.setString(4, location);        // location
                psHistory.setString(5, healthIssues);    // health_issues
                psHistory.setString(6, contact);         // contact
                psHistory.executeUpdate();


                // 4️⃣ Notify matching requests
                String notifQuery = "INSERT INTO notifications(username, type, message) " +
                        "SELECT requester_name, 'Donor Available', CONCAT('Donor ', ?, ' (', ?, ') donated ', ?, ' units of ', ?) " +
                        "FROM blood_requests WHERE blood_group = ? AND location = ?";
                PreparedStatement psNotif = con.prepareStatement(notifQuery);
                psNotif.setString(1, name);
                psNotif.setString(2, contact);
                psNotif.setInt(3, units);
                psNotif.setString(4, bloodGroup);
                psNotif.setString(5, bloodGroup);
                psNotif.setString(6, location);
                psNotif.executeUpdate();

                JOptionPane.showMessageDialog(this, "Donation recorded successfully!");
                dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}
