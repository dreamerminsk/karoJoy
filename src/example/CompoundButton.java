package example;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.Objects;

public class CompoundButton extends JButton {
    protected final ButtonLocation bl;
    protected final Dimension dim;
    protected transient Shape shape;
    protected transient Shape base;

    protected CompoundButton(Dimension d, ButtonLocation bl) {
        super();
        this.dim = d;
        this.bl = bl;
        setIcon(new Icon() {
            private final Color fc = new Color(100, 150, 255, 200);
            private final Color ac = new Color(230, 230, 230);
            private final Color rc = Color.ORANGE;

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isArmed()) {
                    g2.setPaint(ac);
                    g2.fill(shape);
                } else if (isRolloverEnabled() && getModel().isRollover()) {
                    paintFocusAndRollover(g2, rc);
                } else if (hasFocus()) {
                    paintFocusAndRollover(g2, fc);
                } else {
                    g2.setPaint(getBackground());
                    g2.fill(shape);
                }
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return dim.width;
            }

            @Override
            public int getIconHeight() {
                return dim.height;
            }
        });
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBackground(new Color(0xFA_FA_FA));
        initShape();
    }

    @Override
    public Dimension getPreferredSize() {
        return dim;
    }

    private void initShape() {
        if (!getBounds().equals(base)) {
            base = getBounds();
            double ww = getWidth() * .5;
            double xx = ww * .5;
            Shape inner = new Ellipse2D.Double(xx, xx, ww, ww);
            if (ButtonLocation.CENTER == bl) {
                shape = inner;
            } else {
                // TEST: parent.isOptimizedDrawingEnabled: false
                double dw = getWidth() - 2d;
                double dh = getHeight() - 2d;
                Shape outer = new Arc2D.Double(1d, 1d, dw, dh, bl.getStartAngle(), 90d, Arc2D.PIE);
                Area area = new Area(outer);
                area.subtract(new Area(inner));
                shape = area;
            }
        }
    }

    protected void paintFocusAndRollover(Graphics2D g2, Color color) {
        g2.setPaint(new GradientPaint(0f, 0f, color, getWidth() - 1f, getHeight() - 1f, color.brighter(), true));
        g2.fill(shape);
        g2.setPaint(getBackground());
    }

    @Override
    protected void paintComponent(Graphics g) {
        initShape();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(Color.GRAY);
        g2.draw(shape);
        g2.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        return Objects.nonNull(shape) && shape.contains(x, y);
    }


    enum ButtonLocation {
        CENTER(0d), NORTH(45d), EAST(135d), SOUTH(225d), WEST(-45d);
        private final double degree;

        ButtonLocation(double degree) {
            this.degree = degree;
        }

        public double getStartAngle() {
            return degree;
        }
    }

}
