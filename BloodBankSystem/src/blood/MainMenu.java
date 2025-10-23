package blood;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class MainMenu extends JFrame {
    private int userId;

    public MainMenu(int userId) {
        this.userId = userId;
        setTitle("Blood Bank — Main Menu");
        setSize(1000,650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    public MainMenu(Connection con, String u) {
		// TODO Auto-generated constructor stub
	}

	private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.PRIMARY_BG);
        root.add(UIUtils.createHeader("Blood Bank Management", 68), BorderLayout.NORTH);

        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        nav.setBackground(UIUtils.PRIMARY_BG);
        nav.setPreferredSize(new Dimension(240, getHeight()));

        JLabel menu = new JLabel("Menu");
        menu.setFont(UIUtils.HEADER_FONT);
        nav.add(menu);
        nav.add(Box.createVerticalStrut(12));

        JButton profileBtn = UIUtils.createSecondaryButton("Profile");
        JButton donorBtn = UIUtils.createSecondaryButton("Donor Screen");
        JButton viewStockBtn = UIUtils.createSecondaryButton("View Stocks");
        JButton requestBtn = UIUtils.createSecondaryButton("Request Blood");
        JButton campsBtn = UIUtils.createSecondaryButton("Blood Camps / Hospitals");
        JButton notifBtn = UIUtils.createSecondaryButton("Notifications");
        JButton historyBtn = UIUtils.createSecondaryButton("Donation History");
        JButton logoutBtn = UIUtils.createSecondaryButton("Logout");

        nav.add(profileBtn); nav.add(Box.createVerticalStrut(8));
        nav.add(donorBtn); nav.add(Box.createVerticalStrut(8));
        nav.add(viewStockBtn); nav.add(Box.createVerticalStrut(8));
        nav.add(requestBtn); nav.add(Box.createVerticalStrut(8));
        nav.add(campsBtn); nav.add(Box.createVerticalStrut(8));
        nav.add(notifBtn); nav.add(Box.createVerticalStrut(8));
        nav.add(historyBtn); nav.add(Box.createVerticalGlue());
        nav.add(logoutBtn);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        content.add(new JLabel("Select an action from left menu", SwingConstants.CENTER), BorderLayout.CENTER);

        root.add(nav, BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);
        add(root);

        profileBtn.addActionListener(e -> setContentPanel(new ProfileScreen(userId)));
        donorBtn.addActionListener(e -> setContentPanel(new AddDonorScreen(userId)));
        viewStockBtn.addActionListener(e -> setContentPanel(new ViewStockScreen()));
        requestBtn.addActionListener(e -> setContentPanel(new RequestBloodScreen(userId)));
        campsBtn.addActionListener(e -> setContentPanel(new BloodCampsScreen()));
        notifBtn.addActionListener(e -> setContentPanel(new NotificationsScreen(userId)));
        historyBtn.addActionListener(e -> setContentPanel(new DonationHistoryScreen(userId)));
        logoutBtn.addActionListener(e -> { dispose(); new LoginScreen().setVisible(true); });
    }

    private void setContentPanel(JComponent panel) {
        getContentPane().removeAll();
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.PRIMARY_BG);
        root.add(UIUtils.createHeader("Blood Bank Management", 68), BorderLayout.NORTH);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);
        main.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        main.add(panel, BorderLayout.CENTER);

        // Recreate nav
        JPanel navContainer = new JPanel(new BorderLayout());
        navContainer.setBackground(UIUtils.PRIMARY_BG);
        navContainer.setPreferredSize(new Dimension(240, getHeight()));
        // reuse the previous nav building logic
        // For simplicity re-add the left menu using same controls:
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        nav.setOpaque(false);
        JButton profileBtn = UIUtils.createSecondaryButton("Profile");
        JButton donorBtn = UIUtils.createSecondaryButton("Donor Screen");
        JButton viewStockBtn = UIUtils.createSecondaryButton("View Stocks");
        JButton requestBtn = UIUtils.createSecondaryButton("Request Blood");
        JButton campsBtn = UIUtils.createSecondaryButton("Blood Camps / Hospitals");
        JButton notifBtn = UIUtils.createSecondaryButton("Notifications");
        JButton historyBtn = UIUtils.createSecondaryButton("Donation History");
        JButton logoutBtn = UIUtils.createSecondaryButton("Logout");
        nav.add(new JLabel("Menu")); nav.add(Box.createVerticalStrut(12));
        nav.add(profileBtn); nav.add(Box.createVerticalStrut(8));
        nav.add(donorBtn); nav.add(Box.createVerticalStrut(8));
        nav.add(viewStockBtn); nav.add(Box.createVerticalStrut(8));
        nav.add(requestBtn); nav.add(Box.createVerticalStrut(8));
        nav.add(campsBtn); nav.add(Box.createVerticalStrut(8));
        nav.add(notifBtn); nav.add(Box.createVerticalStrut(8));
        nav.add(historyBtn); nav.add(Box.createVerticalGlue());
        nav.add(logoutBtn);
        navContainer.add(nav, BorderLayout.CENTER);

        root.add(navContainer, BorderLayout.WEST);
        root.add(main, BorderLayout.CENTER);
        setContentPane(root);
        revalidate();
        repaint();

        // wire actions again (simple)
        profileBtn.addActionListener(e -> setContentPanel(new ProfileScreen(userId)));
        donorBtn.addActionListener(e -> setContentPanel(new AddDonorScreen(userId)));
        viewStockBtn.addActionListener(e -> setContentPanel(new ViewStockScreen()));
        requestBtn.addActionListener(e -> setContentPanel(new RequestBloodScreen(userId)));
        campsBtn.addActionListener(e -> setContentPanel(new BloodCampsScreen()));
        notifBtn.addActionListener(e -> setContentPanel(new NotificationsScreen(userId)));
        historyBtn.addActionListener(e -> setContentPanel(new DonationHistoryScreen(userId)));
        logoutBtn.addActionListener(e -> { dispose(); new LoginScreen().setVisible(true); });
    }
}
