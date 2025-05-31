package parser.exceptions;

public class ConstructionException extends ParserException {

    public ConstructionException(final String message) {
        super(message);
    }

    public ConstructionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
