package cc.joyreactor.views;

import cc.joyreactor.Source;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ScrollView extends JPanel {

    private final Source source;

    public ScrollView() throws SQLException {
        super(new BorderLayout());
        this.source = Source.getInstance();
        setupUi();
    }

    private void setupUi() {
    }
}
