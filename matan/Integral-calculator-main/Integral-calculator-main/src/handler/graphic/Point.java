package handler.graphic;

import output.GraphicPanel;

public class Point {
    public double x, y;

    public Point(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public int getX(final GraphicPanel p) {
        return p.getX(x);
    }

    public int getY(final GraphicPanel p) {
        return p.getY(y);
    }
}
