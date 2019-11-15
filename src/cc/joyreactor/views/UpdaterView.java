package cc.joyreactor.views;

import cc.joyreactor.JRViewer;
import cc.joyreactor.models.UpdateStats;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static cc.joyreactor.Updater.THREAD_COUNT;
import static java.lang.String.format;

public class UpdaterView extends JPanel implements PropertyChangeListener {

    private final UpdateStats stats;
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final ThreadBoxModel threadBoxModel = new ThreadBoxModel();
    private PropertyChangeSupport changes = new PropertyChangeSupport(this);
    private JLabel newUsersLabel;
    private JLabel newCommentsLabel;
    private JLabel newRatingLabel;
    private JLabel newPostsLabel;
    private JLabel tasks;

    public UpdaterView(UpdateStats stats) {
        super(new FlowLayout(FlowLayout.LEFT));
        this.stats = stats;
        this.stats.addPropertyChangeListener(this);
        ui();
    }

    private void ui() {
        newUsersLabel = new JLabel(" USERS: 0 ");
        newUsersLabel.getInsets();
        newUsersLabel.setBorder(UIManager.getBorder("ScrollPane.border"));
        newUsersLabel.setFont(newUsersLabel.getFont().deriveFont(14.0f));
        add(newUsersLabel);

        newPostsLabel = new JLabel(" POSTS: 0 ");
        newPostsLabel.setBorder(UIManager.getBorder("ScrollPane.border"));
        newPostsLabel.setFont(newPostsLabel.getFont().deriveFont(14.0f));
        add(newPostsLabel);

        newCommentsLabel = new JLabel(" COMMENTS: 0 ");
        newCommentsLabel.setBorder(UIManager.getBorder("ScrollPane.border"));
        newCommentsLabel.setFont(newCommentsLabel.getFont().deriveFont(14.0f));
        add(newCommentsLabel);

        newRatingLabel = new JLabel(" RATING: 0 ");
        newRatingLabel.setBorder(UIManager.getBorder("ScrollPane.border"));
        newRatingLabel.setFont(newRatingLabel.getFont().deriveFont(14.0f));
        add(newRatingLabel);

        tasks = new JLabel(" TASKS: 0 ");
        tasks.setBorder(UIManager.getBorder("ScrollPane.border"));
        tasks.setFont(tasks.getFont().deriveFont(14.0f));
        tasks.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel panel = new JPanel(new BorderLayout());
                JScrollPane jScrollPane = new JScrollPane(new JTable(5, 5));
                panel.add(jScrollPane, BorderLayout.CENTER);

                PopupFactory pf = PopupFactory.getSharedInstance();
                Container parent = UpdaterView.this;
                do {
                    if (parent instanceof JRViewer) {
                        break;
                    }
                    parent = parent.getParent();

                } while (parent != null);

                Point loc = parent.getLocationOnScreen();
                MessagePopup mp = new MessagePopup((Frame) parent,
                        loc.x + parent.getWidth() / 3,
                        loc.y + parent.getHeight() / 3);
                mp.show();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        add(tasks);

        JButton startButton = new JButton("START");
        add(startButton);
        startButton.addActionListener((e) ->
        {
            boolean old = isStarted.getAndSet(!isStarted.get());
            changes.firePropertyChange("start", old, isStarted.get());
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equalsIgnoreCase("newUsers")) {
            SwingUtilities.invokeLater(() -> newUsersLabel.setText(" USERS: " + format("%+,d", evt.getNewValue()) + " "));
        } else if (evt.getPropertyName().equalsIgnoreCase("newPosts")) {
            SwingUtilities.invokeLater(() -> newPostsLabel.setText(" POSTS: " + format("%+,d", evt.getNewValue()) + " "));
        } else if (evt.getPropertyName().equalsIgnoreCase("newComments")) {
            SwingUtilities.invokeLater(() -> newCommentsLabel.setText(" COMMENTS: " + format("%+,d", evt.getNewValue()) + " "));
        } else if (evt.getPropertyName().equalsIgnoreCase("newRating")) {
            SwingUtilities.invokeLater(() -> newRatingLabel.setText(" RATING: " + format("%+-,10.1f", evt.getNewValue()) + " "));
        } else if (evt.getPropertyName().equalsIgnoreCase("threads")) {
            SwingUtilities.invokeLater(() -> threadBoxModel.update((java.util.List<String>) evt.getNewValue()));
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        changes.removePropertyChangeListener(l);
    }


    static class ThreadBoxModel extends AbstractListModel<String> implements ComboBoxModel<String> {

        String selection = null;
        private java.util.List<String> ts = new ArrayList<>();

        public ThreadBoxModel() {
            for (int i = 0; i < THREAD_COUNT; i++) {
                ts.add((i + 1) + " - " + LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
            }
        }

        public String getElementAt(int index) {
            return ts.get(index);
        }

        public int getSize() {
            return ts.size();
        }

        public String getSelectedItem() {
            return selection;
        }

        @Override
        public void setSelectedItem(Object anItem) {
            selection = (String) anItem;
        }

        public void update(java.util.List<String> threads) {
            IntStream.range(0, threads.size()).forEach(i -> ts.set(i, threads.get(i)));
            fireContentsChanged(this, 0, threads.size() - 1);
        }
    }


    private class MessagePopup extends Popup
            implements WindowFocusListener {
        private final JWindow dialog;

        public MessagePopup(Frame base, int x, int y) {
            super();
            dialog = new JWindow(base);
            dialog.setFocusable(true);
            dialog.setLocation(x, y);

            JPanel panel = new JPanel(new BorderLayout());
            JTable jTable = new JTable(UpdaterView.this.stats);
            jTable.setAutoCreateRowSorter(true);
            JScrollPane jScrollPane = new JScrollPane(jTable);
            panel.add(jScrollPane, BorderLayout.CENTER);
            dialog.setContentPane(panel);
            panel.setBorder(new JPopupMenu().getBorder());
            dialog.setSize(panel.getPreferredSize());
        }

        @Override
        public void show() {
            dialog.addWindowFocusListener(this);
            dialog.setVisible(true);
        }

        @Override
        public void hide() {
            dialog.setVisible(false);
            dialog.removeWindowFocusListener(this);
        }

        public void windowGainedFocus(WindowEvent e) {
            // NO-OP
        }

        public void windowLostFocus(WindowEvent e) {
            hide();
        }
    }
}
