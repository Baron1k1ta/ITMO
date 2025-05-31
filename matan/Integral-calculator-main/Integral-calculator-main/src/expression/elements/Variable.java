package expression.elements;

import expression.AbstractExpression;

public class Variable<T> extends AbstractExpression<T> {

    @Override
    public T apply(final T x) {
        return x;
    }

    @Override
    public String toString() {
        return "x";
    }
}
