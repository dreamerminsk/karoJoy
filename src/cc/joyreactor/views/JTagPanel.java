package cc.joyreactor.views;

import cc.joyreactor.data.Tag;
import cc.joyreactor.events.TagListener;
import com.alee.extended.layout.HorizontalFlowLayout;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class JTagPanel extends JPanel implements TagListener {

    private List<TagListener> listeners = new ArrayList<>();

    public JTagPanel(List<Tag> tags) {
        super(new HorizontalFlowLayout(4, false));
        update(tags);
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
                    new JTagLabel(newTags.get(i)))
                    .peek(l -> l.addTagListener(this))
                    .forEachOrdered(this::add);
        } else {
            IntStream.range(0, newTags.size()).forEachOrdered(i ->
                    ((JTagLabel) getComponent(i)).setTag(newTags.get(i)));

            int components = getComponentCount();
            IntStream.range(newTags.size(), components).mapToObj(i -> getComponent(newTags.size())).forEachOrdered(this::remove);
        }
    }

    public void addTagListener(TagListener listener) {
        listeners.add(listener);
    }

    public void removeTagListener(TagListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void tagSelected(Tag tag) {
        listeners.forEach(l -> l.tagSelected(tag));
    }
}
