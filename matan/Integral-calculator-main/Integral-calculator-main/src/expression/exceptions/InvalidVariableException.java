package expression.exceptions;

public class InvalidVariableException extends ExpressionException {
    public InvalidVariableException(final String message) {
        super(message);
    }

    public InvalidVariableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
