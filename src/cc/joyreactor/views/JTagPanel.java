package cc.joyreactor.views;

import cc.joyreactor.data.Tag;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class JTagPanel extends JPanel {

    public static final int MS = 512;
    private final List<Tag> tags = new ArrayList<>();

    //private final List<JTagLabel> tagLabels = new ArrayList<>();

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
            {
                tags.set(i, newTags.get(i));
                ((JTagLabel) getComponent(i)).setTag(newTags.get(i));
                pause(MS);
            });

            IntStream.range(getComponentCount(), newTags.size()).mapToObj(i ->
            {
                tags.add(newTags.get(i));
                pause(MS);
                return new JTagLabel(newTags.get(i));
            }).forEachOrdered(this::add);
        } else {
            IntStream.range(0, tags.size()).forEachOrdered(i ->
            {
                tags.set(i, newTags.get(i));
                ((JTagLabel) getComponent(i)).setTag(newTags.get(i));
                pause(MS);
            });

            int components = getComponentCount();
            IntStream.range(tags.size(), components).mapToObj(i -> getComponent(tags.size())).forEachOrdered(this::remove);
        }
    }

    private void pause(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
