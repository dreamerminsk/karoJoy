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
}
