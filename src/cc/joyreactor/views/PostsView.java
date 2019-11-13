package cc.joyreactor.views;

import cc.joyreactor.Source;
import cc.joyreactor.data.Post;
import cc.joyreactor.models.PostsModel;
import cc.joyreactor.utils.Strings;
import ch.caro62.utils.SwingExecutor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class PostsView extends JPanel implements PropertyChangeListener {

    private final PostsModel model;
    private final ExecutorService ES = Executors.newSingleThreadScheduledExecutor();
    private Post current;
    private JLabel userLabel;
    private Source source;
    private JLabel ratingLabel;
    private BufferedImage defaultPic;
    private JTagPanel tagsPanel;
    private JLabel pubLabel;
    private JPanel imagesPanel;
    private JLabel postImage;
    private JPanel postImages;
    private Box imagesBox;
    private JLabel commentsLabel;
    private JTagStatsView tagStats;

    public PostsView(PostsModel model) throws SQLException, IOException {
        super(new BorderLayout());
        this.model = model;
        this.model.addPropertyChangeListener(this);
        source = Source.getInstance();
        current = source.getLatestPost(Instant.now().atZone(ZoneId.of("Europe/Moscow")));
        ui();
        if (current != null) {
            update();
        }
    }

    private void ui() throws IOException {
        defaultPic = ImageIO.read(new URL("http://glassleafdev.mpstechnologies.com/images/icon/image-icon.png"));
        add(getSearchView(), BorderLayout.NORTH);
        add(getPostView(), BorderLayout.CENTER);
    }

    private JComponent getPostView() {
        JPanel comp = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        userLabel = new JLabel();
        userLabel.setFont(new Font("Lucida Console", Font.PLAIN, 26));
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        comp.add(userLabel, c);


        JButton jbNext = new JButton(">");
        JButton jbPrev = new JButton("<");
        JButton jbNext10 = new JButton(">>");
        JButton jbPrev10 = new JButton("<<");
        JButton jbNext100 = new JButton(">>>");
        JButton jbPrev100 = new JButton("<<<");

        jbNext10.setFont(jbNext10.getFont().deriveFont(Font.ITALIC, 8.0f));
        jbNext10.addActionListener(e -> {
            jbPrev100.setEnabled(false);
            jbNext100.setEnabled(false);
            jbPrev10.setEnabled(false);
            jbNext10.setEnabled(false);
            jbPrev.setEnabled(false);
            jbNext.setEnabled(false);
            CompletableFuture.supplyAsync(() -> source.getLatestPost(current.getPublished(), 10))
                    .thenAcceptAsync((p) -> SwingUtilities.invokeLater(() -> {
                        current = p;
                        update();
                        jbPrev100.setEnabled(true);
                        jbNext100.setEnabled(true);
                        jbPrev10.setEnabled(true);
                        jbNext10.setEnabled(true);
                        jbPrev.setEnabled(true);
                        jbNext.setEnabled(true);
                    }));
        });

        jbPrev100.setFont(jbPrev100.getFont().deriveFont(Font.ITALIC, 8.0f));
        jbPrev100.addActionListener(e -> {
            jbPrev100.setEnabled(false);
            jbNext100.setEnabled(false);
            jbPrev10.setEnabled(false);
            jbNext10.setEnabled(false);
            jbPrev.setEnabled(false);
            jbNext.setEnabled(false);
            CompletableFuture.supplyAsync(() -> source.getPrevLatestPost(current.getPublished(), 100))
                    .thenAcceptAsync((p) -> SwingUtilities.invokeLater(() -> {
                        current = p;
                        update();
                        jbPrev100.setEnabled(true);
                        jbNext100.setEnabled(true);
                        jbPrev10.setEnabled(true);
                        jbNext10.setEnabled(true);
                        jbPrev.setEnabled(true);
                        jbNext.setEnabled(true);
                    }));
        });

        jbNext100.setFont(jbNext10.getFont().deriveFont(Font.ITALIC, 8.0f));
        jbNext100.addActionListener(e -> {
            jbPrev100.setEnabled(false);
            jbNext100.setEnabled(false);
            jbPrev10.setEnabled(false);
            jbNext10.setEnabled(false);
            jbPrev.setEnabled(false);
            jbNext.setEnabled(false);
            CompletableFuture.supplyAsync(() -> source.getLatestPost(current.getPublished(), 100))
                    .thenAcceptAsync((p) -> SwingUtilities.invokeLater(() -> {
                        current = p;
                        update();
                        jbPrev100.setEnabled(true);
                        jbNext100.setEnabled(true);
                        jbPrev10.setEnabled(true);
                        jbNext10.setEnabled(true);
                        jbPrev.setEnabled(true);
                        jbNext.setEnabled(true);
                    }));
        });

        jbPrev10.setFont(jbPrev10.getFont().deriveFont(Font.ITALIC, 8.0f));
        jbPrev10.addActionListener(e -> {
            jbPrev100.setEnabled(false);
            jbNext100.setEnabled(false);
            jbPrev10.setEnabled(false);
            jbNext10.setEnabled(false);
            jbPrev.setEnabled(false);
            jbNext.setEnabled(false);
            CompletableFuture.supplyAsync(() -> source.getPrevLatestPost(current.getPublished(), 10))
                    .thenAcceptAsync((p) -> SwingUtilities.invokeLater(() -> {
                        current = p;
                        update();
                        jbPrev100.setEnabled(true);
                        jbNext100.setEnabled(true);
                        jbPrev10.setEnabled(true);
                        jbNext10.setEnabled(true);
                        jbPrev.setEnabled(true);
                        jbNext.setEnabled(true);
                    }));
        });


        jbNext.setFont(jbNext.getFont().deriveFont(Font.ITALIC, 8.0f));
        jbNext.addActionListener(e -> {
            jbPrev100.setEnabled(false);
            jbNext100.setEnabled(false);
            jbPrev10.setEnabled(false);
            jbNext10.setEnabled(false);
            jbPrev.setEnabled(false);
            jbNext.setEnabled(false);
            CompletableFuture.supplyAsync(() -> source.getLatestPost(current.getPublished()))
                    .thenAcceptAsync((p) -> SwingUtilities.invokeLater(() -> {
                        current = p;
                        update();
                        jbPrev100.setEnabled(true);
                        jbNext100.setEnabled(true);
                        jbPrev10.setEnabled(true);
                        jbNext10.setEnabled(true);
                        jbPrev.setEnabled(true);
                        jbNext.setEnabled(true);
                    }));
        });

        jbPrev.setFont(jbPrev.getFont().deriveFont(Font.ITALIC, 8.0f));
        jbPrev.addActionListener(e -> {
            jbPrev100.setEnabled(false);
            jbNext100.setEnabled(false);
            jbPrev10.setEnabled(false);
            jbNext10.setEnabled(false);
            jbPrev.setEnabled(false);
            jbNext.setEnabled(false);
            CompletableFuture.supplyAsync(() -> source.getPrevLatestPost(current.getPublished()))
                    .thenAcceptAsync((p) -> SwingUtilities.invokeLater(() -> {
                        current = p;
                        update();
                        jbPrev100.setEnabled(true);
                        jbNext100.setEnabled(true);
                        jbPrev10.setEnabled(true);
                        jbNext10.setEnabled(true);
                        jbPrev.setEnabled(true);
                        jbNext.setEnabled(true);
                    }));
        });

        pubLabel = new JLabel();
        pubLabel.setFont(pubLabel.getFont().deriveFont(Font.PLAIN | Font.ITALIC, 16.0f));

        JPanel pubPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pubPanel.add(jbPrev100);
        pubPanel.add(jbPrev10);
        pubPanel.add(jbPrev);
        pubPanel.add(pubLabel);
        pubPanel.add(jbNext);
        pubPanel.add(jbNext10);
        pubPanel.add(jbNext100);
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.LAST_LINE_START;
        comp.add(pubPanel, c);


        commentsLabel = new JLabel();
        try (InputStream stream = this.getClass().getResourceAsStream("/cc/joyreactor/icons/instagram-stories-comments-48px.png")) {
            commentsLabel.setIcon(new ImageIcon(ImageIO.read(stream)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        commentsLabel.setFont(commentsLabel.getFont().deriveFont(Font.ITALIC, 32.0f));
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.EAST;
        comp.add(commentsLabel, c);

        ratingLabel = new JLabel();
        try (InputStream stream = this.getClass().getResourceAsStream("/cc/joyreactor/icons/instagram-stories-rating-48px.png")) {
            ratingLabel.setIcon(new ImageIcon(ImageIO.read(stream)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ratingLabel.setFont(ratingLabel.getFont().deriveFont(Font.ITALIC, 32.0f));
        c.gridx = 3;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.EAST;
        comp.add(ratingLabel, c);


        tagsPanel = new JTagPanel(new ArrayList<>());
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 4;
        c.insets = new Insets(5, 5, 5, 5);
        //c.weighty = 1.0;
        //c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        comp.add(tagsPanel, c);

        imagesPanel = new JPanel(new BorderLayout());
        imagesBox = Box.createVerticalBox();
        postImage = new JLabel();
        imagesPanel.add(new JScrollPane(imagesBox), BorderLayout.CENTER);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 3;
        c.insets = new Insets(5, 5, 5, 5);
        c.weighty = 1.0;
        c.weightx = 2.0;
        c.fill = GridBagConstraints.BOTH;
        comp.add(imagesPanel, c);

        tagStats = new JTagStatsView();
        c.gridx = 3;
        c.gridy = 3;
        c.gridwidth = 1;
        c.insets = new Insets(5, 5, 5, 5);
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        comp.add(new JScrollPane(tagStats), c);

        return comp;
    }

    private void update() {
        if (current == null) return;
        //userLabel.setText(current.getUser().getName());
        CompletableFuture.runAsync(() -> updateLabel(userLabel, current.getUser().getName()), ES);
        pubLabel.setText(current.getPublished().format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss")) + " ");
        ratingLabel.setText(current.getRating().toString() + " ");
        commentsLabel.setText(current.getComments().toString() + " ");
        imagesBox.removeAll();

        CompletableFuture.supplyAsync(() -> {
            try {
                return ImageIO.read(new ByteArrayInputStream(current.getUser().getAvatar()));
            } catch (IOException e) {
                return defaultPic;
            }
            //}).thenAcceptAsync(img -> userLabel.setIcon(new ImageIcon(img)));
        }, ES).thenAcceptAsync(img -> updateImage(userLabel, img), ES);

        CompletableFuture.supplyAsync(() -> source.getPostImages(current.getId()), ES)
                .thenAcceptAsync(images -> images.stream().sequential().forEach((image) -> {
                    try {
                        BufferedImage bufferedImage = ImageIO.read(new URL(image.getRef()));
                        JLabel pImage = new JLabel();
                        BufferedImage pic = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
                        for (int i = 0; i < image.getWidth(); i++) {
                            for (int j = 0; j < image.getHeight(); j++) {
                                pic.setRGB(i, j, Color.darkGray.getRGB());
                            }
                        }
                        pImage.setIcon(new ImageIcon(pic));
                        JLabel label = new JLabel(Strings.getLastSplitComponent(
                                URLDecoder.decode(image.getRef(), StandardCharsets.UTF_8.name()), "/"));
                        label.setFont(label.getFont().deriveFont(16.0f));
                        imagesBox.add(label);
                        imagesBox.add(pImage);
                        imagesBox.revalidate();
                        imagesBox.repaint();
                        updatePostImage(pImage, bufferedImage);
                        //postImage.setIcon(new ImageIcon(bufferedImage));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }), ES).thenRunAsync(() -> SwingUtilities.invokeLater(() -> imagesBox.revalidate()), ES);

        CompletableFuture.supplyAsync(() -> source.getPostTags(current.getId()), ES)
                .thenAcceptAsync(tags -> SwingUtilities.invokeLater(() -> tagsPanel.setTags(tags)), ES);


    }

    private void updatePostImage(JLabel userLabel, BufferedImage img) {
        BufferedImage logo = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < logo.getWidth(); i++) {
            for (int j = 0; j < logo.getHeight(); j++) {
                logo.setRGB(i, j, Color.WHITE.getRGB());
            }
        }
        for (int j = 0; j < logo.getHeight(); j++) {
            for (int i = 0; i < logo.getWidth(); i++) {
                logo.setRGB(i, j, img.getRGB(i, j));
            }
            if (j % 3 == 0) {
                SwingUtilities.invokeLater(() -> userLabel.setIcon(new ImageIcon(logo)));
                try {
                    Thread.sleep(8);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        SwingUtilities.invokeLater(() -> userLabel.setIcon(new ImageIcon(logo)));
    }

    private void updateImage(JLabel userLabel, BufferedImage img) {
        BufferedImage logo = new BufferedImage(50, 50, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < logo.getWidth(); i++) {
            for (int j = 0; j < logo.getHeight(); j++) {
                logo.setRGB(i, j, Color.WHITE.getRGB());
            }
        }
        ImageIcon prev = (ImageIcon) userLabel.getIcon();
        if (prev != null) {
            BufferedImage prevImage = (BufferedImage) prev.getImage();
            for (int i = 0; i < prevImage.getWidth(); i++) {
                for (int j = 0; j < prevImage.getHeight(); j++) {
                    logo.setRGB(i, j, prevImage.getRGB(i, j));
                }
            }
        }

        int diffX = (logo.getWidth() - img.getWidth()) / 2;
        int diffX2 = logo.getWidth() - img.getWidth() - diffX;
        int diffY = (logo.getHeight() - img.getHeight()) / 2;
        int diffY2 = logo.getHeight() - img.getHeight() - diffY;
        for (int i = 0; i < logo.getWidth(); i++) {
            for (int j = 0; j < logo.getHeight(); j++) {
                try {
                    if ((i < diffX) || (i > (logo.getWidth() - diffX2 - 1)) || (j < diffY) || (j > (logo.getHeight() - diffY2 - 1))) {
                        logo.setRGB(i, j, Color.WHITE.getRGB());
                    } else {
                        logo.setRGB(i, j, img.getRGB(i - diffX, j - diffY));
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "[" + (i - diffX) + " / " + img.getWidth() + ", " + (j - diffY) + " / " + img.getHeight() + "] ", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            if (i % 2 == 0) {
                userLabel.setIcon(new ImageIcon(logo));
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        userLabel.setIcon(new ImageIcon(logo));
    }

    private void updateLabel(JLabel label, String text) {
        String oldText = label.getText();
        if (oldText.equals(text)) return;
        for (int i = 0; i < text.length(); i++) {
            if (i < label.getText().length()) {
                char[] chars = label.getText().toCharArray();
                chars[i] = text.charAt(i);
                SwingUtilities.invokeLater(() -> label.setText(new String(chars)));
            } else {
                int finalI = i;
                SwingUtilities.invokeLater(() -> label.setText(label.getText() + text.charAt(finalI)));
            }
            SwingUtilities.invokeLater(() -> {
                label.revalidate();
                label.repaint();
            });
            try {
                Thread.sleep(64);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (oldText.length() > text.length()) {
            for (int i = 0; i < (oldText.length() - text.length()); i++) {
                SwingUtilities.invokeLater(() -> {
                    label.setText(label.getText().substring(0, label.getText().length() - 1));
                    label.revalidate();
                    label.repaint();
                });
                try {
                    Thread.sleep(64);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateLabel2(JLabel l, String t) {
        if (l.getText().equals(t)) return;
        final int minLength = Math.min(l.getText().length(), t.length());
        IntStream.range(0, minLength).forEach(i -> {
            char[] chars = l.getText().toCharArray();
            chars[i] = t.charAt(i);
            System.out.println("TITLE: " + new String(chars));
            updateLabelTitle(l, new String(chars));
        });
        IntStream.range(minLength, l.getText().length()).forEach(i -> updateLabelTitle(l, l.getText().substring(0, minLength) + l.getText().substring(minLength + 1)));
        IntStream.range(minLength, t.length()).forEach(i -> updateLabelTitle(l, l.getText() + t.charAt(i)));
    }

    private void updateLabelTitle(JLabel l, String t) {
        System.out.println("TITLE: " + t);
        SwingExecutor.schedule(() -> {
            l.setText(t);
            l.revalidate();
            l.repaint();
        }, 32, TimeUnit.MILLISECONDS);
    }


    private JComponent getSearchView() {
        return new JPanel();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
