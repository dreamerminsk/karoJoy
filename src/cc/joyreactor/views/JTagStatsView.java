package cc.joyreactor.views;

import cc.joyreactor.Source;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
        table.setAutoCreateRowSorter(true);
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

        JButton lastMonth = new JButton("last month");
        lastMonth.addActionListener((e) -> CompletableFuture.supplyAsync(() ->
                source.getLastMonthTags()).thenAcceptAsync((Map<String, BigDecimal> tags) ->
                SwingUtilities.invokeLater(() -> table.setModel(new TagsModel(tags)))));
        group.add(lastMonth);
        panel.add(lastMonth);

        JButton lastYear = new JButton("last year");
        lastYear.addActionListener((e) -> CompletableFuture.supplyAsync(() ->
                source.getLastYearTags()).thenAcceptAsync((Map<String, BigDecimal> tags) ->
                SwingUtilities.invokeLater(() -> table.setModel(new TagsModel(tags)))));
        group.add(lastYear);
        panel.add(lastYear);

        lastDay.doClick();
        return panel;
    }

    public static class TagsModel extends AbstractTableModel {

        private final Map<String, BigDecimal> tags = new HashMap<>();

        public TagsModel(Map<String, BigDecimal> tags) {
            List<BigDecimal> values = tags.values().stream().sorted().collect(Collectors.toList());
            values.forEach(value -> tags.entrySet().stream().filter((tag) -> tag.getValue().equals(value)).forEachOrdered(item -> {
                this.tags.put(item.getKey(), item.getValue());
            }));
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
