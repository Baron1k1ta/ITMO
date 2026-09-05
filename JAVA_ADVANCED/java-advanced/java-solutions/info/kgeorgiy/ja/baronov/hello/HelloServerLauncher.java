package info.kgeorgiy.ja.baronov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;

public class HelloServerLauncher {
    public static void main(String[] args, HelloServer server) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("2 arguments required: port and number of threads");
        }
        try {
            int port = Integer.parseInt(args[0]);
            int threads = Integer.parseInt(args[1]);

            server.start(port, threads);

            System.out.println("Press Enter to stop the server...");
            System.in.read();
            server.close();

            System.out.println("Server stopped.");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid args " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error while closing the server: " + e.getMessage());
        }
    }
}
