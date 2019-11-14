package cc.joyreactor.views;

import cc.joyreactor.Source;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public class JTagStatsView extends JPanel {

    private Source source;
    private JTable table;

    public JTagStatsView() {
        super(new BorderLayout());
        try {
            source = Source.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setupUi();
    }

    public void setupUi() {
        add(getMenu(), BorderLayout.PAGE_START);
        table = new JTable(new TagsModel(new TreeMap<>()));
        table.setAutoCreateColumnsFromModel(true);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private JComponent getMenu() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ButtonGroup group = new ButtonGroup();

        JButton lastDay = new JButton("last day");
        lastDay.addActionListener((e) -> CompletableFuture.supplyAsync(() ->
                source.getLastDayTags()).thenAcceptAsync((Map<String, BigDecimal> tags) ->
                SwingUtilities.invokeLater(() -> table.setModel(new TagsModel(tags)))));
        group.add(lastDay);
        panel.add(lastDay);

        JButton lastWeek = new JButton("last week");
        lastWeek.addActionListener((e) -> CompletableFuture.supplyAsync(() ->
                source.getLastWeekTags()).thenAcceptAsync((Map<String, BigDecimal> tags) ->
                SwingUtilities.invokeLater(() -> table.setModel(new TagsModel(tags)))));
        group.add(lastWeek);
        panel.add(lastWeek);

        lastWeek.doClick();
        return panel;
    }

    private void update() {
        try {
            Map<String, BigDecimal> tags = Source.getInstance().getThisMonthTags();
            table.setModel(new TagsModel(tags));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class TagsModel extends AbstractTableModel {

        private final Map<String, BigDecimal> tags;

        public TagsModel(Map<String, BigDecimal> tags) {
            this.tags = tags;
        }

        @Override
        public int getRowCount() {
            return tags.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return String.class;
            } else {
                return Float.class;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return tags.entrySet().stream().skip(rowIndex).map(Map.Entry::getKey).findFirst().orElse("");
            } else if (columnIndex == 1) {
                return tags.entrySet().stream().skip(rowIndex).map(Map.Entry::getValue).findFirst().orElse(new BigDecimal("0.0")).floatValue();
            } else {
                return "";
            }
        }
    }

}
