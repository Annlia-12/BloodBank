package bloodbank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;

public class MainMenu extends JFrame implements ActionListener {
    JButton addDonor, viewStock, requestBlood, donorMatching, profileScreen, donationHistory, bloodCamps, notifications, exitButton;
    Connection con;
    String currentUser;
    String role;

    public MainMenu(Connection con, String username, String role) {
        this.con = con;
        this.currentUser = username;
        this.role = role.toLowerCase();

        setTitle("Main Menu - " + username);
        setSize(400, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(9, 1, 10, 10));

        addDonor = new JButton("Add Donor");
        viewStock = new JButton("View Blood Stock");
        requestBlood = new JButton("Request Blood");
        donorMatching = new JButton("Donor Matching");
        profileScreen = new JButton("Profile / Update");
        donationHistory = new JButton("Donation History");
        bloodCamps = new JButton("Blood Camps / Events");
        notifications = new JButton("Notifications");
        exitButton = new JButton("Exit");

        JButton[] buttons = {addDonor, viewStock, requestBlood, donorMatching, profileScreen, donationHistory, bloodCamps, notifications, exitButton};

        for (JButton b : buttons) {
            b.setBackground(new Color(220, 0, 0));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 14));
            b.addActionListener(this);
            add(b);
        }

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addDonor) new AddDonorScreen(con);          // Adds donor
        else if (e.getSource() == viewStock) new ViewStockScreen(con);               // Shows stock
        else if (e.getSource() == requestBlood) new RequestBloodScreen(con, currentUser);  // Request blood
        else if (e.getSource() == donorMatching) new DonorMatchingScreen(con, currentUser); // Matching donors
        else if (e.getSource() == profileScreen) new ProfileScreen(con, currentUser);       // Update profile
        else if (e.getSource() == donationHistory) new DonationHistoryScreen(con, currentUser); // History
        else if (e.getSource() == bloodCamps) new BloodCampsScreen(con);             // Blood camps
        else if (e.getSource() == notifications) new NotificationsScreen(con, currentUser); // Notifications
        else if (e.getSource() == exitButton) dispose();
    }
}
