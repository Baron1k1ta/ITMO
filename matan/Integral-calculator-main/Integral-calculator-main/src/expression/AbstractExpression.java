package expression;

import expression.operations.Operation;

public abstract class AbstractExpression<T> implements Expression<T> {

    protected Operation<T> operation;

    @Override
    public void setOperation(final Operation<T> operation) {
        this.operation = operation;
    }

    @Override
    public Operation<T> getOperation() {
        return operation;
    }
}
