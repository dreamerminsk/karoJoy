package cc.joyreactor.models;

import cc.joyreactor.data.Post;
import cc.joyreactor.utils.Integers;
import cc.joyreactor.utils.Strings;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jdesktop.swingx.JXErrorPane;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
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
    private PublishedTreeTableModel pubTableModel = new PublishedTreeTableModel();

    private ConcurrentHashMultiset<String> tagStats = ConcurrentHashMultiset.create();
    private AbstractTableModel tagTableModel = new AbstractTableModel() {
        @Override
        public int getRowCount() {
            return tagStats.elementSet().size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            List<Multiset.Entry<String>> tags = tagStats.entrySet().stream()
                    .sorted(Comparator.comparingInt(Multiset.Entry::getCount)).collect(Collectors.toList());
            Collections.reverse(tags);
            if (columnIndex == 0) {
                return tags.get(rowIndex);
            } else if (columnIndex == 1) {
                return tagStats.count(tags.get(rowIndex).getElement());
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
                SwingUtilities.invokeLater(() -> {
                    fireTableCellUpdated(i, 3);
                    fireTableCellUpdated(i, 4);
                });
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
        try {
            int row = Integers.of(Strings.getLastSplitComponent(currentThread.getName(), "-"), 0);
            threadList.put(row - 1, currentThread);
            startedList.put(row - 1, started);
            refList.put(row - 1, ref);
            tagList.put(row - 1, tag);
            fireTableRowsUpdated(row - 1, row - 1);
        } catch (Exception e) {
            JXErrorPane.showDialog(e);
        }
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
        try {
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
        } catch (Exception e) {
            JXErrorPane.showDialog(e);
        }
        return null;
    }

    public void processed(Post item) {
        try {
            pubTableModel.updateStats(item.getPublished());
            item.getTags().forEach(t -> tagStats.add(t.getTag()));
            List<Multiset.Entry<String>> tags = tagStats.entrySet().stream()
                    .sorted(Comparator.comparingInt(Multiset.Entry::getCount)).collect(Collectors.toList());
            Collections.reverse(tags);
            for (int i = 0; i < tags.size(); i++) {
                for (int j = 0; j < item.getTags().size(); j++) {
                    if (Objects.equals(tags.get(i).getElement(), item.getTags().get(j).getTag())) {
                        tagTableModel.fireTableRowsUpdated(i, i);
                    }
                }
            }
        } catch (Exception e) {
            JXErrorPane.showDialog(e);
        }
    }

    public PublishedTreeTableModel getPubTableModel() {
        return pubTableModel;
    }

    public AbstractTableModel getTagTableModel() {
        return tagTableModel;
    }
}

