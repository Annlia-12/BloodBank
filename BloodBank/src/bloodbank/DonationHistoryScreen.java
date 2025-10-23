package bloodbank;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class DonationHistoryScreen extends JFrame {
    JTable table;
    Connection con;
    String currentUser;

    public DonationHistoryScreen(Connection con, String currentUser){
        this.con = con;
        this.currentUser = currentUser;

        setTitle("Donation History");
        setSize(700,400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] columns = {"Donor/Recipient","Blood Group","Units","Location","Date"};
        DefaultTableModel model = new DefaultTableModel(columns,0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadHistory(model);
        setVisible(true);
    }

    private void loadHistory(DefaultTableModel model){
        try{
        	PreparedStatement ps = con.prepareStatement(
        		    "SELECT donor_name,blood_group,units,location,donation_date FROM donation_history WHERE donor_name=? ORDER BY donation_date DESC"
        		);

            ps.setString(1,currentUser);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getString("donor_name"),
                        rs.getString("blood_group"),
                        rs.getInt("units"),
                        rs.getString("location"),
                        rs.getDate("donation_date"),
                       
                });
            }
        } catch(Exception e){
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage());
        }
    }
}
