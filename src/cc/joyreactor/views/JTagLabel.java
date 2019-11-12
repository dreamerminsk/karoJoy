package cc.joyreactor.views;

import cc.joyreactor.data.Tag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class JTagLabel extends JLabel implements MouseListener {

    private final Tag tag;

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