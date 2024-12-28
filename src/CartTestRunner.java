import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class CartTestRunner {
    private static final String TEST_REPORT_FILE = "cart_test_report.txt";

    public static void main(String[] args){
        LocalDateTime startTime = LocalDateTime.now();
        Result result = JUnitCore.runClasses(CartTest.class);


        LocalDateTime endTime = LocalDateTime.now();

        generateTestReport(result, startTime, endTime);

        printConsoleSummary(result);

        System.exit(result.wasSuccessful() ? 0 : 1);
    }

    private static void generateTestReport(Result result, LocalDateTime startTime, LocalDateTime endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (PrintWriter writer = new PrintWriter(new FileWriter(TEST_REPORT_FILE))) {
            writer.println("Cart Operations Test Report");
            writer.println("=========================");
            writer.println();

            writer.println("Test Execution Details:");
            writer.println("-----------------------");
            writer.println("Start Time: " + startTime.format(formatter));
            writer.println("End Time: " + endTime.format(formatter));
            writer.println("Duration: " + result.getRunTime() + "ms");
            writer.println();

            writer.println("Test Summary:");
            writer.println("-------------");
            writer.println("Total Tests: " + result.getRunCount());
            writer.println("Successful: " + (result.getRunCount() - result.getFailureCount()));
            writer.println("Failed: " + result.getFailureCount());
            writer.println("Ignored: " + result.getIgnoreCount());
            writer.println();

            double successRate = (double)(result.getRunCount() - result.getFailureCount())
                    / result.getRunCount() * 100;
            writer.println(String.format("Success Rate: %.2f%%", successRate));
            writer.println();

            if (result.getFailureCount() > 0) {
                writer.println("Failed Tests Details:");
                writer.println("--------------------");
                int failureCount = 1;
                for (Failure failure : result.getFailures()) {
                    writer.println("Failure #" + failureCount++);
                    writer.println("Test: " + failure.getTestHeader());
                    writer.println("Message: " + failure.getMessage());
                    writer.println("Stack Trace:");
                    writer.println(failure.getTrace());
                    writer.println();
                }
            }

            writer.println("End of Test Report");
            writer.println("=================");

            System.out.println("Detailed test report has been saved to: " + TEST_REPORT_FILE);

        } catch (IOException e) {
            System.err.println("Error writing test report: " + e.getMessage());
        }
    }

    private static void printConsoleSummary(Result result) {
        System.out.println("\nCart Operations Test Summary");
        System.out.println("===========================");
        System.out.println("Total Tests: " + result.getRunCount());
        System.out.println("Failed Tests: " + result.getFailureCount());
        System.out.println("Execution Time: " + result.getRunTime() + "ms");

        if (result.wasSuccessful()) {
            System.out.println("\n✅ ALL TESTS PASSED");
        } else {
            System.out.println("\n❌ SOME TESTS FAILED");
            System.out.println("\nFailed Tests:");
            for (Failure failure : result.getFailures()) {
                System.out.println("- " + failure.getTestHeader());
                System.out.println("  " + failure.getMessage());
            }
        }
    }
}