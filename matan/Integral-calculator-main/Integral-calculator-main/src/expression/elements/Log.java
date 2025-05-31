package expression.elements;

import expression.Expression;
import expression.operations.Operation;

public class Log<T> extends BinaryOperation<T> {

    public Log(final Expression<T> arg1, final Expression<T> arg2, final Operation<T> operation) {
        super(arg1, arg2, operation);
    }

    public Log(final Expression<T> arg1, final Expression<T> arg2) {
        super(arg1, arg2, null);
    }

    @Override
    public T calculate(T a, T b) {
        return operation.log(a, b);
    }

    @Override
    public String toString() {
        return String.format("log(%s)(%s)", arg1.toString(), arg2.toString());
    }
}
