package bloodbank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ProfileScreen extends JFrame implements ActionListener {
    JTextField nameField, contactField;
    JComboBox<String> locationCombo;
    JButton updateButton;
    Connection con;
    String currentUser;

    private final String[] locations = {"Thiruvananthapuram","Kollam","Pathanamthitta","Alappuzha","Kottayam","Idukki","Ernakulam","Thrissur","Palakkad","Malappuram","Kozhikode","Wayanad","Kannur","Kasaragod"};

    public ProfileScreen(Connection con, String currentUser){
        this.con = con;
        this.currentUser = currentUser;

        setTitle("Profile Update");
        setSize(400,300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4,2,10,10));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Contact:"));
        contactField = new JTextField();
        add(contactField);

        add(new JLabel("Location:"));
        locationCombo = new JComboBox<>(locations);
        add(locationCombo);

        updateButton = new JButton("Update");
        updateButton.addActionListener(this);
        add(new JLabel(""));
        add(updateButton);

        loadProfile();
        setVisible(true);
    }

    private void loadProfile(){
        try{
            PreparedStatement ps = con.prepareStatement("SELECT name,contact,location FROM users WHERE username=?");
            ps.setString(1,currentUser);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                nameField.setText(rs.getString("name"));
                contactField.setText(rs.getString("contact"));
                locationCombo.setSelectedItem(rs.getString("location"));
            }
        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage());}
    }

    @Override
    public void actionPerformed(ActionEvent e){
        try{
            PreparedStatement ps = con.prepareStatement("UPDATE users SET name=?, contact=?, location=? WHERE username=?");
            ps.setString(1,nameField.getText().trim());
            ps.setString(2,contactField.getText().trim());
            ps.setString(3,locationCombo.getSelectedItem().toString());
            ps.setString(4,currentUser);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Profile updated successfully!");
            dispose();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());
        }
    }
}
