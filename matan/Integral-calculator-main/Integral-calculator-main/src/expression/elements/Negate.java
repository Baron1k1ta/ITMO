package expression.elements;

import expression.AbstractExpression;
import expression.Expression;
import expression.operations.Operation;

public class Negate<T> extends AbstractExpression<T> {

    public final Expression<T> arg;

    public Negate(final Expression<T> arg, final Operation<T> operation) {
        this.arg = arg;
        this.operation = operation;
    }

    public Negate(final Expression<T> arg) {
        this(arg, null);
    }

    @Override
    public T apply(final T x) {
        return operation.negate(arg.apply(x));
    }

    @Override
    public String toString() {
        return String.format("-(%s)", arg.toString());
    }
}
