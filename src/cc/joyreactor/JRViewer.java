package cc.joyreactor;

import cc.joyreactor.models.PostsModel;
import cc.joyreactor.models.UpdateStats;
import cc.joyreactor.views.PostsView;
import cc.joyreactor.views.UpdaterView;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.window.WebFrame;
import com.alee.managers.notification.NotificationManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.SQLException;

public class JRViewer extends WebFrame implements PropertyChangeListener {

    private static final String TITLE = "karoJoy";

    private static final String VERSION = "v2019-11-27";

    private final UpdateStats stats = new UpdateStats();

    private final ThreadLocal<Updater> updater = new ThreadLocal<>();


    private JRViewer() {
        super(TITLE + " " + VERSION);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension(screen.width * 9 / 10, screen.height * 9 / 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            setupUi();
            updater.set(new Updater(stats));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            NotificationManager.showNotification(e.getMessage());
        }
        setVisible(true);

    }

    public static void main(String... args) {
        SwingUtilities.invokeLater(() -> {
            WebLookAndFeel.install();
            JRViewer jrViewer = new JRViewer();
        });
    }

    private void setupUi() throws SQLException, IOException {
        add(new PostsView(new PostsModel()), BorderLayout.CENTER);
        UpdaterView updaterView = new UpdaterView(stats);
        updaterView.addPropertyChangeListener(this);
        add(new JScrollPane(updaterView), BorderLayout.PAGE_END);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("start")) {
            if ((boolean) evt.getNewValue()) {
                updater.get().execute();
            }
        }
    }
}
