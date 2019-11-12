package cc.joyreactor.views;

import cc.joyreactor.data.Tag;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class JTagPanel extends JPanel {

    private final List<Tag> tags = new ArrayList<>();

    //private final List<JTagLabel> tagLabels = new ArrayList<>();

    public JTagPanel(List<Tag> tags) {
        super(new FlowLayout(FlowLayout.LEFT));
        tags.addAll(tags);
    }

    public void setTags(List<Tag> tags) {
        tags.clear();
        tags.addAll(tags);
        update();
    }

    public void update() {
        int minLength = Math.min(tags.size(), getComponentCount());
        IntStream.range(0, minLength).forEachOrdered(i -> ((JTagLabel) getComponent(i)).setTag(tags.get(i)));
        int components = getComponentCount();
        IntStream.range(minLength, components).map(i -> minLength).forEachOrdered(this::remove);
        IntStream.range(minLength, tags.size()).mapToObj(i -> new JTagLabel(tags.get(i))).forEachOrdered(this::add);
    }

}
