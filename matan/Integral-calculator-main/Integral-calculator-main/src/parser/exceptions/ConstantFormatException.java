package parser.exceptions;

public class ConstantFormatException extends ParserException {

    public ConstantFormatException(final String message) {
        super(message);
    }

    public ConstantFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
