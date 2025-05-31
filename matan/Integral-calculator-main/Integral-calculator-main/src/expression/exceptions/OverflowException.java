package expression.exceptions;

public class OverflowException extends ExpressionException {
    public static final String MESSAGE = "overflow";

    public OverflowException() {
        super(MESSAGE);
    }

    public OverflowException(final String message) {
        super(message);
    }

    public OverflowException(final Throwable cause) {
        super(MESSAGE, cause);
    }

    public OverflowException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
