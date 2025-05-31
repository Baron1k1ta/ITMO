package handler.graphic;

import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics2D;

import expression.Expression;
import expression.operations.Operation;
import output.GraphicPanel;

public class Graph implements Shape {
    private static final int COUNT = 200;

    public final List<Point> points;
    public final double step;
    public final double fmin;
    public final double fmax;

    public <T> Graph(final Expression<T> expression, final double a, final double b, final Operation<T> oper) {
        double temp, maxF, minF;

        this.points = new ArrayList<>();
        this.step = (b - a) / COUNT;

        maxF = -Double.MAX_VALUE;
        minF = Double.MAX_VALUE;
        for (int i = 0; i <= COUNT; i++) {
            temp = oper.convertFrom(expression.apply(oper.convertTo(a + step * i)));
            maxF = Math.max(maxF, temp);
            minF = Math.min(minF, temp);
            points.add(new Point(a + step * i, temp));
        }
        this.fmin = minF;
        this.fmax = maxF;
    }

    public <T> Graph(final Expression<T> expression, final double a, final double b) {
        this(expression, a, b, expression.getOperation());
    }

    @Override
    public void draw(final Graphics2D g, final GraphicPanel p) {
        for (int i = 1; i <= COUNT; i++) {
            g.drawLine(
                    points.get(i - 1).getX(p), points.get(i - 1).getY(p),
                    points.get(i).getX(p), points.get(i).getY(p));
        }
    }

}
