package info.kgeorgiy.ja.baronov.iterative;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;


/**
 * <p>
 * The {@code ParallelMapperImpl} class is responsible for parallel execution of tasks
 * </p>
 *
 * @author Nikita Baronov
 * @see ParallelMapper
 */
public class ParallelMapperImpl implements ParallelMapper {
    /**
     * A list of all threads.
     */
    private final List<Thread> threadList = new ArrayList<>();
    /**
     * A Queue for tasks.
     */
    private final SynchronizedQueue<Runnable> queue = new SynchronizedQueue<>();

    /**
     * Constructor for {@code ParallelMapperImpl} with fixed number of threads.
     * Each thread will execute tasks from the task queue.
     *
     * @param threads the number of threads to use for parallel execution
     */
    public ParallelMapperImpl(int threads) {
        for (int i = 0; i < threads; ++i) {
            final Thread thread = Thread.ofPlatform().start(() -> {
                try {
                    while (!Thread.interrupted()) {
                        try {
                            queue.get().run();
                        } catch (RuntimeException ignored) {
                        }
                    }
                } catch (InterruptedException ignored) {
                }
            });
            threadList.add(thread);
        }
    }


    /**
     * Maps function {@code f} over specified {@code items}.
     * Mapping for each item is performed in parallel.
     *
     * @throws InterruptedException if calling thread was interrupted
     */
    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> items) throws InterruptedException {

        final List<Task<R>> tasks = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            final int finalI = i;
            Task<R> task = new Task<>();
            tasks.add(task);
            queue.add(() -> {
                try {
                    task.setResult(f.apply(items.get(finalI)));
                } catch (RuntimeException e) {
                    task.setException(e);
                } finally {
                    synchronized (this) {
                        task.setDone();
                        this.notify();
                    }
                }
            });
        }

        final List<R> results = new ArrayList<>(Collections.nCopies(items.size(), null));

        RuntimeException exception = null;

        for (int i = 0; i < items.size(); i++) {

            Task<R> task = tasks.get(i);

            synchronized (this) {
                while (!task.isDone()) {
                    this.wait();
                }

                results.set(i, task.getResult());

                if (task.getException() != null) {
                    if (exception == null) {
                        exception = task.getException();
                    } else {
                        exception.addSuppressed(task.getException());
                    }
                }
            }
        }

        if (exception != null) {
            throw exception;
        }

        return results;
    }

    /**
     * Stops all threads.
     * <p>Easy version: all unfinished mappings are left in undefined state.</p>
     * <p>Hard version: all unfinished mappings should throw exception.</p>
     */
    @Override
    public void close() {
        threadList.forEach(Thread::interrupt);
        threadList.forEach(thread -> {
            while (true) {
                try {
                    thread.join();
                    break;
                } catch (InterruptedException ignored) {
                }
            }
        });
    }

    private static class Task<R> {
        private R result;
        private boolean isDone;
        private RuntimeException exception;

        public Task() {
            result = null;
            isDone = false;
            exception = null;
        }

        public R getResult() {
            return result;
        }

        public void setResult(R result) {
            this.result = result;
            this.isDone = true;
        }

        public RuntimeException getException() {
            return exception;
        }

        public void setException(RuntimeException exception) {
            this.exception = exception;
        }

        public boolean isDone() {
            return isDone;
        }

        public void setDone() {
            isDone = true;
        }
    }
}
