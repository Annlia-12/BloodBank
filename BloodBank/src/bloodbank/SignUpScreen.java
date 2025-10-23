package bloodbank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SignUpScreen extends JFrame implements ActionListener {
    JTextField usernameField, nameField, locationField, contactField;
    JPasswordField passwordField;
    JComboBox<String> roleBox;
    JButton signupButton;
    Connection con;

    public SignUpScreen(Connection con) {
        this.con = con;
        setTitle("Sign Up");
        setSize(400, 400);
        setLayout(new GridLayout(7,2,10,10));
        setLocationRelativeTo(null);

        add(new JLabel("Username:")); usernameField=new JTextField(); add(usernameField);
        add(new JLabel("Password:")); passwordField=new JPasswordField(); add(passwordField);
        add(new JLabel("Name:")); nameField=new JTextField(); add(nameField);
        add(new JLabel("Location:")); locationField=new JTextField(); add(locationField);
        add(new JLabel("Contact:")); contactField=new JTextField(); add(contactField);
        add(new JLabel("Role:")); roleBox=new JComboBox<>(new String[]{"donor","recipient","admin"}); add(roleBox);

        signupButton=new JButton("Sign Up"); add(new JLabel("")); add(signupButton);
        signupButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            PreparedStatement ps = con.prepareStatement("INSERT INTO users(username,password,name,location,contact,role) VALUES(?,?,?,?,?,?)");
            ps.setString(1, usernameField.getText());
            ps.setString(2, new String(passwordField.getPassword()));
            ps.setString(3, nameField.getText());
            ps.setString(4, locationField.getText());
            ps.setString(5, contactField.getText());
            ps.setString(6, roleBox.getSelectedItem().toString());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Sign up successful!");
        }catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());}
    }
}
