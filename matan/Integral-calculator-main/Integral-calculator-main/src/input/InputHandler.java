package input;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class InputHandler {

    private final Scanner scan;
    private final PrintStream out;

    public String toParse;
    public double a, b;
    public int splitCount;

    public InputHandler(final InputStream in, final PrintStream out) {
        this.scan = new Scanner(in);
        this.out = out;
    }

    public void set(final String toParse, final double a, final double b, final int splitCount) {
        this.toParse = toParse;
        this.a = a;
        this.b = b;
        this.splitCount = splitCount;
    }

    public void header() {
        out.println();
        out.println("█▀█ ▀█▀ █▄ ▄█ █▀▄  █  █   █▀█ █ █ █▄ ▄█ █▀█");
        out.println("█▄▀  █  █ ▀ █ █▄▄█ █▀▄█   █▄▄ █ █ █ ▀ █ █▄▄");
        out.println("█ █ ▄█▄ █   █ █  █ █  █   ▄▄█ ▀▄█ █   █ ▄▄█");
        out.println();
    }

    public void input() {
        out.print("===== Function =====> ");
        toParse = scan.nextLine();
        out.print("== Segment's ends ==> ");
        a = scan.nextDouble();
        b = scan.nextDouble();
        check();
        out.print("=== Terms number ===> ");
        splitCount = scan.nextInt();
        scan.nextLine();
    }

    public void output(final PrintStream out, final String expr, final double RiemannSum) {
        out.println();
        out.println(String.format("===== Function =====> %s", expr));
        out.println(String.format("==== Riemann sum ===> %.6f", RiemannSum));
        out.println();
    }

    private void check() {
        if (a > b) {
            swap();
        }
    }

    private void swap() {
        final double temp = a;

        a = b;
        b = temp;
    }
}
