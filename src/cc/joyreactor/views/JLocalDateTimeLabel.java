package cc.joyreactor.views;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.stream.IntStream;

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
        // Get text-contents of Label
        String text = this.getText();

        // No text in the JLabel? -> No risk: super
        if (text == null || text.length() == 0) {
            super.paintComponent(g);
            return;
        }

        // Content Array of characters to paint
        char[] chars = text.toCharArray();

        // Draw nice and smooth
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw background
        if (this.isOpaque()) {
            g2d.setColor(this.getBackground());
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        }

        // FontMetrics to calculate widths and height
        FontMetrics fm = g2d.getFontMetrics();

        // Available space
        Insets ins = this.getInsets();
        int maxSpace = this.getWidth() - (ins.left + ins.right);
        boolean overflow = (fm.stringWidth(text) > maxSpace);

        // Starting offset
        int offset = ins.left + 1;

        // The start Color is the default
        g2d.setColor(this.getForeground());

        // Loop over characters
        for (int i = 0; i < chars.length; i++) {
            // Switch Color?
            if (i >= selectedStart || i < (selectedStart + selectedLength)) {
                g2d.setColor(extraColor);
            } else {
                g2d.setColor(this.getForeground());
            }

            g2d.drawString(String.valueOf(chars[i]), offset, (fm.getHeight() + ins.top));

            // Move cursor to the next horizontal position
            offset += fm.charWidth(chars[i]);
        }
    }
}
