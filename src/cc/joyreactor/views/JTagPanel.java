package cc.joyreactor.views;

import cc.joyreactor.data.Tag;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class JTagPanel extends JPanel {

    public static final int MS = 64;
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
                SwingUtilities.invokeLater(() -> {
                    ((JTagLabel) getComponent(i)).setTag(newTags.get(i));
                    revalidate();
                    repaint();
                });
                pause(MS);
            });

            int comps = getComponentCount();
            IntStream.range(comps, newTags.size()).mapToObj(i ->
            {
                revalidate();
                repaint();
                pause(MS);
                return new JTagLabel(newTags.get(i));
            }).forEachOrdered(this::add);
        } else {
            IntStream.range(0, getComponentCount()).forEachOrdered(i ->
            {
                ((JTagLabel) getComponent(i)).setTag(newTags.get(i));
                revalidate();
                repaint();
                pause(MS);
            });

            int components = getComponentCount();
            IntStream.range(newTags.size(), components).mapToObj(i -> getComponent(newTags.size())).forEachOrdered(this::remove);
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
