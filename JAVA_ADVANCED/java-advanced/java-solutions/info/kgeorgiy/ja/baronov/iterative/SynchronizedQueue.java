package info.kgeorgiy.ja.baronov.iterative;

import java.util.ArrayDeque;

/**
 * <p>
 * The {@code SynchronizedQueue} class is responsible for realisation of synchronized queue
 * </p>
 * @author Nikita Baronov
 */
public class SynchronizedQueue<T> {
    /**
     * A queue to store elements.
     */
    private final ArrayDeque<T> queue;

    /**
     *  Constructor for {@code SynchronizedQueue}, that creates {@link ArrayDeque} instance.
     */
    public SynchronizedQueue() {
        queue = new ArrayDeque<>();
    }

    /**
     * thread-safely adds an element to the {@link ArrayDeque} and notifies.
     * @param value the element to add to the queue
     */
    public synchronized void add(T value) {
        queue.add(value);
        notify();
    }

    /**
     * tries to return the oldest element in queue. Wait for elements, if queue is empty.
     */
    public synchronized T get() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.poll();
    }

}
