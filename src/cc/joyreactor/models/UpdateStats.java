package cc.joyreactor.models;

import cc.joyreactor.data.Post;
import cc.joyreactor.utils.Integers;
import cc.joyreactor.utils.Strings;
import org.apache.commons.lang3.time.DurationFormatUtils;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static cc.joyreactor.Updater.THREAD_COUNT;

public class UpdateStats extends AbstractTableModel {

    private final AtomicLong newUsers = new AtomicLong(0);
    private final AtomicLong newPosts = new AtomicLong(0);
    private final AtomicLong newComments = new AtomicLong(0);
    private final AtomicReference<BigDecimal> newRating = new AtomicReference<>(new BigDecimal(0));
    private ScheduledExecutorService SES = Executors.newScheduledThreadPool(1);
    private PropertyChangeSupport changes = new PropertyChangeSupport(this);

    private ConcurrentSkipListMap<Integer, Thread> threadList = new ConcurrentSkipListMap<>();
    private ConcurrentSkipListMap<Integer, Instant> startedList = new ConcurrentSkipListMap<>();
    private ConcurrentSkipListMap<Integer, String> refList = new ConcurrentSkipListMap<>();
    private ConcurrentSkipListMap<Integer, String> tagList = new ConcurrentSkipListMap<>();

    private ConcurrentSkipListMap<LocalDate, Long> pubs = new ConcurrentSkipListMap<>();
    private AbstractTableModel pubTableModel = new AbstractTableModel() {
        @Override
        public int getRowCount() {
            return pubs.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return pubs.keySet().stream().skip(rowIndex).findFirst().orElse(LocalDate.MIN);
            } else if (columnIndex == 1) {
                return pubs.values().stream().skip(rowIndex).findFirst().orElse(0L);
            }
            return null;
        }

    };


    public UpdateStats() {
        IntStream.range(0, THREAD_COUNT).forEach(i -> {
            threadList.put(i + 1, Thread.currentThread());
            startedList.put(i + 1, Instant.now());
            refList.put(i + 1, "");
            tagList.put(i + 1, "");
        });
        SES.scheduleAtFixedRate(() -> {
            IntStream.range(0, THREAD_COUNT).forEach(i -> {
                fireTableCellUpdated(i, 3);
                fireTableCellUpdated(i, 4);
            });
        }, 768, 768, TimeUnit.MILLISECONDS);
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

    public void startTask(Thread currentThread, Instant started, String ref, String tag) {
        int row = Integers.of(Strings.getLastSplitComponent(currentThread.getName(), "-"), 0);
        threadList.put(row, currentThread);
        startedList.put(row, started);
        refList.put(row, ref);
        tagList.put(row, tag);
        fireTableRowsUpdated(row, row);
    }

    @Override
    public int getRowCount() {
        return threadList.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return threadList.get(rowIndex + 1).getName();
        } else if (columnIndex == 1) {
            return tagList.get(rowIndex + 1);
        } else if (columnIndex == 2) {
            return Strings.getLastSplitComponent(refList.get(rowIndex + 1), "/");
        } else if (columnIndex == 3) {
            return threadList.get(rowIndex + 1).getState();
        } else if (columnIndex == 4) {
            return DurationFormatUtils.formatDuration(
                    Duration.between(startedList.get(rowIndex + 1).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                            Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime()).toMillis(),
                    "**HH:mm:ss**", true);
        }
        return null;
    }

    public void processed(Post item) {
        if (pubs.containsKey(item.getPublished().toLocalDate())) {
            pubs.put(item.getPublished().toLocalDate(), pubs.get(item.getPublished().toLocalDate()) + 1);
        } else {
            pubs.put(item.getPublished().toLocalDate(), 1L);
        }
        pubTableModel.fireTableDataChanged();
    }

    public TableModel getPubTableModel() {
        return pubTableModel;
    }
}

