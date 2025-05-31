package parser;

import expression.Expression;
import expression.elements.*;
import expression.operations.Operation;

public enum Elements {
    COS,
    SIN,
    E,
    PI,
    LOG,
    POW,
    RIGHT_BRACKET,
    LEFT_BRACKET,
    RIGHT_BRACE,
    LEFT_BRACE,
    RIGHT_PARENTHESES,
    LEFT_PARENTHESES,
    CONST,
    VARIABLE,
    EOF,
    ADD,
    SUBTRACT,
    DIVIDE,
    MULTIPLY;

    public static final int MIN_PRIORITY = 0;

    public static final Elements[][] PRIORITIES = new Elements[][] {
            { Elements.ADD, Elements.SUBTRACT },
            { Elements.DIVIDE, Elements.MULTIPLY },
            { Elements.POW }
    };

    public static <T> Expression<T> combine(final Elements e, final Expression<T> main,
            final Expression<T> additional, final Operation<T> operation) {
        return switch (e) {
            case ADD -> new Add<T>(main, additional, operation);
            case SUBTRACT -> new Subtract<T>(main, additional, operation);
            case DIVIDE -> new Divide<T>(main, additional, operation);
            case MULTIPLY -> new Multiply<T>(main, additional, operation);
            case LOG -> new Log<T>(main, additional, operation);
            case POW -> new Pow<T>(main, additional, operation);
            default -> throw new AssertionError("expression element not found");
        };
    }
}