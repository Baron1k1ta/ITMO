package expression.operations;

public class IntegerOperation implements Operation<Integer> {

    @Override
    public final Integer add(final Integer a, final Integer b) {
        return a + b;
    }

    @Override
    public final Integer subtract(final Integer a, final Integer b) {
        return a - b;
    }

    @Override
    public final Integer multiply(final Integer a, final Integer b) {
        return a * b;
    }

    @Override
    public final Integer divide(final Integer a, final Integer b) {
        return a / b;
    }

    @Override
    public final Integer negate(final Integer a) {
        return -a;
    }

    @Override
    public final Integer pow(final Integer a, final Integer b) {
        return null;
    }

    @Override
    public final Integer log(final Integer a, final Integer b) {
        return null;
    }

    @Override
    public final Integer sin(final Integer a) {
        return null;
    }

    @Override
    public final Integer cos(final Integer a) {
        return null;
    }

    @Override
    public final Integer toConst(String string) {
        return Integer.parseInt(string);
    }

    @Override
    public final Integer convertTo(final double x) {
        return (int) x;
    }

    @Override
    public final double convertFrom(final Integer x) {
        return x;
    }
}
