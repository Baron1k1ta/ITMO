package info.kgeorgiy.ja.baronov.hello;


import info.kgeorgiy.java.advanced.hello.HelloServer;
import info.kgeorgiy.java.advanced.hello.NewHelloClient;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * The {@code WebCrawler} class is responsible getting packets
 * from the client
 * </p>
 * @author Nikita Baronov
 * @see NewHelloClient
 */
public class HelloUDPServer implements HelloServer {
    final int socketTimeOut = 200;
    private ExecutorService executorService;
    private DatagramSocket socket;

    /**
     * Starts a new Hello server.
     * This method should return immediately.
     *
     * @param port server port.
     * @param threads number of working threads.
     */
    @Override
    public void start(int port, int threads) {

        executorService = Executors.newFixedThreadPool(threads);
        try {
            socket = new DatagramSocket(port);
            socket.setSoTimeout(socketTimeOut);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                try {
                    DatagramPacket receivedPacket = new DatagramPacket(
                            new byte[socket.getReceiveBufferSize()],
                            socket.getReceiveBufferSize()

                    );
                    while (!socket.isClosed()) {
                        try {
                            socket.receive(receivedPacket);
                            DatagramPacket responsePacket = getDatagramPacket(receivedPacket);
                            socket.send(responsePacket);

                        } catch (IOException e) {
                            System.err.println("sending or receiving failed");
                        }
                    }
                } catch (SocketException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static DatagramPacket getDatagramPacket(DatagramPacket receivedPacket) {
        String request = new String(receivedPacket.getData(),
                receivedPacket.getOffset(),
                receivedPacket.getLength(),
                StandardCharsets.UTF_8);

        String response = "Hello, " + request;

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        return new DatagramPacket(
                responseBytes, responseBytes.length, receivedPacket.getSocketAddress()
        );
    }

    /**
     * Stops server and deallocates all resources.
     */
    @Override
    public void close() {
        if (executorService != null) {
            executorService.shutdownNow();
        }

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
    /**
     * The main method to launch the HelloUDPServer.
     *
     * @param args Command-line arguments:
     *             - args[0] - the port number the server will listen on.
     *             - args[1] - the number of threads to handle incoming requests.
     */
    public static void main(String[] args) {
        HelloUDPServer server = new HelloUDPServer();
        try {
            HelloServerLauncher.main(args, server);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}