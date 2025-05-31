package expression.operations;

public class DoublOperation implements Operation<Double> {

    @Override
    public final Double add(final Double a, final Double b) {
        return a + b;
    }

    @Override
    public final Double subtract(final Double a, final Double b) {
        return a - b;
    }

    @Override
    public final Double multiply(final Double a, final Double b) {
        return a * b;
    }

    @Override
    public final Double divide(final Double a, final Double b) {
        return a / b;
    }

    @Override
    public final Double negate(final Double a) {
        return -a;
    }

    @Override
    public final Double pow(final Double a, final Double b) {
        return Math.pow(a, b);
    }

    @Override
    public final Double log(final Double a, final Double b) {
        return Math.log(a) / Math.log(b);
    }

    @Override
    public final Double sin(final Double a) {
        return Math.sin(a);
    }

    @Override
    public final Double cos(final Double a) {
        return Math.cos(a);
    }

    @Override
    public final Double toConst(String string) {
        return Double.parseDouble(string);
    }

    @Override
    public final Double convertTo(final double x) {
        return x;
    }

    @Override
    public final double convertFrom(final Double x) {
        return x;
    }
}
