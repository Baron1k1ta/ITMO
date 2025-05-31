package handler.graphic;

import java.awt.Graphics2D;

import output.GraphicPanel;

public class Rectangle implements Shape {
    private final double x1, y1, x2, y2;

    public Rectangle(final double x1, final double y1, final double x2, final double y2) {
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.max(y1, y2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.min(y1, y2);
    }

    @Override
    public void draw(final Graphics2D g, final GraphicPanel p) {
        int x = p.getX(x1);
        int y = p.getY(y1);
        g.drawRect(x, y, p.getX(x2) - x, p.getY(y2) - y);
    }

}
