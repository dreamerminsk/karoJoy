package cc.joyreactor.views;

import javax.swing.*;
import java.awt.*;

public class JTagStatsView extends JPanel {

    private JTable table;

    public JTagStatsView() {
        super(new BorderLayout());
        setupUi();
    }

    public void setupUi() {
        table = new JTable(10, 2);
        add(new JScrollPane(table));
    }

}
