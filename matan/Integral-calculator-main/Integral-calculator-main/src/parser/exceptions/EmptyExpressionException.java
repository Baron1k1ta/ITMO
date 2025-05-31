package parser.exceptions;

public class EmptyExpressionException extends ConstructionException {

    public EmptyExpressionException(final String message) {
        super(message);
    }

    public EmptyExpressionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
