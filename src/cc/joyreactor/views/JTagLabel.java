package cc.joyreactor.views;

import cc.joyreactor.data.Tag;
import cc.joyreactor.events.TagListener;
import com.alee.extended.label.WebStyledLabel;
import com.alee.managers.style.StyleId;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JTagLabel extends WebStyledLabel implements MouseListener {

    private List<TagListener> listeners = new ArrayList<>();

    private Tag tag;

    public JTagLabel(Tag tag) {
        super(StyleId.styledlabelTag);
        this.tag = tag;
        initUI();
    }

    private void initUI() {
        setText(tag.getTag() + " ");
        setFont(getFont().deriveFont(Font.ITALIC, 16.0f));
        addMouseListener(this);
    }

    public void setTag(Tag tag) {
        this.tag = tag;
        update();
    }

    private void update() {
        SwingUtilities.invokeLater(() -> {
            setText(tag.getTag() + " ");
            BufferedImage icon = null;
            try {
                if (tag.getAvatar() != null) {
                    icon = ImageIO.read(new ByteArrayInputStream(tag.getAvatar()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (tag.getAvatar() != null) {
                setIcon(new ImageIcon(icon));
            }
            revalidate();
            repaint();
        });
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        listeners.forEach(l -> l.tagSelected(tag));
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

    public void addTagListener(TagListener listener) {
        listeners.add(listener);
    }

    public void removeTagListener(TagListener listener) {
        listeners.remove(listener);
    }
}
