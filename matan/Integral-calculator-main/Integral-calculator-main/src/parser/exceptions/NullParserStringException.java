package parser.exceptions;

public class NullParserStringException extends ParserException {
    
    public NullParserStringException(final String message) {
        super(message);
    }

    public NullParserStringException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
