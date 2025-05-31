package parser;

import parser.exceptions.NullParserStringException;

public abstract class StringParser {
    protected final char END = 0;
    private boolean locker = false;
    private char[] buffer;
    private int pointer;

    protected void skipWhitespace() {
        while (Character.isWhitespace(check())) {
            take();
        }
    }

    protected void resetStringParser(final String string) throws NullParserStringException {
        if (string == null) {
            throw new NullParserStringException("null string argument");
        }
        buffer = string.toCharArray();
        pointer = 0;
        locker = buffer.length > 0;
    }

    protected char take() {
        if (locker) {
            locker = pointer + 1 < buffer.length;
            return buffer[pointer++];
        } else {
            return END;
        }
    }

    protected int pointerValue() {
        return pointer;
    }

    protected char check() {
        return locker ? buffer[pointer] : END;
    }

    protected boolean take(final String string) {
        final char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!take(chars[i])) {
                pointer -= i;
                return false;
            }
        }
        return true;
    }

    protected boolean take(final char target) {
        if (locker && buffer[pointer] == target) {
            pointer++;
            locker = pointer < buffer.length;
            return true;
        }
        return false;
    }

    protected boolean state() {
        return locker;
    }
}
