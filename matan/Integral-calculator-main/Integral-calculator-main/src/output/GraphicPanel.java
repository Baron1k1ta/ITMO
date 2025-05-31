package output;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import javax.swing.JPanel;

import handler.graphic.Point;
import handler.graphic.Shape;

public class GraphicPanel extends JPanel {
    public static final int START_WIDTH = 800;
    public static final int START_HEIGHT = 800;
    public static final double OFF = 0.1;
    public static final double WITHOUT_OFF = 1.0 - OFF;
    public static final double SCALE = WITHOUT_OFF - OFF;

    private final List<Shape> shapes;
    public final double a, b, aLength;
    public final double fmin, fmax, fLength, riemannSum;

    public GraphicPanel(final List<Shape> shapes, final double a, final double b,
            final double minF, final double maxF, final double riemannSum) {
        super();
        this.shapes = shapes;
        this.a = a;
        this.b = b;
        this.aLength = b - a;
        this.fmin = Math.min(minF, 0);
        this.fmax = Math.max(maxF, 0);
        this.fLength = fmax - fmin;
        this.riemannSum = riemannSum;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);
        g2d.setStroke(new BasicStroke(3));
        shapes.forEach(shape -> shape.draw(g2d, this));
    }

    public int getX(final double x) {
        return (int) (getWidth() * (OFF + SCALE * (x - a) / aLength));
    }

    public int getY(final double y) {
        return (int) (getHeight() * (WITHOUT_OFF - SCALE * (y - fmin) / fLength));
    }

    public Point point(final double x, final double y) {
        return new Point(getX(x), getY(y));
    }
}
