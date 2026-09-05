
package info.kgeorgiy.ja.baronov.hello;

import info.kgeorgiy.java.advanced.hello.NewHelloClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

public class XHelloUDPNonblockingClient implements NewHelloClient {
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

        Selector selector;
        try {
            selector = Selector.open();
        } catch (IOException e) {
            return;
        }
        for (int i = 1; i < threads + 1; i++) {
            try {
                DatagramChannel channel = DatagramChannel.open();
                channel.configureBlocking(false);
                channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                channel.register(selector, OP_WRITE, new int[]{i, 0});

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


        while (!Thread.interrupted() && !selector.keys().isEmpty()) {

            try {
                selector.select(socketTimeOut);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (selector.selectedKeys().isEmpty()) {
                for (final SelectionKey key : selector.keys()) {
                    key.interestOps(OP_WRITE);
                }
            }

            for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
                SelectionKey key = it.next();
                DatagramChannel channel = (DatagramChannel) key.channel();
                int[] attachment = (int[]) key.attachment();
                it.remove();
                if (attachment[1] < requests.size()) {
                    Request request = requests.get(attachment[1]);
                    String message = request.template().replaceAll("\\$", String.valueOf(attachment[0]));

                    if (key.isReadable()) {
                        final ByteBuffer receiveBuffer;
                        try {
                            receiveBuffer = ByteBuffer.allocate(channel.getOption(StandardSocketOptions.SO_RCVBUF));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        try {
                            channel.receive(receiveBuffer);
                            receiveBuffer.flip();
                        } catch (IOException exception) {
                            System.out.println("Receiving failed");
                            continue;
                        }


                        String string = new String(receiveBuffer.array(), receiveBuffer.position(), receiveBuffer.remaining(), StandardCharsets.UTF_8);
                        if (string.equals("Hello, " + message)) {
                            attachment[1]++;
                            System.out.println(string);
                        }
                        if(attachment[1] < requests.size()) {
                            key.interestOps(OP_WRITE);
                        }else{
                            key.cancel();
                            try {
                                channel.close();
                            } catch (IOException ignored) {}
                        }

                    } else {
                        final ByteBuffer sendBuffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
                        try {

                            channel.send(sendBuffer, new InetSocketAddress(request.host(), request.port()));
                            key.interestOps(OP_READ);
                        } catch (IOException exception) {
                            System.out.println("Sending failed");
                        }
                    }
                }

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
