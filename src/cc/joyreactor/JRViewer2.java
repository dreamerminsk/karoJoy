package cc.joyreactor;

import cc.joyreactor.models.UpdateStats;
import cc.joyreactor.views.ScrollView;
import cc.joyreactor.views.UpdaterView;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.SQLException;

public class JRViewer2 extends JFrame implements PropertyChangeListener {

    private static final String TITLE = "karoJoy";

    private static final String VERSION = "v2019-11-17";
    private static Updater updater;

    private final UpdateStats stats = new UpdateStats();

    private JRViewer2() throws SQLException, IOException {
        super(TITLE + " " + VERSION);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension(screen.width * 9 / 10, screen.height * 9 / 10));
        setLocationRelativeTo(null);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupUi();
        setVisible(true);
    }

    public static void main(String... args) throws SQLException, IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JRViewer2 jrViewer = new JRViewer2();
        updater = new Updater(jrViewer.stats);
    }

    private void setupUi() throws SQLException, IOException {
        add(new ScrollView(), BorderLayout.CENTER);
        UpdaterView updaterView = new UpdaterView(stats);
        updaterView.addPropertyChangeListener(this);
        add(new JScrollPane(updaterView), BorderLayout.PAGE_END);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("start")) {
            if ((boolean) evt.getNewValue()) {
                updater.execute();
            }
        }
    }
}
