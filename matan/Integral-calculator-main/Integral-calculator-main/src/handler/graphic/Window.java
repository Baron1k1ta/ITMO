package handler.graphic;

import java.awt.Graphics2D;

import output.GraphicPanel;

public class Window implements Shape {
    private static final double eps = 0.001;
    private static final double markupLimit = 50;

    @Override
    public void draw(final Graphics2D g, final GraphicPanel p) {
        final int zeroY = (p.fmin <= eps && -eps <= p.fmax) ? p.getY(0) : p.getY(p.fmax) / 2;
        final int zeroX = (p.a <= eps && -eps <= p.b) ? p.getX(0) : p.getX(p.a) / 2;

        g.drawLine(p.getX(p.a), zeroY, p.getX(p.b), zeroY);
        if (p.b - p.a <= markupLimit) {
            for (int x = (int) Math.ceil(p.a); x <= Math.floor(p.b); x++) {
                if (x != 0) {
                    markupX(g, p, zeroY, x, Integer.toString(x));
                }
            }
        }
        markupX(g, p, zeroY, p.b, String.format("%.2f", p.b));

        g.drawLine(zeroX, p.getY(p.fmin), zeroX, p.getY(p.fmax));
        if (p.fmax - p.fmin <= markupLimit && p.fmax - p.fmin > eps) {
            for (int y = (int) Math.ceil(p.fmin); y <= Math.floor(p.fmax); y++) {
                if (y != 0) {
                    markupY(g, p, zeroX, y, Integer.toString(y));
                }
            }
        }
        markupY(g, p, zeroX, p.fmax, String.format("%.2f", p.fmax));

        g.fillOval(zeroX - 4, zeroY - 4, 8, 8);
    }

    private void markupX(final Graphics2D g, final GraphicPanel p, final int zeroY, final double x, final String str) {
        final int temp = p.getX(x);
        g.drawLine(temp, zeroY + 3, temp, zeroY - 3);
        g.drawString(str, temp, zeroY + 20);
    }

    private void markupY(final Graphics2D g, final GraphicPanel p, final int zeroX, final double y, final String str) {
        final int temp = p.getY(y);
        g.drawLine(zeroX + 3, temp, zeroX - 3, temp);
        g.drawString(str, zeroX - 20, temp - 3);
    }
}
