package info.kgeorgiy.ja.baronov.iterative;

import info.kgeorgiy.java.advanced.iterative.ScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;


/**
 * <p>
 * The {@code IterativeParallelism} class is responsible for parallel execution
 * </p>
 *
 * @author Nikita Baronov
 * @see ParallelMapper
 */
public class IterativeParallelism implements ScalarIP {
    private final ParallelMapper parallelMapper;

    /**
     * Constructor for {@code IterativeParallelism} with {@code ParallelMapperImpl} instance.
     *
     * @param parallelMapper instance of {@code ParallelMapperImpl}.
     */
    public IterativeParallelism(ParallelMapper parallelMapper) {
        this.parallelMapper = parallelMapper;
    }

    /**
     * Default constructor to create an instance of Implementor.
     */
    public IterativeParallelism() {
        this.parallelMapper = null;
    }


    private <R> R parallelRun(int threads,
                              int len,
                              Function<IntStream, R> task,
                              Function<List<R>, R> resultProcessor) throws InterruptedException {


        threads = Math.max(1, Math.min(threads, len));

        int size = len / threads;
        int addition = len % threads;
        int right_border = 0;

        List<IntStream> items = new ArrayList<>(threads);

        for (int i = 0; i < threads; ++i) {
            final int l = right_border;
            right_border = l + size + (addition > 0 ? 1 : 0);
            addition--;
            final int r = right_border;
            items.add(IntStream.range(l, r));
        }

        if (parallelMapper != null) {
            List<R> results = parallelMapper.map(task, items);
            return resultProcessor.apply(results);
        }


        List<Thread> threadList = new ArrayList<>();
        List<R> results = new ArrayList<>(Collections.nCopies(threads, null));

        for (int i = 0; i < threads; ++i) {
            final int index = i;
            final IntStream range = items.get(i);

            Thread thread = Thread.ofPlatform().start(() -> {
                R result = task.apply(range);
                results.set(index, result);
            });
            threadList.add(thread);

        }

        final Exception resultException = new Exception();
        boolean isInterrupted = false;

        for (Thread thread : threadList) {
            while (true) {
                try {
                    thread.join();
                    break;
                } catch (InterruptedException e) {
                    isInterrupted = true;
                    resultException.addSuppressed(e);
                    System.err.println("Can't join thread: " + thread.getName() + "will try again");
                }
            }
        }

        if (isInterrupted) {
            Thread.currentThread().interrupt();
            throw new InterruptedException(resultException.getLocalizedMessage());
        }

        return resultProcessor.apply(results);
    }


    private <T> int argExtrema(int threads, List<T> values, Comparator<? super T> comparator) throws InterruptedException {
        if (values.isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        return parallelRun(
                threads,
                values.size(),
                stream -> stream.reduce((i, j) ->
                        comparator.compare(values.get(i), values.get(j)) >= 0 ? i : j
                ).orElseThrow(() -> new NoSuchElementException("No extrema value found")),
                results -> results.stream()
                        .reduce((i, j) -> comparator.compare(values.get(i), values.get(j)) > 0 ? i : (comparator.compare(values.get(i), values.get(j)) == 0 ? Math.min(i, j) : j))
                        .orElseThrow(() -> new NoSuchElementException("No extrema value found"))
        );
    }


    /**
     * Returns index of the first maximum.
     *
     * @param threads    number of concurrent threads.
     * @param values     values to find maximum in.
     * @param comparator value comparator.
     * @param <T>        value type.
     * @return index of the first maximum in given values.
     * @throws InterruptedException             if executing thread was interrupted.
     * @throws java.util.NoSuchElementException if no values are given.
     */
    @Override
    public <T> int argMax(int threads, List<T> values, Comparator<? super T> comparator) throws InterruptedException {
        return argExtrema(threads, values, comparator);
    }

    /**
     * Returns index of the first minimum.
     *
     * @param threads    number of concurrent threads.
     * @param values     values to find minimum in.
     * @param comparator value comparator.
     * @param <T>        value type.
     * @return index of the first minimum in given values.
     * @throws InterruptedException             if executing thread was interrupted.
     * @throws java.util.NoSuchElementException if no values are given.
     */
    @Override
    public <T> int argMin(int threads, List<T> values, Comparator<? super T> comparator) throws InterruptedException {
        return argExtrema(threads, values, comparator.reversed());
    }

    private <T> int findIndex(int threads, List<T> values, Predicate<? super T> predicate, boolean isLast) throws InterruptedException {
        return parallelRun(
                threads,
                values.size(),
                stream -> stream.filter(i -> predicate.test(values.get(i)))
                        .reduce(isLast ? (a, b) -> b : (a, b) -> a)
                        .orElse(-1),
                results -> results.stream()
                        .filter(i -> i != -1)
                        .reduce(isLast ? Math::max : Math::min)
                        .orElse(-1)
        );
    }

    /**
     * Returns the index of the first value satisfying a predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param <T>       value type.
     * @return index of the first value satisfying the predicate, or {@code -1}, if there are none.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> int indexOf(int threads, List<T> values, Predicate<? super T> predicate) throws InterruptedException {
        return findIndex(threads, values, predicate, false);
    }

    /**
     * Returns the index of the last value satisfying a predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param <T>       value type.
     * @return index of the last value satisfying the predicate, or {@code -1}, if there are none.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> int lastIndexOf(int threads, List<T> values, Predicate<? super T> predicate) throws InterruptedException {
        return findIndex(threads, values, predicate, true);
    }

    /**
     * Returns sum of the indices of the values satisfying a predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param <T>       value type.
     * @return sum of the indices of values satisfying the predicate.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> long sumIndices(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return parallelRun(
                threads,
                values.size(),
                stream -> stream.filter(i -> predicate.test(values.get(i))).mapToLong(i -> i).sum(),
                results -> results.stream().mapToLong(Long::longValue).sum()
        );

    }
}



