package parser;

import java.util.Arrays;

public class Tokenizer {
    public final static String VALID = "\0";
    private int size;
    private int valueSize;
    private int pointer;
    private int valuePointer;
    private Elements[] elements;
    private String[] values;

    public Tokenizer() {
        this.pointer = 0;
        this.size = 0;
        this.valueSize = 0;
        this.valuePointer = 0;
        this.elements = new Elements[16];
        this.values = new String[16];
    }

    public void add(final Elements element) {
        if (size == elements.length) {
            elements = Arrays.copyOf(elements, elements.length * 2);
        }
        elements[size++] = element;
    }

    public void add(final Elements element, final String value) {
        if (valueSize == values.length) {
            values = Arrays.copyOf(values, values.length * 2);
        }
        values[valueSize++] = value;
        add(element);
    }

    public String element(final Elements compare) {
        checkValidity();
        if (elements[pointer] == compare) {
            pointer++;
            return compare == Elements.CONST ? values[valuePointer++] : VALID;
        }
        return null;
    }

    public Elements last() {
        return size > 0 ? elements[size - 1] : null;
    }

    public String checkElement() {
        checkValidity();
        return elements[pointer].toString();
    }

    private void checkValidity() {
        if (pointer >= size) {
            throw new IndexOutOfBoundsException("index is out of bounds");
        }
    }

    public void clear() {
        pointer = 0;
        size = 0;
        valuePointer = 0;
        valueSize = 0;
    }
}
