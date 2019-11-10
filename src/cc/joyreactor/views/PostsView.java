package cc.joyreactor.views;

import cc.joyreactor.Source;
import cc.joyreactor.data.Post;
import cc.joyreactor.models.PostsModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;

public class PostsView extends JPanel implements PropertyChangeListener {

    private final PostsModel model;

    private Post current = null;

    public PostsView(PostsModel model) throws SQLException {
        super(new BorderLayout());
        this.model = model;
        this.model.addPropertyChangeListener(this);
        current = Source.getInstance().getPost(44);
        ui();
    }

    private void ui() {
        add(getSearchView(), BorderLayout.NORTH);
        add(getPostView(), BorderLayout.CENTER);
    }

    private JComponent getPostView() {
        JPanel comp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel userLabel = new JLabel(current.getUser().getName());
        comp.add(userLabel);
        return comp;
    }

    private JComponent getSearchView() {
        return new JPanel();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
