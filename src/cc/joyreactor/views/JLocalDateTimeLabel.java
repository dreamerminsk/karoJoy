package cc.joyreactor.views;

import javax.swing.*;
import java.awt.*;

public class JLocalDateTimeLabel extends JLabel {

    private Color extraColor = Color.green;

    private int selectedStart;

    private int selectedLength;

    public JLocalDateTimeLabel(String text) {
        super(text);
    }

    public void setColor(Color color) {
        extraColor = color;
    }

    protected void paintComponent(Graphics g) {
        String text = this.getText();

        if (text == null || text.length() == 0) {
            super.paintComponent(g);
            return;
        }

        char[] chars = text.toCharArray();

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (this.isOpaque()) {
            g2d.setColor(this.getBackground());
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        }

        FontMetrics fm = g2d.getFontMetrics();

        Insets ins = this.getInsets();

        int offset = ins.left + 1;

        g2d.setColor(this.getForeground());

        for (int i = 0; i < chars.length; i++) {
            if (i >= selectedStart && i < (selectedStart + selectedLength)) {
                g2d.setColor(extraColor);
            } else {
                g2d.setColor(this.getForeground());
            }

            g2d.drawString(String.valueOf(chars[i]), offset, (fm.getHeight() + ins.top));

            offset += fm.charWidth(chars[i]);
        }
    }
}
