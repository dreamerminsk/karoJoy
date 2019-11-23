package cc.joyreactor.views;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

public class PublishedTreeTableModel extends AbstractTreeTableModel {
    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public Object getValueAt(Object node, int column) {
        return null;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        return 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return 0;
    }
}
