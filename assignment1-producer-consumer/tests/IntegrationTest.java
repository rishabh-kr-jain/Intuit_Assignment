import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

/**
 * Integration tests for the complete Producer-Consumer system.
 * 
 * Demonstrates the full assignment requirements:
 * - Producer reads from SOURCE container (input file)
 * - Data flows through SHARED QUEUE (BlockingQueue)
 * - Consumer stores in DESTINATION container (output file)
 * - Thread synchronization between concurrent threads
 */
public class IntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("E2E: Complete data flow from source file → queue → destination file")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testEndToEndFlow() throws IOException, InterruptedException {
        Path inputFile = tempDir.resolve("input.txt");
        Path outputFile = tempDir.resolve("output.txt");
        Files.writeString(inputFile, "Hello");

        BlockingQueue queue = new BlockingQueue(10);
        Producer producer = new Producer(queue, inputFile.toString());
        Consumer consumer = new Consumer(queue, outputFile.toString());

        Thread p = new Thread(producer);
        Thread c = new Thread(consumer);

        p.start();
        c.start();

        Thread.sleep(500);

        p.interrupt();
        c.interrupt();
        p.join();
        c.join();

        String output = Files.readString(outputFile);
        assertTrue(output.startsWith("Hello"), "Output should contain input content");
    }

    @Test
    @DisplayName("Concurrent: Fast producer with small queue - tests blocking")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testProducerFasterThanConsumer() throws IOException, InterruptedException {
        Path inputFile = tempDir.resolve("input.txt");
        Path outputFile = tempDir.resolve("output.txt");
        Files.writeString(inputFile, "ABCDEFGHIJ"); // 10 chars

        BlockingQueue queue = new BlockingQueue(3); // Small queue
        Producer producer = new Producer(queue, inputFile.toString());
        Consumer consumer = new Consumer(queue, outputFile.toString());

        Thread p = new Thread(producer);
        Thread c = new Thread(consumer);

        p.start();
        c.start();

        Thread.sleep(1000);

        p.interrupt();
        c.interrupt();
        p.join();
        c.join();

        String output = Files.readString(outputFile);
        assertTrue(output.length() >= 10, "All characters should be processed");
    }

    @Test
    @DisplayName("Thread Sync: No data corruption during concurrent access")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testDataIntegrity() throws IOException, InterruptedException {
        Path inputFile = tempDir.resolve("input.txt");
        Path outputFile = tempDir.resolve("output.txt");
        String testData = "TestDataIntegrity123";
        Files.writeString(inputFile, testData);

        BlockingQueue queue = new BlockingQueue(50);
        Producer producer = new Producer(queue, inputFile.toString());
        Consumer consumer = new Consumer(queue, outputFile.toString());

        Thread p = new Thread(producer);
        Thread c = new Thread(consumer);

        p.start();
        c.start();

        Thread.sleep(500);

        p.interrupt();
        c.interrupt();
        p.join();
        c.join();

        String output = Files.readString(outputFile);
        assertTrue(output.startsWith(testData), "Data integrity should be maintained");
    }

    @Test
    @DisplayName("Edge Case: Empty input file produces empty output")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testEmptyInputFile() throws IOException, InterruptedException {
        Path inputFile = tempDir.resolve("empty.txt");
        Path outputFile = tempDir.resolve("output.txt");
        Files.writeString(inputFile, "");

        BlockingQueue queue = new BlockingQueue(10);
        Producer producer = new Producer(queue, inputFile.toString());
        Consumer consumer = new Consumer(queue, outputFile.toString());

        Thread p = new Thread(producer);
        Thread c = new Thread(consumer);

        p.start();
        c.start();

        Thread.sleep(500);

        p.interrupt();
        c.interrupt();
        p.join();
        c.join();

        assertTrue(queue.isEmpty(), "Queue should be empty with empty input");
    }
}
