package expression.exceptions;

public class InvalidFunctionArgumentException extends ExpressionException {
    public InvalidFunctionArgumentException(final String message) {
        super(message);
    }

    public InvalidFunctionArgumentException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
