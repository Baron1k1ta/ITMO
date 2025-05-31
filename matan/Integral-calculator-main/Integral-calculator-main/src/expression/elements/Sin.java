package expression.elements;

import expression.AbstractExpression;
import expression.Expression;
import expression.operations.Operation;

public class Sin<T> extends AbstractExpression<T> {

    public final Expression<T> arg;

    public Sin(final Expression<T> arg, final Operation<T> operation) {
        this.arg = arg;
        this.operation = operation;
    }

    public Sin(final Expression<T> arg) {
        this(arg, null);
    }

    @Override
    public T apply(final T x) {
        return operation.sin(arg.apply(x));
    }

    @Override
    public String toString() {
        return String.format("sin(%s)", arg.toString());
    }
}
