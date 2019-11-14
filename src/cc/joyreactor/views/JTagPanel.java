package cc.joyreactor.views;

import cc.joyreactor.data.Tag;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.IntStream;

public class JTagPanel extends JPanel {

    public JTagPanel(List<Tag> tags) {
        super(new FlowLayout(FlowLayout.LEFT));
        update(tags);
        setBorder(UIManager.getBorder("ScrollPane.border"));
    }

    public void setTags(List<Tag> tags) {
        update(tags);
        revalidate();
        repaint();
    }

    public void update(List<Tag> newTags) {
        if (newTags.size() >= getComponentCount()) {
            IntStream.range(0, getComponentCount()).forEachOrdered(i ->
                    SwingUtilities.invokeLater(() -> ((JTagLabel) getComponent(i)).setTag(newTags.get(i))));

            int comps = getComponentCount();
            IntStream.range(comps, newTags.size()).mapToObj(i ->
                    new JTagLabel(newTags.get(i))).forEachOrdered(this::add);
        } else {
            IntStream.range(0, newTags.size()).forEachOrdered(i ->
                    ((JTagLabel) getComponent(i)).setTag(newTags.get(i)));

            int components = getComponentCount();
            IntStream.range(newTags.size(), components).mapToObj(i -> getComponent(newTags.size())).forEachOrdered(this::remove);
        }
    }

}
