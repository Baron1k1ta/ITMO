package parser;

import expression.Expression;
import expression.elements.*;
import expression.operations.Operation;
import parser.exceptions.*;

public class ExpressionParser<T> extends StringParser implements Parser<T> {
    private final Tokenizer data;
    private String savedValue;
    private final Operation<T> operation;

    public ExpressionParser(final Operation<T> operation) {
        this.data = new Tokenizer();
        this.savedValue = null;
        this.operation = operation;
    }

    @Override
    public Expression<T> parse(final String toParse) throws ParserException {
        data.clear();
        resetStringParser(toParse);
        parseExpressionElements();
        return getExpressionByRule_0();
    }

    private void parseExpressionElements() throws InvalidSymbolException, InvalidCombination {
        skipWhitespace();
        while (state()) {
            toExpressionElement(check());
            skipWhitespace();
        }
        data.add(Elements.EOF);
    }

    private void toExpressionElement(final char c) throws InvalidSymbolException, InvalidCombination {
        Elements element = switch (c) {
            case '^' -> Elements.POW;
            case '+' -> Elements.ADD;
            case '-' -> Elements.SUBTRACT;
            case '/' -> Elements.DIVIDE;
            case '*' -> Elements.MULTIPLY;
            case '(' -> Elements.LEFT_PARENTHESES;
            case ')' -> Elements.RIGHT_PARENTHESES;
            case '{' -> Elements.LEFT_BRACE;
            case '}' -> Elements.RIGHT_BRACE;
            case '[' -> Elements.LEFT_BRACKET;
            case ']' -> Elements.RIGHT_BRACKET;
            default -> null;
        };

        if (element == null) {
            detailedAnalysis(c);
        } else {
            data.add(element);
            take();
        }
    }

    private void detailedAnalysis(final char c) throws InvalidSymbolException, InvalidCombination {
        if (getConst()) {
            data.add(Elements.CONST, savedValue);
        } else if (getVariable()) {
            data.add(Elements.VARIABLE);
        } else if (checkElement("log", Elements.LOG) ||
                checkElement("pi", Elements.PI) ||
                checkElement("e", Elements.E) ||
                checkElement("cos", Elements.COS) ||
                checkElement("sin", Elements.SIN)) {
            return;
        } else {
            throw new InvalidSymbolException(joinPosition(String.format("invalid char found - `%c`", c)));
        }
    }

    private boolean checkElement(final String name, final Elements element) throws InvalidCombination {
        if (take(name)) {
            checkCombinationException(name);
            data.add(element);
            return true;
        }
        return false;
    }

    private boolean getConst() {
        final StringBuilder sb;

        if (Character.DECIMAL_DIGIT_NUMBER != Character.getType(check())) {
            return false;
        }
        sb = new StringBuilder();
        while (Character.getType(check()) == Character.DECIMAL_DIGIT_NUMBER || check() == '.') {
            sb.append(take());
        }
        savedValue = sb.toString();
        return true;
    }

    private boolean getVariable() {
        return take("x");
    }

    /*
     * EXPRESSION RULES
     */

    private Expression<T> getExpressionByRule_0() throws ParserException {
        Expression<T> main = getExpressionByRule_1();

        if (compare(Elements.EOF)) {
            if (main.getOperation() == null) {
                main.setOperation(operation);
            }
            return main;
        }
        throw new ExtraExpressionException(String.format("extra expression after `%s`", main.toString()));
    }

    private Expression<T> getExpressionByRule_1() throws ParserException {
        if (compare(Elements.EOF)
                || compare(Elements.RIGHT_PARENTHESES)
                || compare(Elements.RIGHT_BRACE)
                || compare(Elements.RIGHT_BRACKET)) {
            throw new EmptyExpressionException("empty expression found");
        }
        return getExpressionByRule_2(Elements.MIN_PRIORITY);
    }

    private Expression<T> getExpressionByRule_2(final int currentPriority) throws ParserException {
        if (currentPriority >= Elements.PRIORITIES.length) {
            return getExpressionByRule_3();
        }

        final int nextPriority = currentPriority + 1;
        Expression<T> main = getExpressionByRule_2(nextPriority);

        rerun: while (true) {
            for (Elements e : Elements.PRIORITIES[currentPriority]) {
                if (compare(e)) {
                    main = Elements.combine(e, main, getExpressionByRule_2(nextPriority), operation);
                    continue rerun;
                }
            }
            break rerun;
        }
        return main;
    }

    private Expression<T> getExpressionByRule_3() throws ParserException {
        if (compare(Elements.LOG)) {
            return new Log<T>(getExpressionByRule_3(), getExpressionByRule_3(), operation);
        }
        if (compare(Elements.SIN)) {
            return new Sin<T>(getExpressionByRule_3(), operation);
        }
        if (compare(Elements.COS)) {
            return new Cos<T>(getExpressionByRule_3(), operation);
        }
        if (compare(Elements.E)) {
            return new Const<T>(operation.convertTo(Math.E));
        }
        if (compare(Elements.PI)) {
            return new Const<T>(operation.convertTo(Math.PI));
        }
        if (compareWithSave(Elements.CONST)) {
            try {
                return new Const<T>(operation.toConst(savedValue));
            } catch (NumberFormatException exception) {
                throw new ConstantFormatException(formatMessage());
            }
        }
        if (compareWithSave(Elements.VARIABLE)) {
            return new Variable<T>();
        }
        if (compare(Elements.SUBTRACT)) {
            if (compareWithSave(Elements.CONST)) {
                savedValue = "-" + savedValue;
                try {
                    return new Const<T>(operation.toConst(savedValue));
                } catch (NumberFormatException exception) {
                    throw new ConstantFormatException(formatMessage());
                }
            }
            return new Negate<T>(getExpressionByRule_3(), operation);
        }
        if (compare(Elements.LEFT_PARENTHESES)) {
            return completeExpression(Elements.RIGHT_PARENTHESES);
        }
        if (compare(Elements.LEFT_BRACE)) {
            return completeExpression(Elements.RIGHT_BRACE);
        }
        if (compare(Elements.LEFT_BRACKET)) {
            return completeExpression(Elements.RIGHT_BRACKET);
        }
        throw new ConstructionException(String.format("not valid argument found - `%s`", data.checkElement()));
    }

    /*
     * Some usefull methods
     */

    private Expression<T> completeExpression(final Elements end) throws ParserException {
        final Expression<T> e = getExpressionByRule_1();

        if (compare(end)) {
            return e;
        }
        throw new ConstructionException("closing parenthesis not found");
    }

    private boolean compare(final Elements e) {
        return data.element(e) != null;
    }

    private boolean compareWithSave(final Elements e) {
        return (savedValue = data.element(e)) != null;
    }

    private void checkCombinationException(final String name) throws InvalidCombination {
        if (getVariable() || getConst()) {
            throw new InvalidCombination(String.format("invalid symbol combination found - `%s%s`", name, savedValue));
        }
    }

    private String joinPosition(final String message, final int position) {
        return String.format("%s, position - %d", message, position);
    }

    private String formatMessage() {
        return String.format("invalid format value - `%s", savedValue);
    }

    private String joinPosition(final String message) {
        return joinPosition(message, pointerValue() + 1);
    }
}
