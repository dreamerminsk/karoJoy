package cc.joyreactor;

import cc.joyreactor.models.PostsModel;
import cc.joyreactor.models.UpdateStats;
import cc.joyreactor.views.PostsView;
import cc.joyreactor.views.UpdaterView;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public class JRViewer extends JFrame {

    private static final String TITLE = "karoJoy";

    private static final String VERSION = "v2019-11-11";

    private final UpdateStats stats = new UpdateStats();

    private JRViewer() throws SQLException, IOException {
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
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JRViewer jrViewer = new JRViewer();
        Updater updater = new Updater(jrViewer.stats);
        updater.execute();
    }

    private void setupUi() throws SQLException, IOException {
        add(new PostsView(new PostsModel()), BorderLayout.CENTER);
        add(new JScrollPane(new UpdaterView(stats)), BorderLayout.PAGE_END);
    }

}
