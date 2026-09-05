package info.kgeorgiy.ja.baronov.hello;

import info.kgeorgiy.java.advanced.hello.NewHelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

/**
 * <p>
 * The {@code WebCrawler} class is responsible sending packets
 * to the server
 * </p>
 * @author Nikita Baronov
 * @see NewHelloClient
 */
public class HelloUDPClient implements NewHelloClient {
    final int socketTimeOut = 200;
    /**
     * Runs Hello client.
     * This method should return when all requests are completed.
     *
     * @param requests requests to perform in each thread.
     * @param threads  number of request threads.
     */
    @Override
    public void newRun(List<Request> requests, int threads) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(threads)) {
            for (int i = 1; i < threads + 1; i++) {
                final int threadNumber = i;

                executorService.submit(() -> {
                    try (DatagramSocket socket = new DatagramSocket()) {
                        socket.setSoTimeout(socketTimeOut);
                        for (Request request : requests) {
                            String message = request.template().replaceAll("\\$", String.valueOf(threadNumber));
                            final byte[] bytes = message.getBytes(StandardCharsets.UTF_8);

                            DatagramPacket sentPacket = new DatagramPacket(bytes, bytes.length, new InetSocketAddress(request.host(), request.port()));

                            DatagramPacket receivedPacket = new DatagramPacket(
                                    new byte[socket.getReceiveBufferSize()],
                                    socket.getReceiveBufferSize()
                            );

                            while (true) {
                                try {
                                    socket.send(sentPacket);
                                    socket.receive(receivedPacket);
                                    String string = new String(
                                            receivedPacket.getData(),
                                            receivedPacket.getOffset(),
                                            receivedPacket.getLength(),
                                            StandardCharsets.UTF_8
                                    );
                                    if (string.equals("Hello, " + message)) {
                                        System.out.println(string);
                                    } else {
                                        System.out.println("will retry " + message);
                                        continue;
                                    }
                                    break;
                                } catch (IOException e) {
                                    System.err.println("sending or receiving failed");
                                }
                            }
                        }
                    } catch (SocketException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    /**
     * The main method to launch the client.
     *
     * @param args Command-line arguments:
     *             - args[0] - the host name or IP address of the server.
     *             - args[1] - the port number to which the requests should be sent.
     *             - args[2] - the prefix to be used for the requests.
     *             - args[3] - the number of threads that will send requests concurrently.
     *             - args[4] - the number of requests each thread should send.
     */
    public static void main(String[] args) {
        HelloUDPClient client = new HelloUDPClient();
        HelloClientLauncher.main(args, client);
    }
}
