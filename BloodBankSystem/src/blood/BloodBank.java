package blood;

import javax.swing.*;

public class BloodBank {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new LoginScreen().setVisible(true);
        });
    }
}
