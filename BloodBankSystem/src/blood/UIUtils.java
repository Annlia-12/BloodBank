package blood;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class UIUtils {
    public static final Color HEADER_COLOR = Color.decode("#B30000");
    public static final Color PRIMARY_BG = Color.decode("#F2F2F2");
    public static final Color PRIMARY_BUTTON = Color.decode("#B30000");
    public static final Color SECONDARY_BUTTON = Color.decode("#D9D9D9");
    public static final Color INPUT_BORDER = Color.decode("#CCCCCC");
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public static JPanel createHeader(String title, int height) {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(10, height));
        header.setBackground(HEADER_COLOR);

        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(TITLE_FONT);
        header.add(lbl, BorderLayout.CENTER);
        return header;
    }

    public static JButton createPrimaryButton(String text) {
        JButton btn = new RoundedButton(text);
        btn.setBackground(PRIMARY_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setFont(HEADER_FONT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8,14,8,14));
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new RoundedButton(text);
        btn.setBackground(SECONDARY_BUTTON);
        btn.setForeground(Color.BLACK);
        btn.setFont(REGULAR_FONT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8,12,8,12));
        return btn;
    }

    public static JTextField createField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(REGULAR_FONT);
        f.setBorder(new CompoundBorder(new LineBorder(INPUT_BORDER,1,true), new EmptyBorder(6,8,6,8)));
        return f;
    }

    public static JPasswordField createPasswordField(int cols) {
        JPasswordField f = new JPasswordField(cols);
        f.setFont(REGULAR_FONT);
        f.setBorder(new CompoundBorder(new LineBorder(INPUT_BORDER,1,true), new EmptyBorder(6,8,6,8)));
        return f;
    }

    // Simple rounded button implementation
    static class RoundedButton extends JButton {
        public RoundedButton(String text) { super(text); setContentAreaFilled(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int stringWidth = fm.stringWidth(getText());
            int stringHeight = fm.getAscent();
            g2.setFont(getFont());
            g2.drawString(getText(), (getWidth()-stringWidth)/2, (getHeight()+stringHeight)/2 - 3);
            g2.dispose();
            super.paintComponent(g);
        }
        @Override public void setContentAreaFilled(boolean b) { /* ignore */ }
    }
}
