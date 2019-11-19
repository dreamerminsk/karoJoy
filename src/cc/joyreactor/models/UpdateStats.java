package cc.joyreactor.models;

import cc.joyreactor.data.Post;
import cc.joyreactor.utils.Integers;
import cc.joyreactor.utils.Strings;
import com.google.common.collect.ConcurrentHashMultiset;
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

    private ConcurrentHashMultiset<LocalDate> pubs = ConcurrentHashMultiset.create();
    private AbstractTableModel pubTableModel = new AbstractTableModel() {
        @Override
        public int getRowCount() {
            return pubs.elementSet().size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            LocalDate day = pubs.elementSet().stream().skip(rowIndex).findFirst().orElse(LocalDate.MIN);
            if (columnIndex == 0) {
                return day;
            } else if (columnIndex == 1) {
                return pubs.count(day);
            }
            return null;
        }

    };


    public UpdateStats() {
        IntStream.range(0, THREAD_COUNT).forEach(i -> {
            threadList.put(i, Thread.currentThread());
            startedList.put(i, Instant.now());
            refList.put(i, "");
            tagList.put(i, "");
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
        threadList.put(row - 1, currentThread);
        startedList.put(row - 1, started);
        refList.put(row - 1, ref);
        tagList.put(row - 1, tag);
        fireTableRowsUpdated(row - 1, row - 1);
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
            return threadList.get(rowIndex).getName();
        } else if (columnIndex == 1) {
            return tagList.get(rowIndex);
        } else if (columnIndex == 2) {
            return Strings.getLastSplitComponent(refList.get(rowIndex), "/");
        } else if (columnIndex == 3) {
            return threadList.get(rowIndex).getState();
        } else if (columnIndex == 4) {
            return DurationFormatUtils.formatDuration(
                    Duration.between(startedList.get(rowIndex).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                            Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime()).toMillis(),
                    "**HH:mm:ss**", true);
        }
        return null;
    }

    public void processed(Post item) {
        pubs.add(item.getPublished().toLocalDate());
        pubTableModel.fireTableDataChanged();
    }

    public TableModel getPubTableModel() {
        return pubTableModel;
    }
}

