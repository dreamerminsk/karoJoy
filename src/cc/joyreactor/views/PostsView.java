package cc.joyreactor.views;

import cc.joyreactor.Source;
import cc.joyreactor.data.Post;
import cc.joyreactor.models.PostsModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

public class PostsView extends JPanel implements PropertyChangeListener {

    private final PostsModel model;

    private Post current = null;
    private JLabel userLabel;
    private Source source;
    private JLabel ratingLabel;
    private BufferedImage defaultPic;
    private JPanel tagsPanel;
    private JLabel loadingLabel = new JLabel(" LOADING... ");

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
        userLabel.setBorder(UIManager.getBorder("ScrollPane.border"));
        userLabel.setFont(userLabel.getFont().deriveFont(Font.PLAIN, 32.0f));
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        //c.weighty = 1.0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        comp.add(userLabel, c);

        ratingLabel = new JLabel();
        ratingLabel.setFont(ratingLabel.getFont().deriveFont(Font.ITALIC, 32.0f));
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        //c.weighty = 1.0;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.EAST;
        comp.add(ratingLabel, c);

        JButton nextButton = new JButton("next >>");
        nextButton.setFont(nextButton.getFont().deriveFont(Font.ITALIC, 32.0f));
        nextButton.addActionListener(e -> {
            nextButton.setText("loading...");
            nextButton.setEnabled(false);
            CompletableFuture.supplyAsync(() -> source.getLatestPost(current.getPublished()))
                    .thenAcceptAsync((p) -> {
                        current = p;
                        SwingUtilities.invokeLater(this::update);
                        SwingUtilities.invokeLater(() -> {
                            nextButton.setText("next >>");
                            nextButton.setEnabled(true);
                        });
                    });
        });
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        //c.weighty = 1.0;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        comp.add(nextButton, c);


        tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        c.insets = new Insets(5, 5, 5, 5);
        c.weighty = 1.0;
        //c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        comp.add(tagsPanel, c);

        return comp;
    }

    private void update() {
        userLabel.setText(current.getUser().getName() + " ");
        userLabel.setIcon(new ImageIcon(defaultPic));
        ratingLabel.setText(current.getRating().toString() + " ");
        SwingUtilities.invokeLater(() -> tagsPanel.removeAll());
        SwingUtilities.invokeLater(() -> loadingLabel.setVisible(true));
        SwingUtilities.invokeLater(() -> tagsPanel.add(loadingLabel));

        CompletableFuture.supplyAsync(() -> {
            try {
                return ImageIO.read(new ByteArrayInputStream(current.getUser().getAvatar()));
            } catch (IOException e) {
                return defaultPic;
            }
        }).thenAcceptAsync(img -> userLabel.setIcon(new ImageIcon(img)));

        CompletableFuture.supplyAsync(() -> source.getPostTags(current.getId()))
                .thenAcceptAsync(tags -> {
                    SwingUtilities.invokeLater(() -> tagsPanel.removeAll());
                    tags.stream().map(tag -> {
                        JLabel tagLabel = new JLabel(" " + tag.getTag() + " ");
                        tagLabel.setFont(tagLabel.getFont().deriveFont(Font.ITALIC, 16.0f));
                        tagLabel.setBorder(UIManager.getBorder("ScrollPane.border"));
                        return tagLabel;
                    }).forEach(tl -> SwingUtilities.invokeLater(() -> tagsPanel.add(tl)));
                    SwingUtilities.invokeLater(() -> loadingLabel.setVisible(false));
                });
    }

    private JComponent getSearchView() {
        return new JPanel();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
