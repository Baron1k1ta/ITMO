package expression.elements;

import expression.AbstractExpression;
import expression.Expression;
import expression.operations.Operation;

public abstract class BinaryOperation<T> extends AbstractExpression<T> {

    public final Expression<T> arg1, arg2;

    public BinaryOperation(final Expression<T> arg1, final Expression<T> arg2, final Operation<T> operation) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.operation = operation;
    }

    protected abstract T calculate(final T a, final T b);

    protected String sign() {
        return "?";
    }

    @Override
    public void setOperation(final Operation<T> operation) {
        this.operation = operation;
        arg1.setOperation(operation);
        arg2.setOperation(operation);
    }

    @Override
    public T apply(final T x) {
        return calculate(arg1.apply(x), arg2.apply(x));
    }

    @Override
    public String toString() {
        return "(" + arg1 + " " + sign() + " " + arg2 + ")";
    }
}