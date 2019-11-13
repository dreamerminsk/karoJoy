package cc.joyreactor.views;

import cc.joyreactor.data.Tag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class JTagLabel extends JLabel implements MouseListener {

    private Tag tag;

    public JTagLabel(Tag tag) {
        super();
        this.tag = tag;
        initUI();
    }

    private void initUI() {
        setText(" " + tag.getTag() + " ");
        setFont(getFont().deriveFont(Font.ITALIC, 16.0f));
        setBorder(UIManager.getBorder("ScrollPane.border"));
        addMouseListener(this);
    }

    public void setTag(Tag tag) {
        this.tag = tag;
        update();
    }

    private void update() {
        SwingUtilities.invokeLater(() -> {
            setText(" " + tag.getTag() + " ");
            revalidate();
            repaint();
        });
    }

    private void updateText(String text) {
        String oldText = getText();
        if (oldText.equals(text)) return;
        for (int i = 0; i < text.length(); i++) {
            if (i < getText().length()) {
                char[] chars = getText().toCharArray();
                chars[i] = text.charAt(i);
                SwingUtilities.invokeLater(() -> setText(new String(chars)));
            } else {
                int finalI = i;
                SwingUtilities.invokeLater(() -> setText(getText() + text.charAt(finalI)));
            }
            SwingUtilities.invokeLater(() -> {
                revalidate();
                repaint();
            });
            try {
                Thread.sleep(64);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (oldText.length() > text.length()) {
            for (int i = 0; i < (oldText.length() - text.length()); i++) {
                SwingUtilities.invokeLater(() -> {
                    setText(getText().substring(0, getText().length() - 1));
                    revalidate();
                    repaint();
                });
                try {
                    Thread.sleep(64);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setForeground(Color.BLUE);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setForeground(Color.BLACK);
    }
}
