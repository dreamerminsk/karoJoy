package cc.joyreactor.views;

import cc.joyreactor.Source;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

public class JTagStatsView extends JPanel {

    private JTable table;

    public JTagStatsView() {
        super(new BorderLayout());
        setupUi();
        update();
    }

    public void setupUi() {
        table = new JTable(10, 2);
        add(new JScrollPane(table));
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

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return tags.entrySet().stream().skip(rowIndex).map(Map.Entry::getKey).findFirst().orElse("");
            } else if (columnIndex == 1) {
                return tags.entrySet().stream().skip(rowIndex).map(Map.Entry::getValue).findFirst().orElse(new BigDecimal("0.0"));
            } else {
                return "";
            }
        }
    }

}
