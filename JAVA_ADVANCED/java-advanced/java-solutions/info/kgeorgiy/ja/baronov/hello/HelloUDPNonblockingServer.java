package info.kgeorgiy.ja.baronov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.*;

import static java.nio.channels.SelectionKey.OP_READ;

/**
 * Non-blocking UDP server using a selector for I/O and a worker pool for
 * processing, ensuring continuous reads under flood without DoS.
 */
public class HelloUDPNonblockingServer implements HelloServer {
    private Selector selector;
    private DatagramChannel channel;
    private Thread channelThread;
    private ExecutorService workerPool;
    private int bufSize;
    private volatile boolean running = true;
    private final ConcurrentLinkedQueue<Response> responseQueue = new ConcurrentLinkedQueue<>();



    @Override
    public void start(int port, int threads) {
        try {
            selector = Selector.open();
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(port));
            bufSize = channel.socket().getReceiveBufferSize();

            channel.register(selector, OP_READ);

            workerPool = Executors.newFixedThreadPool(threads);
            channelThread = new Thread(this::runChannelThread);
            channelThread.start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start server", e);
        }
    }

    private void runChannelThread() {
        ByteBuffer buffer = ByteBuffer.allocate(bufSize);
        try {
            while (running && channel.isOpen()) {
                selector.select();
                Response resp;
                while ((resp = responseQueue.poll()) != null) {
                    channel.send(resp.buffer, resp.client);
                }
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isReadable()) {
                        buffer.clear();
                        SocketAddress client = channel.receive(buffer);
                        if (client != null) {
                            buffer.flip();
                            ByteBuffer requestBuf = ByteBuffer.allocate(buffer.remaining());
                            requestBuf.put(buffer);
                            requestBuf.flip();
                            workerPool.submit(() -> process(requestBuf, client));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private void process(ByteBuffer requestBuffer, SocketAddress client) {
        String request = StandardCharsets.UTF_8.decode(requestBuffer).toString();
        String response = "Hello, " + request;
        ByteBuffer respBuf = StandardCharsets.UTF_8.encode(response);
        responseQueue.add(new Response(respBuf, client));
        selector.wakeup();
    }


    @Override
    public void close() {
        running = false;
        selector.wakeup();

        try {
            if (channelThread != null) {
                channelThread.join();
            }
        } catch (InterruptedException e) {
            System.out.println("couldn't join thread");
        }
    }

    private void shutdown() {
        workerPool.shutdownNow();
        try {
            channel.close();
            selector.close();
        } catch (IOException ignored) {}
    }

    private static class Response {
        final ByteBuffer buffer;
        final SocketAddress client;
        Response(ByteBuffer buffer, SocketAddress client) {
            this.buffer = buffer;
            this.client = client;
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
        HelloUDPNonblockingServer server = new HelloUDPNonblockingServer();
        try {
            HelloServerLauncher.main(args, server);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
