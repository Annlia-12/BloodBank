package blood;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewStockScreen extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public ViewStockScreen() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(10,10));
        add(UIUtils.createHeader("Blood Stocks", 58), BorderLayout.NORTH);
        model = new DefaultTableModel(new Object[]{"Blood Group","Units"},0) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model);
        table.setFont(UIUtils.REGULAR_FONT);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Color.WHITE);
        JButton refresh = UIUtils.createPrimaryButton("Refresh");
        bottom.add(refresh);
        add(bottom, BorderLayout.SOUTH);
        refresh.addActionListener(e -> loadStocks());
        loadStocks();
    }

    private void loadStocks() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT blood_group, units FROM blood_stock");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("blood_group"), rs.getInt("units")});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }
}
