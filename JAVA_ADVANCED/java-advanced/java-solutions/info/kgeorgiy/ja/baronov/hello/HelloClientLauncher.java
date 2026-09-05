package info.kgeorgiy.ja.baronov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

public class HelloClientLauncher {
    public static void main(String[] args, HelloClient client) {
        if (args.length != 5) {
            throw new IllegalArgumentException("5 arguments required");
        }

        try {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            String prefix = args[2];
            int threads = Integer.parseInt(args[3]);
            int requests = Integer.parseInt(args[4]);
            client.run(host, port, prefix, threads, requests);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid args " + e.getMessage());
        }
    }
}
