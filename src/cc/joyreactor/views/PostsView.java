package cc.joyreactor.views;

import cc.joyreactor.Source;
import cc.joyreactor.data.Post;
import cc.joyreactor.data.Tag;
import cc.joyreactor.events.TagListener;
import cc.joyreactor.models.PostsModel;
import cc.joyreactor.utils.Strings;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostsView extends JPanel implements PropertyChangeListener, TagListener {

    private final PostsModel model;
    private final ExecutorService ES = Executors.newSingleThreadScheduledExecutor();
    private final java.util.List<Tag> filterTags = new ArrayList<>();
    private Post current;
    private JLabel userLabel;
    private Source source;
    private JLabel ratingLabel;
    private BufferedImage defaultPic;
    private JTagPanel tagsPanel;
    private LocalDateTimeSpinner pubLabel;
    private JPanel imagesPanel;
    private JLabel postImage;
    private JPanel postImages;
    private Box imagesBox;
    private JLabel commentsLabel;
    private JFilterView tagStats;
    private JPanel imagesMenu;
    private JPanel crPanel;

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

        pubLabel = new LocalDateTimeSpinner();

        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.LAST_LINE_START;
        comp.add(pubLabel, c);


        crPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

        commentsLabel = new JLabel();
        try (InputStream stream = this.getClass().getResourceAsStream("/cc/joyreactor/icons/instagram-stories-comments-48px.png")) {
            commentsLabel.setIcon(new ImageIcon(ImageIO.read(stream)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        commentsLabel.setFont(commentsLabel.getFont().deriveFont(Font.ITALIC, 32.0f));

        ratingLabel = new JLabel();
        try (InputStream stream = this.getClass().getResourceAsStream("/cc/joyreactor/icons/instagram-stories-rating-48px.png")) {
            ratingLabel.setIcon(new ImageIcon(ImageIO.read(stream)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ratingLabel.setFont(ratingLabel.getFont().deriveFont(Font.ITALIC, 32.0f));
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.EAST;

        crPanel.add(commentsLabel);
        crPanel.add(ratingLabel);
        comp.add(crPanel, c);


        tagsPanel = new JTagPanel(new ArrayList<>());
        tagsPanel.addTagListener(this);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 4;
        c.insets = new Insets(5, 5, 5, 5);
        //c.weighty = 1.0;
        //c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        comp.add(tagsPanel, c);

        imagesPanel = new JPanel(new BorderLayout());
        imagesMenu = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imagesBox = Box.createVerticalBox();
        postImage = new JLabel();
        //imagesPanel.add(new JScrollPane(imagesMenu), BorderLayout.PAGE_START);
        imagesPanel.add(new JScrollPane(imagesMenu), BorderLayout.CENTER);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 3;
        c.insets = new Insets(5, 5, 5, 5);
        c.weighty = 1.0;
        c.weightx = 2.0;
        c.fill = GridBagConstraints.BOTH;
        comp.add(imagesPanel, c);

        tagStats = new JFilterView(filterTags);
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
        if (current.getUser() == null) return;
        //userLabel.setText(current.getUser().getName());
        CompletableFuture.runAsync(() -> updateLabel(userLabel, current.getUser().getName()), ES);
        pubLabel.setDt(current.getPublished().toLocalDateTime());
        ratingLabel.setText(current.getRating().toString() + " ");
        commentsLabel.setText(current.getComments().toString() + " ");
        imagesBox.removeAll();
        imagesMenu.removeAll();

        CompletableFuture.supplyAsync(() -> {
            try {
                return ImageIO.read(new ByteArrayInputStream(current.getUser().getAvatar()));
            } catch (IOException e) {
                return defaultPic;
            }
            //}).thenAcceptAsync(img -> userLabel.setIcon(new ImageIcon(img)));
        }, ES).thenAcceptAsync(img -> updateImage(userLabel, img), ES);

        CompletableFuture.supplyAsync(() -> source.getPostTags(current.getId()), ES)
                .thenAcceptAsync(tags -> SwingUtilities.invokeLater(() -> tagsPanel.setTags(tags)), ES);

        CompletableFuture.supplyAsync(() -> source.getPostImages(current.getId()), ES)
                .thenAcceptAsync(images -> images.stream().sequential().forEach((image) -> {
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            return ImageIO.read(new URL(image.getRef()));
                        } catch (IOException e) {
                            return null;
                        }
                    }, ES).thenApplyAsync((bufferedImage) ->
                            Scalr.resize(bufferedImage,
                                    Scalr.Method.ULTRA_QUALITY,
                                    Scalr.Mode.AUTOMATIC,
                                    512, 512), ES)
                            .thenAcceptAsync((pic) -> SwingUtilities.invokeLater(() ->
                            {
                                JLabel jLabel = null;
                                try {
                                    jLabel = new JLabel(Strings.getLastSplitComponent(
                                            URLDecoder.decode(image.getRef(), StandardCharsets.UTF_8.name()), "/"),
                                            new ImageIcon(pic), SwingConstants.LEFT);
                                    jLabel.setHorizontalTextPosition(JLabel.CENTER);
                                    jLabel.setVerticalTextPosition(JLabel.TOP);
                                    jLabel.setFont(jLabel.getFont().deriveFont(16.0f));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                imagesMenu.add(jLabel);
                                imagesPanel.revalidate();
                                imagesPanel.repaint();
                            }), ES);
                }), ES).thenRunAsync(() -> SwingUtilities.invokeLater(() -> imagesBox.revalidate()), ES);
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


    private JComponent getSearchView() {
        return new JPanel();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }


    @Override
    public void tagSelected(Tag tag) {
        SwingUtilities.invokeLater(() -> {
            tagStats.add(tag);
        });
    }
}
