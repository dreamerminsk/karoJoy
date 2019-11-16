package cc.joyreactor.models;

import cc.joyreactor.utils.Strings;

import javax.swing.table.AbstractTableModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static cc.joyreactor.Updater.THREAD_COUNT;

public class UpdateStats extends AbstractTableModel {

    private final AtomicLong newUsers = new AtomicLong(0);
    private final AtomicLong newPosts = new AtomicLong(0);
    private final AtomicLong newComments = new AtomicLong(0);
    private final AtomicReference<BigDecimal> newRating = new AtomicReference<>(new BigDecimal(0));
    private List<String> threads = new ArrayList<>();

    private PropertyChangeSupport changes = new PropertyChangeSupport(this);
    private ConcurrentSkipListMap<Thread, Map.Entry<String, String>> tasks = new ConcurrentSkipListMap<>(
            Comparator.comparing(Thread::getName));

    public UpdateStats() {
        IntStream.range(0, THREAD_COUNT).forEach(i -> threads.add(""));
    }

    public long incUsers() {
        long newValue = newUsers.incrementAndGet();
        changes.firePropertyChange("newUsers", newValue - 1, newValue);
        return newValue;
    }

    public long addUsers(long delta) {
        long newValue = newUsers.addAndGet(delta);
        changes.firePropertyChange("newUsers", newValue - delta, newValue);
        return newValue;
    }

    public long incPosts() {
        long newValue = newPosts.incrementAndGet();
        changes.firePropertyChange("newPosts", newValue - 1, newValue);
        return newValue;
    }

    public long incComments() {
        long newValue = newUsers.incrementAndGet();
        changes.firePropertyChange("newComments", newValue - 1, newValue);
        return newValue;
    }

    public long addComments(long delta) {
        long newValue = newComments.addAndGet(delta);
        changes.firePropertyChange("newComments", newValue - delta, newValue);
        return newValue;
    }

    public BigDecimal addRating(BigDecimal delta) {
        BigDecimal newValue = newRating.updateAndGet(bigDecimal -> bigDecimal.add(delta));
        changes.firePropertyChange("newRating", newValue.subtract(delta), newValue);
        return newValue;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        changes.removePropertyChangeListener(l);
    }

    public void addThread(int index, String thread) {
        if (index == 0) return;
        threads.set(index - 1, thread);
        changes.firePropertyChange("threads",
                index,
                Collections.unmodifiableList(threads));
    }

    public void startTask(Thread currentThread, Map.Entry<String, String> tagRef) {
        tasks.put(currentThread, tagRef);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return tasks.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return tasks.keySet().stream().skip(rowIndex).map(Thread::getName).findFirst().orElse("");
        } else if (columnIndex == 1) {
            return tasks.values().stream().skip(rowIndex).findFirst().map(Map.Entry::getKey).orElse("");
        } else if (columnIndex == 2) {
            return tasks.values().stream().skip(rowIndex).findFirst()
                    .map(Map.Entry::getValue)
                    .map(item -> Strings.getLastSplitComponent(item, "/")).orElse("");
        } else if (columnIndex == 3) {
            return tasks.keySet().stream().skip(rowIndex).map(Thread::getState).map(Enum::toString).findFirst().orElse("");
        }
        return null;
    }
}

