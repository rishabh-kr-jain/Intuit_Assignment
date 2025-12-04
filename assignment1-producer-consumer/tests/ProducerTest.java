import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import model.Item;

public class ProducerTest {

    @TempDir
    Path tempDir;

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testProducerReadsFileAndAddsToQueue() throws IOException, InterruptedException {
        // Create a temp file with test content
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "AB");

        SharedQueue queue = new SharedQueue(10);
        Producer producer = new Producer(queue, testFile.toString());
        Thread t = new Thread(producer);
        t.start();

        // Wait for producer to read the file once
        Thread.sleep(500);
        t.interrupt();
        t.join();

        // Should have at least 2 characters
        assertTrue(queue.size() >= 2, "Queue should have at least 2 items");
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testProducerProducesCorrectCharacters() throws IOException, InterruptedException {
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "XY");

        SharedQueue queue = new SharedQueue(10);
        Producer producer = new Producer(queue, testFile.toString());
        Thread t = new Thread(producer);
        t.start();

        // Wait for characters to be produced
        Thread.sleep(300);
        t.interrupt();
        t.join();

        Item first = queue.take();
        Item second = queue.take();

        assertEquals('X', first.getCharacter());
        assertEquals('Y', second.getCharacter());
    }
}
