package cc.joyreactor.views;

import cc.joyreactor.models.UpdateStats;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.IntStream;

import static cc.joyreactor.Updater.THREAD_COUNT;
import static java.lang.String.format;

public class UpdaterView extends JPanel implements PropertyChangeListener {

    private final UpdateStats stats;
    private final ThreadBoxModel threadBoxModel = new ThreadBoxModel();
    private final JComboBox<String> threads = new JComboBox<>(threadBoxModel);
    private JLabel newUsersLabel;
    private JLabel newCommentsLabel;
    private JLabel newRatingLabel;
    private JLabel newPostsLabel;

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

        threads.setFont(threads.getFont().deriveFont(14.0f));
        //threads.setPrototypeDisplayValue("XX - XX:XX:XX.XXX - XXXXXXXXXXXXXXXXXXXXXXX - XXXXXX");
        add(threads);
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
}
