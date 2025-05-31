package expression.elements;

import expression.AbstractExpression;

public class Const<T> extends AbstractExpression<T> {

    public final T value;

    public Const(final T value) {
        this.value = value;
    }

    @Override
    public T apply(final T x) {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
