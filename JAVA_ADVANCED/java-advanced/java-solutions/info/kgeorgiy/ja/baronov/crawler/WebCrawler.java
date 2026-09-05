package info.kgeorgiy.ja.baronov.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;

/**
 * <p>
 * The {@code WebCrawler} class is responsible for crawling sites
 * </p>
 *
 * @author Nikita Baronov
 * @see NewCrawler
 */
public class WebCrawler implements NewCrawler {
    private final Downloader downloader;
    private final ExecutorService downloaders;
    private final ExecutorService extractors;

    /**
     * The main method to launch the program.
     *
     * @param args Command-line arguments:
     *             - args[0] - the URL to start the crawling from.
     *             - args[1] - depth of crawling (optional, default is 1).
     *             - args[2] - number of downloader threads (optional, default is 1).
     *             - args[3] - number of extractor threads (optional, default is 1).
     *             - args[4] - number of threads per host (optional, default is 1).
     */

    public static void main(String[] args) {
        if (args == null || args.length < 1 || args.length > 5) { //note -- args[] can be null
            System.err.println("incorrect input");
            return;
        }

        try {
            WebCrawler webCrawler = new WebCrawler(
                    new CachingDownloader(1),
                    getOrDefault(args, 2, 1),
                    getOrDefault(args, 3, 1),
                    getOrDefault(args, 4, 1)
            );
            List<String> excludes = Collections.emptyList();
            Result result = webCrawler.download(
                    args[0],
                    getOrDefault(args, 1, 1),
                    excludes
            );
            System.out.println("Downloaded: " + result.getDownloaded());
            System.err.println("Errors: " + result.getErrors());
            webCrawler.close();
        } catch (IOException e) {
            System.err.println("Unable to create CachingDownloader: " + e.getMessage());
        }
    }

    private static int getOrDefault(String[] args, int index, int defaultValue) {
        try {
            return args.length > index ? Integer.parseInt(args[index]) : defaultValue;
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format at index " + index + ". Using default value: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Constructs a {@code WebCrawler} with the given parameters.
     *
     * @param downloader  the {@link Downloader} instance used to download web pages.
     * @param downloaders the number of threads for downloading.
     * @param extractors  the number of threads for extracting.
     * @param perHost     the maximum number of downloads per host.
     */
    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloaders = Executors.newFixedThreadPool(downloaders);
        this.extractors = Executors.newFixedThreadPool(extractors);
    }

    /**
     * Downloads website up to specified depth.
     *
     * @param url      start URL.
     * @param depth    download depth.
     * @param excludes hosts containing one of given substrings are ignored.
     * @return download result.
     */
    @Override
    public Result download(String url, int depth, List<String> excludes) {
        final Set<String> visited = ConcurrentHashMap.newKeySet();
        final Set<String> downloaded = ConcurrentHashMap.newKeySet();
        final ConcurrentMap<String, IOException> errors = new ConcurrentHashMap<>();

        List<String> currentLevel = Collections.singletonList(url);

        for (int d = 0; d < depth; d++) {
            ConcurrentLinkedQueue<String> nextLevel = new ConcurrentLinkedQueue<>();
            Phaser phaser = new Phaser(1);

            Predicate<String> predicate = link -> !shouldExclude(link, excludes);

            for (String link : currentLevel) {
                phaser.register();

                downloaders.submit(() -> {
                    try {
                        if (!visited.add(link) || !predicate.test(link)) {
                            return;
                        }
                        Document page = downloader.download(link);
                        downloaded.add(link);

                        phaser.register();
                        extractors.submit(() -> {
                            try {
                                nextLevel.addAll(page.extractLinks());
                            } catch (IOException e) {
                                errors.put(link, e);
                            } finally {
                                phaser.arriveAndDeregister();
                            }
                        });
                    } catch (IOException e) {
                        errors.put(link, e);
                    } finally {
                        phaser.arriveAndDeregister();
                    }
                });

            }

            phaser.arriveAndAwaitAdvance();
            currentLevel = new ArrayList<>(nextLevel);
        }

        return new Result(new ArrayList<>(downloaded), errors);
    }

    /**
     * Closes this crawler, freeing any allocated resources.
     */
    @Override
    public void close() {
        //note -- better use close()
        shutdown(downloaders);
        shutdown(extractors);
    }

    private void shutdown(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println("Executor service did not terminate in time");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            System.err.println("Executor service was interrupted");
        }
    }

    private boolean shouldExclude(String url, List<String> excludes) {
        try {
            String host = URLUtils.getHost(url);
            return excludes.stream().anyMatch(host::contains);
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
