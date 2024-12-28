import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.Description;

public class TestRunner {
    public static void main(String[] args) {
        // Create JUnit core runner
        JUnitCore runner = new JUnitCore();

        // Add a listener to show test progress
        runner.addListener(new RunListener() {
            @Override
            public void testStarted(Description description) {
                System.out.println("\n🚀 Starting test: " + description.getMethodName());
            }

            @Override
            public void testFinished(Description description) {
                System.out.println("✅ Finished test: " + description.getMethodName());
            }

            @Override
            public void testFailure(Failure failure) {
                System.out.println("❌ Test failed: " + failure.getDescription().getMethodName());
                System.out.println("   Reason: " + failure.getMessage());
                System.out.println("   Location: " + failure.getTrace());
            }
        });

        // Run the tests
        Result result = runner.run(order_avail_tests.class);

        // Print summary
        System.out.println("\n==========================================");
        System.out.println("🎯 Test Results Summary");
        System.out.println("==========================================");
        System.out.println("Total tests run: " + result.getRunCount());
        System.out.println("Failed tests: " + result.getFailureCount());
        System.out.println("Ignored tests: " + result.getIgnoreCount());
        System.out.println("Time taken: " + result.getRunTime() + "ms");

        if (result.wasSuccessful()) {
            System.out.println("\n✨ All tests passed successfully!");
        } else {
            System.out.println("\n⚠️ Some tests failed. Details:");
            for (Failure failure : result.getFailures()) {
                System.out.println("\nTest: " + failure.getDescription().getMethodName());
                System.out.println("Error: " + failure.getMessage());
            }
        }
    }
}