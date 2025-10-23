package blood;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BloodCampsScreen extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public BloodCampsScreen() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(10,10));
        add(UIUtils.createHeader("Hospitals / Blood Camps", 58), BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Hospital ID","Name","City","Address","Phone"},0) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Color.WHITE);
        top.add(new JLabel("Filter by city:"));
        JTextField cityFilter = UIUtils.createField(12);
        JButton filterBtn = UIUtils.createSecondaryButton("Filter");
        JButton reload = UIUtils.createPrimaryButton("Reload");
        top.add(cityFilter); top.add(filterBtn); top.add(reload);
        add(top, BorderLayout.NORTH);

        filterBtn.addActionListener(e -> loadHospitals(cityFilter.getText().trim()));
        reload.addActionListener(e -> loadHospitals(null));
        loadHospitals(null);
    }

    private void loadHospitals(String city) {
        model.setRowCount(0);
        String q = (city == null || city.isEmpty()) ? "SELECT id,name,city,address,phone FROM hospitals" :
                "SELECT id,name,city,address,phone FROM hospitals WHERE city LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(q)) {
            if (city != null && !city.isEmpty()) ps.setString(1, "%" + city + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("city"),
                            rs.getString("address"),
                            rs.getString("phone")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }
}
