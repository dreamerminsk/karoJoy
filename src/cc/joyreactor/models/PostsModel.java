package cc.joyreactor.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PostsModel {

    private PropertyChangeSupport changes = new PropertyChangeSupport(this);

    public PostsModel() {

    }


    public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        changes.removePropertyChangeListener(l);
    }
}
