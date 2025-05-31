package expression.elements;

import expression.Expression;
import expression.operations.Operation;

public class Multiply<T> extends BinaryOperation<T> {

    public Multiply(final Expression<T> arg1, final Expression<T> arg2, final Operation<T> operation) {
        super(arg1, arg2, operation);
    }

    public Multiply(final Expression<T> arg1, final Expression<T> arg2) {
        super(arg1, arg2, null);
    }

    @Override
    public T calculate(T a, T b) {
        return operation.multiply(a, b);
    }

    @Override
    protected String sign() {
        return "*";
    }
}
