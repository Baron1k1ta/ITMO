package info.kgeorgiy.ja.baronov.bank.tests;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;


/**
 * The {@code Tester} class is responsible for executing unit tests using the JUnit Platform.
 */
public class Tester {

    /**
     * Main method that serves as the entry point to run the tests.
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SummaryGeneratingListener listener = new SummaryGeneratingListener();

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectClass(Tests.class))
                .build();

        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();
        summary.printTo(new java.io.PrintWriter(System.out));

        summary.getFailures().forEach(failure -> {
            System.err.println("Test failed: " + failure.getTestIdentifier().getDisplayName());
            System.err.println("Reason: " + failure.getException().getMessage());
        });

        if (summary.getTotalFailureCount() == 0) {
            System.out.println("Successfully completed");
            System.exit(0);
        } else {
            System.out.println("Failed");
            System.exit(1);
        }
    }
}

