package cc.joyreactor.views;

import cc.joyreactor.data.Tag;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class JTagPanel extends JPanel {

    private final List<Tag> tags = new ArrayList<>();

    private final Map<String, JTagLabel> tagLabels = new TreeMap<>();

    public JTagPanel(List<Tag> tags) {
        super(new FlowLayout(FlowLayout.LEFT));
        tags.addAll(tags);
    }

    public update() {

    }

}
