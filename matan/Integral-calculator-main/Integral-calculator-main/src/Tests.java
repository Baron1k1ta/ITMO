import java.util.Scanner;

import input.InputHandler;

final class Test {

    public static final int SPLIT_COUNT = 100;
    private static double EPS = 0.01;

    public final String toParse;
    public final double a, b;
    public final double correct;

    public Test(final String toParse, final double a, final double b, final double correct) {
        this.toParse = toParse;
        this.a = a;
        this.b = b;
        this.correct = correct;
    }

    public Test(final String toParse, final double a, final double b) {
        this(toParse, a, b, Double.NaN);
    }

    public InputHandler fill(final InputHandler handler) {
        handler.set(toParse, a, b, SPLIT_COUNT);
        return handler;
    }

    @Override
    public String toString() {
        return String.format("f = %s [%s, %s]-[%s]", toParse, a, b, SPLIT_COUNT);
    }

    public boolean check(final double answer) {
        return Math.abs(correct - answer) <= EPS || Double.isNaN(correct);
    }
}

public class Tests {

    public static final Test[] testArray = new Test[] {
            new Test("4^x", 1.0, 2.0, 8.65617),
            new Test("5^x", 0.0, 3.0, 77.04553),
            new Test("2^x", 0.0, 2.0, 4.32809),
            new Test("e^(-x)", 0.0, 2.0, 0.864665),
            new Test("x^3", -2.0, 0.0, -4),

            new Test("e^(-2*x)", 1, 3),
            new Test("x^2", 1, 2),
            new Test("e^x", 0, 1),
            new Test("sin x", 0, Math.PI),
            new Test("cos x", 0, Math.PI / 2),
            new Test("x^3", 0, 1),
            new Test("3^x", 1, 2),
            new Test("e^(-x)", 0, 1),
            new Test("x^2", -3, 0),
            new Test("e^(2*x)", 0, 1),
            new Test("sin x", 0, 2 * Math.PI),
            new Test("cos x", 0, Math.PI),
            new Test("2^x", 0, 1),
            new Test("x^3", 0, 2),
            new Test("3^x", -1, 0),
            new Test("x^2", -1, 1),
            new Test("e^(3*x)", 0, 0.5),
            new Test("sin (2*x)", 0, Math.PI),
            new Test("cos (2*x)", 0, Math.PI / 2),
            new Test("4^x", 0, 2),
            new Test("x^2", 1, 4),
            new Test("e^(2*x)", -1, 0),
            new Test("sin (2*x)", 0, Math.PI / 2),
            new Test("cos (2*x)", 0, Math.PI),
            new Test("x^3", -1, 1),
            new Test("3^x", -1, 1),
            new Test("e^(-x)", -1, 1),
    };

    public static void main(String[] args) throws Exception {
        final InputHandler handler = new InputHandler(System.in, System.out);
        boolean completed;

        try (final Scanner stop = new Scanner(System.in)) {
            for (final Test test : testArray) {
                System.out.println(String.format("[TEST] %s", test.toString()));
                completed = test.check(Main.launch(test.fill(handler)));
                System.out.print(String.format("[%s] Press enter...", completed ? "PASSED" : "FAILED"));
                stop.nextLine();
            }
        }

        System.out.println("\n[TESTS COMPLETED]");
    }
}
