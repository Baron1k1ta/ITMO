package parser;

import expression.Expression;

public interface Parser<T> {

    Expression<T> parse(String toParse) throws Exception;
}
