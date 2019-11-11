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
        JPanel comp = new JPanel(new FlowLayout(FlowLayout.LEFT));

        userLabel = new JLabel();
        userLabel.setFont(userLabel.getFont().deriveFont(Font.BOLD | Font.ITALIC, 16.0f));
        comp.add(userLabel);

        ratingLabel = new JLabel();
        ratingLabel.setFont(ratingLabel.getFont().deriveFont(Font.ITALIC, 32.0f));
        comp.add(ratingLabel);

        JButton nextButton = new JButton("next >>");
        nextButton.addActionListener(e -> {
            nextButton.setText("loading...");
            nextButton.setEnabled(false);
            CompletableFuture.supplyAsync(() -> source.getLatestPost(current.getPublished()))
                    .thenAcceptAsync((p) -> {
                        current = p;
                        nextButton.setText("next >>");
                        nextButton.setEnabled(true);
                        SwingUtilities.invokeLater(this::update);
                    });
        });
        comp.add(nextButton);

        return comp;
    }

    private void update() {
        userLabel.setText("[" + current.getUser().getName() + "]");
        CompletableFuture.supplyAsync(() -> {
            try {
                return ImageIO.read(new ByteArrayInputStream(current.getUser().getAvatar()));
            } catch (IOException e) {
                return defaultPic;
            }
        }).thenAcceptAsync(img -> userLabel.setIcon(new ImageIcon(img)));

        ratingLabel.setText(current.getRating().toString());
    }

    private JComponent getSearchView() {
        return new JPanel();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
