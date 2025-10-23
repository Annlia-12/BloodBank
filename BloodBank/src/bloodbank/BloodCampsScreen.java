package bloodbank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BloodCampsScreen extends JFrame {
    JTable table;
    Connection con;

    public BloodCampsScreen(Connection con){
        this.con = con;
        setTitle("Blood Camps");
        setSize(700,400);
        setLocationRelativeTo(null);

        String[] columns = {"Event Name","Location","Date"};
        DefaultTableModel model = new DefaultTableModel(columns,0);
        table = new JTable(model);
        add(new JScrollPane(table));

        loadCamps(model);
        setVisible(true);
    }

    private void loadCamps(DefaultTableModel model){
        try{
            ResultSet rs = con.createStatement().executeQuery("SELECT event_name,location,event_date FROM default_hospitals ORDER BY event_date ASC");
            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getString("event_name"),
                        rs.getString("location"),
                        rs.getDate("event_date")
                });
            }
        } catch(Exception e){
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage());
        }
    }
}
