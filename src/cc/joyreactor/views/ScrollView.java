package cc.joyreactor.views;

import cc.joyreactor.Source;
import cc.joyreactor.data.Post;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ScrollView extends JPanel {

    private final Source source;

    private AtomicReference<Post> post = new AtomicReference<>();
    private AtomicInteger imageIndex = new AtomicInteger(0);
    private JPanel imagePanel;
    private JLabel imageView;
    private JButton prev;
    private JButton next;

    public ScrollView() throws SQLException {
        super(new BorderLayout());
        source = Source.getInstance();
        post.set(source.getLatestPost(Instant.now().atZone(ZoneId.systemDefault())));
        post.get().setImages(source.getPostImages(post.get().getId()));
        setupUi();
        updateUi();
    }

    private void updateUi() {
        int idx = imageIndex.get();
        Post postItem = post.get();
        if (idx < postItem.getImages().size()) {
            try {
                BufferedImage bufferedImage = ImageIO.read(new URL(postItem.getImages().get(idx).getRef()));
                BufferedImage scaledImage = Scalr.resize(bufferedImage,
                        Scalr.Method.ULTRA_QUALITY,
                        Scalr.Mode.AUTOMATIC,
                        700, 700);
                imageView.setIcon(new ImageIcon(scaledImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupUi() {
        getParent2();
        imagePanel = new JPanel(new BorderLayout());
        imageView = new JLabel();
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowPanel.add(imageView);
        imagePanel.add(flowPanel, BorderLayout.CENTER);

        prev = new JButton("<");
        prev.setFont(new Font("Georgia", Font.PLAIN, 72));
        add(prev, BorderLayout.WEST);
        prev.addActionListener(e -> {
            int idx = imageIndex.get() - 1;
            Post postItem = post.get();
            if (idx >= 0) {
                imageIndex.getAndDecrement();
                SwingUtilities.invokeLater(this::updateUi);
            } else {
                post.set(source.getPrevLatestPost(post.get().getPublished()));
                post.get().setImages(source.getPostImages(post.get().getId()));
                imageIndex.set(post.get().getImages().size() - 1);
                SwingUtilities.invokeLater(this::updateUi);
            }
        });

        next = new JButton(">");
        next.setFont(new Font("Georgia", Font.PLAIN, 72));
        add(next, BorderLayout.EAST);
        next.addActionListener(e -> {
            int idx = imageIndex.get() + 1;
            Post postItem = post.get();
            if (idx < postItem.getImages().size()) {
                imageIndex.getAndIncrement();
                SwingUtilities.invokeLater(this::updateUi);
            } else {
                post.set(source.getLatestPost(post.get().getPublished()));
                post.get().setImages(source.getPostImages(post.get().getId()));
                imageIndex.set(0);
                SwingUtilities.invokeLater(this::updateUi);
            }
        });

        add(imagePanel, BorderLayout.CENTER);
    }

    private Component getParent2() {
        Container parent = this;
        do {
            //if (parent instanceof JRViewer) {
            //  break;
            //}
            System.out.println(parent.getClass().getSimpleName());
            parent = parent.getParent();
        } while (parent != null);
        return parent;
    }
}
