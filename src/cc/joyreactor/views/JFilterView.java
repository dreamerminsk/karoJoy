package cc.joyreactor.views;

import cc.joyreactor.data.Tag;
import org.jdesktop.swingx.WrapLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JFilterView extends JPanel implements MouseListener {
    private final List<Tag> filterTags;

    public JFilterView(List<Tag> filterTags) {
        super(new WrapLayout());
        this.filterTags = filterTags;
    }

    private void update() {
        removeAll();
        filterTags.forEach(t -> {
            JLabel lt = new JLabel(" " + t.getTag() + " ");
            lt.setFont(lt.getFont().deriveFont(Font.ITALIC, 18.0f));
            lt.setBorder(UIManager.getBorder("ScrollPane.border"));
            lt.addMouseListener(this);
            add(lt);
        });
        revalidate();
        repaint();
    }

    public List<Tag> getFilterTags() {
        return Collections.unmodifiableList(filterTags);
    }

    public void add(Tag tag) {
        List<Tag> tags = filterTags.stream().filter(t ->
                t.getTag().equalsIgnoreCase(tag.getTag())).collect(Collectors.toList());
        if (tags.isEmpty()) {
            filterTags.add(tag);
            update();
        }
    }

    public void removeTag(Tag tag) {
        filterTags.remove(tag);
        update();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object source = e.getSource();
        if (source.getClass().isAssignableFrom(JLabel.class)) {
            JLabel label = (JLabel) source;
            List<Tag> tags = filterTags.stream()
                    .filter(t -> t.getTag().equalsIgnoreCase(label.getText())).collect(Collectors.toList());
            tags.forEach(filterTags::remove);
            update();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
