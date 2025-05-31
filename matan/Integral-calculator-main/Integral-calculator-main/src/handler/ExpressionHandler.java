package handler;

import java.util.ArrayList;
import java.util.List;

import expression.Expression;
import expression.operations.Operation;
import handler.graphic.*;
import output.GraphicPanel;

public class ExpressionHandler {

    public static <T> GraphicPanel process(final Expression<T> expression, final double a,
            final double b, final int splitCount) throws Exception {
        final List<Shape> shapes = new ArrayList<>();
        final Operation<T> oper = expression.getOperation();
        final Graph graph = new Graph(expression, a, b, oper);
        final double step = (b - a) / splitCount;
        double funct, riemannSum;

        shapes.add(graph);
        shapes.add(new Window());
        riemannSum = 0.0;

        for (int i = 0; i < splitCount; i++) {
            funct = oper.convertFrom(expression.apply(oper.convertTo(a + (i + 0.5) * step)));
            riemannSum += funct * step;
            shapes.add(new Rectangle(a + i * step, 0, a + (i + 1) * step, funct));
        }
        return new GraphicPanel(shapes, a, b, graph.fmin, graph.fmax, riemannSum);
    }

}
