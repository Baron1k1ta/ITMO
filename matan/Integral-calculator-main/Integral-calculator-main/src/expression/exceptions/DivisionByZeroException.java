package expression.exceptions;

public class DivisionByZeroException extends ExpressionException {
    public static final String MESSAGE = "division by zero";

    public DivisionByZeroException() {
        super(MESSAGE);
    }

    public DivisionByZeroException(final String message) {
        super(message);
    }

    public DivisionByZeroException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DivisionByZeroException(final Throwable cause) {
        super(MESSAGE, cause);
    }
}
