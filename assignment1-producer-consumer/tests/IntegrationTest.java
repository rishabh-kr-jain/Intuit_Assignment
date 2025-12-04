import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

public class IntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testProducerConsumerFlow() throws IOException, InterruptedException {
        // Create temp file with test content
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "Hello");

        SharedQueue queue = new SharedQueue(10);

        Producer producer = new Producer(queue, testFile.toString());
        Consumer consumer = new Consumer(queue);

        Thread p = new Thread(producer);
        Thread c = new Thread(consumer);

        p.start();
        c.start();

        // Let them run for a bit
        Thread.sleep(500);

        // Interrupt both threads to stop
        p.interrupt();
        c.interrupt();

        p.join();
        c.join();

        // Should have consumed "Hello" = 5 characters
        assertTrue(consumer.getConsumedCharacters().size() >= 5,
                "Should have consumed at least 5 characters");
        assertTrue(consumer.getConsumedString().startsWith("Hello"),
                "Consumed string should start with 'Hello'");
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testMultipleFileReads() throws IOException, InterruptedException {
        Path testFile = tempDir.resolve("small.txt");
        Files.writeString(testFile, "AB");

        SharedQueue queue = new SharedQueue(100);

        Producer producer = new Producer(queue, testFile.toString());
        Consumer consumer = new Consumer(queue);

        Thread p = new Thread(producer);
        Thread c = new Thread(consumer);

        p.start();
        c.start();

        // Wait long enough for multiple file reads (delay is 1 second)
        Thread.sleep(2500);

        p.interrupt();
        c.interrupt();

        p.join();
        c.join();

        // Should have read the file at least twice (AB twice = 4 chars)
        assertTrue(consumer.getConsumedCharacters().size() >= 4,
                "Should have consumed at least 4 characters from multiple reads");
    }
}
