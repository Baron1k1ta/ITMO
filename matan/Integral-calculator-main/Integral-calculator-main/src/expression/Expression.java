package expression;

import expression.operations.Operation;

public interface Expression<T> {
    /*
     * Вычисляет значение выражения в точке x
     */
    T apply(T x) throws ArithmeticException;

    void setOperation(Operation<T> operation);

    Operation<T> getOperation();
}
