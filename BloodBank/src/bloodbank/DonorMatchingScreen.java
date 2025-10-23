package bloodbank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DonorMatchingScreen extends JFrame {
    JTable table;
    Connection con;
    String currentUser;

    public DonorMatchingScreen(Connection con, String currentUser){
        this.con = con;
        this.currentUser = currentUser;

        setTitle("Donor Matching");
        setSize(700,400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] columns = {"Donor Name","Blood Group","Location","Health Issues","Contact"};
        DefaultTableModel model = new DefaultTableModel(columns,0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadDonors(model);

        table.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent evt){
                int row = table.getSelectedRow();
                String donorName = table.getValueAt(row,0).toString();
                try{
                    PreparedStatement ps = con.prepareStatement("INSERT INTO notifications(username,type,message) VALUES(?,?,?)");
                    ps.setString(1, donorName);
                    ps.setString(2,"Blood Request Accepted");
                    ps.setString(3,"You have been selected to donate blood to "+currentUser);
                    ps.executeUpdate();

                    ps.setString(1,currentUser);
                    ps.setString(2,"Donor Confirmed");
                    ps.setString(3,"Donor "+donorName+" confirmed for your request");
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(null,"Donor confirmed and notifications sent!");
                } catch(Exception e){
                    JOptionPane.showMessageDialog(null,"Error: "+e.getMessage());
                }
            }
        });

        setVisible(true);
    }

    private void loadDonors(DefaultTableModel model){
        try{
            ResultSet rs = con.createStatement().executeQuery("SELECT name,blood_group,location,health_issues,contact FROM donors");
            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getString("blood_group"),
                        rs.getString("location"),
                        rs.getString("health_issues"),
                        rs.getString("contact")
                });
            }
        } catch(Exception e){
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage());
        }
    }
}
