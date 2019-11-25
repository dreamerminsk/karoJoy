package cc.joyreactor.views;

import cc.joyreactor.data.Tag;
import org.jdesktop.swingx.WrapLayout;

import javax.swing.*;
import java.util.List;

public class JFilterView extends JPanel {
    private final List<Tag> filterTags;

    public JFilterView(List<Tag> filterTags) {
        super(new WrapLayout());
        this.filterTags = filterTags;
    }

    private void update() {
        removeAll();
        filterTags.forEach(t -> {
            JLabel lt = new JLabel(t.getTag());
            add(lt);
        });
        revalidate();
        repaint();
    }

    public void add(Tag tag) {
        filterTags.add(tag);
        update();
    }

    public void removeTag(Tag tag) {
        filterTags.remove(tag);
    }
}
