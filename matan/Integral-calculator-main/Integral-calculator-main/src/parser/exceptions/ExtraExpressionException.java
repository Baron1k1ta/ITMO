package parser.exceptions;

public class ExtraExpressionException extends ConstructionException {

    public ExtraExpressionException(final String message) {
        super(message);
    }

    public ExtraExpressionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
