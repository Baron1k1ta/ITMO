import handler.ExpressionHandler;
import input.InputHandler;
import output.GraphicPanel;
import output.Output;
import parser.ExpressionParser;
import expression.Expression;
import expression.operations.DoublOperation;

public class Main {

    public static void main(final String[] args) throws Exception {
        final InputHandler handler = new InputHandler(System.in, System.out);

        handler.header();
        while (true) {
            handler.input();
            launch(handler);
        }
    }

    public static double launch(final InputHandler handler) throws Exception {
        final ExpressionParser<Double> parser = new ExpressionParser<>(new DoublOperation());
        final Expression<Double> expr = parser.parse(handler.toParse);
        final GraphicPanel panel = ExpressionHandler.process(expr, handler.a, handler.b, handler.splitCount);

        Output.show(panel);
        handler.output(System.out, expr.toString(), panel.riemannSum);
        return panel.riemannSum;
    }
}