package cc.joyreactor.views;

import cc.joyreactor.Source;
import cc.joyreactor.data.Post;
import cc.joyreactor.models.PostsModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

public class PostsView extends JPanel implements PropertyChangeListener {

    private final PostsModel model;

    private Post current = null;
    private JLabel userLabel;
    private Source source;
    private JLabel ratingLabel;
    private BufferedImage defaultPic;
    private JPanel tagsPanel;
    private JLabel pubLabel;
    private JPanel imagesPanel;
    private JLabel postImage;

    public PostsView(PostsModel model) throws SQLException, IOException {
        super(new BorderLayout());
        this.model = model;
        this.model.addPropertyChangeListener(this);
        source = Source.getInstance();
        current = source.getLatestPost(Instant.now().atZone(ZoneId.of("Europe/Moscow")));
        ui();
        update();
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
        userLabel.setFont(userLabel.getFont().deriveFont(Font.PLAIN, 30.0f));
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        comp.add(userLabel, c);

        JButton jbNext = new JButton(">>");
        JButton jbPrev = new JButton("<<");
        jbNext.setFont(jbNext.getFont().deriveFont(Font.ITALIC, 12.0f));
        jbNext.addActionListener(e -> {
            jbPrev.setEnabled(false);
            jbNext.setEnabled(false);
            CompletableFuture.supplyAsync(() -> source.getLatestPost(current.getPublished()))
                    .thenAcceptAsync((p) -> SwingUtilities.invokeLater(() -> {
                        current = p;
                        update();
                        jbPrev.setEnabled(true);
                        jbNext.setEnabled(true);
                    }));
        });

        jbPrev.setFont(jbPrev.getFont().deriveFont(Font.ITALIC, 12.0f));
        jbPrev.addActionListener(e -> {
            jbPrev.setEnabled(false);
            jbNext.setEnabled(false);
            CompletableFuture.supplyAsync(() -> source.getPrevLatestPost(current.getPublished()))
                    .thenAcceptAsync((p) -> SwingUtilities.invokeLater(() -> {
                        current = p;
                        update();
                        jbPrev.setEnabled(true);
                        jbNext.setEnabled(true);
                    }));
        });

        pubLabel = new JLabel();
        pubLabel.setFont(pubLabel.getFont().deriveFont(Font.PLAIN | Font.ITALIC, 16.0f));

        JPanel pubPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pubPanel.add(jbPrev);
        pubPanel.add(pubLabel);
        pubPanel.add(jbNext);
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        //c.weighty = 1.0;
        c.anchor = GridBagConstraints.LAST_LINE_START;
        comp.add(pubPanel, c);

        ratingLabel = new JLabel();
        ratingLabel.setFont(ratingLabel.getFont().deriveFont(Font.ITALIC, 32.0f));
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        //c.weighty = 1.0;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.EAST;
        comp.add(ratingLabel, c);


        tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        c.insets = new Insets(5, 5, 5, 5);
        //c.weighty = 1.0;
        //c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        comp.add(tagsPanel, c);

        imagesPanel = new JPanel(new BorderLayout());
        postImage = new JLabel();
        imagesPanel.add(new JScrollPane(postImage), BorderLayout.CENTER);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.insets = new Insets(5, 5, 5, 5);
        c.weighty = 1.0;
        //c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        comp.add(imagesPanel, c);

        return comp;
    }

    private void update() {
        userLabel.setText(current.getUser().getName());
        pubLabel.setText(current.getPublished().format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss Z")) + " ");
        ratingLabel.setText(current.getRating().toString() + " ");
        tagsPanel.removeAll();
        postImage.setIcon(null);

        CompletableFuture.supplyAsync(() -> {
            try {
                return ImageIO.read(new ByteArrayInputStream(current.getUser().getAvatar()));
            } catch (IOException e) {
                return defaultPic;
            }
        }).thenAcceptAsync(img -> userLabel.setIcon(new ImageIcon(img)));

        CompletableFuture.supplyAsync(() -> source.getPostImages(current.getId()))
                .thenAcceptAsync(images -> {
                    SwingUtilities.invokeLater(() -> tagsPanel.removeAll());
                    images.stream().limit(1).sequential().forEach((image) -> {
                        try {
                            postImage.setIcon(new ImageIcon(ImageIO.read(new URL(image.getRef()))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                });

        CompletableFuture.supplyAsync(() -> source.getPostTags(current.getId()))
                .thenAcceptAsync(tags -> {
                    SwingUtilities.invokeLater(() -> tagsPanel.removeAll());
                    tags.stream().sequential().map(tag -> {
                        JLabel tagLabel = new JLabel(" " + tag.getTag() + " ");
                        tagLabel.setFont(tagLabel.getFont().deriveFont(Font.ITALIC, 16.0f));
                        tagLabel.setBorder(UIManager.getBorder("ScrollPane.border"));
                        tagLabel.addMouseListener(new MouseAdapter() {

                            @Override
                            public void mouseEntered(MouseEvent e) {
                                tagLabel.setForeground(Color.BLUE);
                            }

                            @Override
                            public void mouseExited(MouseEvent e) {
                                tagLabel.setForeground(Color.BLACK);
                            }
                        });
                        return tagLabel;
                    }).forEach(tl -> SwingUtilities.invokeLater(() -> {
                        tagsPanel.add(tl);
                        tagsPanel.revalidate();
                        tagsPanel.repaint();
                    }));
                });


    }

    private JComponent getSearchView() {
        return new JPanel();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
