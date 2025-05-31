package expression.elements;

import expression.AbstractExpression;
import expression.Expression;
import expression.operations.Operation;

public class Cos<T> extends AbstractExpression<T> {

    public final Expression<T> arg;

    public Cos(final Expression<T> arg, final Operation<T> operation) {
        this.arg = arg;
        this.operation = operation;
    }

    public Cos(final Expression<T> arg) {
        this(arg, null);
    }

    @Override
    public T apply(final T x) {
        return operation.cos(arg.apply(x));
    }

    @Override
    public String toString() {
        return String.format("cos(%s)", arg.toString());
    }
}
