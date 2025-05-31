package parser.exceptions;

public class InvalidCombination extends ConstructionException {

    public InvalidCombination(final String message) {
        super(message);
    }

    public InvalidCombination(final String message, final Throwable cause) {
        super(message, cause);
    }
}
