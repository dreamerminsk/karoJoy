package cc.joyreactor.models;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import javax.swing.tree.TreePath;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PublishedTreeTableModel extends AbstractTreeTableModel {

    private List<Year> years = new ArrayList<>();

    private Map<LocalDate, Long> dayStats = new TreeMap<>();

    public PublishedTreeTableModel() {
        super("ROOT");
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    public Class<?> getColumnClass(int column) {
        if (column == 0) {
            return String.class;
        }
        if (column == 1) {
            return Long.class;
        }
        return Object.class;
    }

    @Override
    public Object getValueAt(Object node, int column) {
        if (node.equals(root)) {
            return null;
        } else if (node.getClass().isAssignableFrom(Year.class)) {
            Year y = (Year) node;
            if (column == 0) {
                return y.getValue();
            } else {
                return dayStats.entrySet().stream().filter(dayStat -> dayStat.getKey().getYear() == y.getValue())
                        .mapToLong(Map.Entry::getValue).reduce(0, Long::sum);
            }
        } else if (node.getClass().isAssignableFrom(YearMonth.class)) {
            YearMonth ym = (YearMonth) node;
            if (column == 0) {
                return ym.getMonthValue() + ". " + ym.getMonth().name();
            } else {
                return dayStats.entrySet().stream()
                        .filter(dayStat -> dayStat.getKey().getYear() == ym.getYear())
                        .filter(dayStat -> dayStat.getKey().getMonth() == ym.getMonth())
                        .mapToLong(Map.Entry::getValue).reduce(0, Long::sum);
            }
        } else if (node.getClass().isAssignableFrom(LocalDate.class)) {
            LocalDate ld = (LocalDate) node;
            if (column == 0) {
                return ld.getDayOfMonth();
            } else {
                return dayStats.get(ld);
            }
        }
        return null;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent.equals(root)) {
            return years.get(index);
        } else if (parent.getClass().isAssignableFrom(Year.class)) {
            Year y = (Year) parent;
            return y.atMonth(index + 1);
        } else if (parent.getClass().isAssignableFrom(YearMonth.class)) {
            YearMonth ym = (YearMonth) parent;
            if (ym.isValidDay(index + 1)) {
                return ym.atDay(index + 1);
            }
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent.equals(root)) {
            return years.size();
        } else if (parent.getClass().isAssignableFrom(Year.class)) {
            return Month.values().length;
        } else if (parent.getClass().isAssignableFrom(YearMonth.class)) {
            YearMonth ym = (YearMonth) parent;
            return ym.lengthOfMonth();
        }
        return 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent.equals(root)) {
            return years.indexOf(child);
        } else if (parent.getClass().isAssignableFrom(Year.class)) {
            YearMonth ym = (YearMonth) child;
            return ym.getMonthValue() - 1;
        } else if (parent.getClass().isAssignableFrom(YearMonth.class)) {
            LocalDate ymd = (LocalDate) child;
            return ymd.getDayOfMonth() - 1;
        }
        return 0;
    }

    public void updateStats(ZonedDateTime day) {
        if (dayStats.containsKey(day.toLocalDate())) {
            dayStats.put(day.toLocalDate(), dayStats.get(day.toLocalDate()) + 1);
        } else {
            dayStats.put(day.toLocalDate(), 1L);
        }
        List<Year> fy = years.stream().filter(y -> y.getValue() == day.getYear()).collect(Collectors.toList());
        if (fy.size() == 0) {
            years.add(Year.of(day.getYear()));
            modelSupport.fireChildAdded(new TreePath(root), years
                    .indexOf(Year.of(day.getYear())), Year.of(day.getYear()));
        } else {
            modelSupport.fireChildChanged(new TreePath(root), years
                    .indexOf(Year.of(day.getYear())), Year.of(day.getYear()));
        }
    }
}
