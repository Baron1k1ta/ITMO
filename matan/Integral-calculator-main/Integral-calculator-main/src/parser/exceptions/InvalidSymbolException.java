package parser.exceptions;

public class InvalidSymbolException extends ParserException {
    
    public InvalidSymbolException(final String message) {
        super(message);
    }

    public InvalidSymbolException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
