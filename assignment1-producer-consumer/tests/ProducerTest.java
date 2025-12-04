import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import model.Item;

/**
 * Tests for Producer thread - reads from source file and places items into shared queue.
 * 
 * Demonstrates:
 * - Reading data from source container (file)
 * - Placing items into shared blocking queue
 * - Thread coordination with Consumer
 */
public class ProducerTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Producer: Reads characters from file into queue")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testProducerReadsFileCharacters() throws IOException, InterruptedException {
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "ABC");

        BlockingQueue queue = new BlockingQueue(10);
        Producer producer = new Producer(queue, testFile.toString());

        Thread t = new Thread(producer);
        t.start();
        Thread.sleep(500);
        t.interrupt();
        t.join();

        assertTrue(queue.size() >= 3, "Queue should have at least 3 items");
    }

    @Test
    @DisplayName("Producer: Maintains FIFO order of characters")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testProducerMaintainsCharacterOrder() throws IOException, InterruptedException {
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "XYZ");

        BlockingQueue queue = new BlockingQueue(10);
        Producer producer = new Producer(queue, testFile.toString());

        Thread t = new Thread(producer);
        t.start();
        Thread.sleep(500);
        t.interrupt();
        t.join();

        assertEquals('X', queue.take().getCharacter());
        assertEquals('Y', queue.take().getCharacter());
        assertEquals('Z', queue.take().getCharacter());
    }

    @Test
    @DisplayName("Blocking: Producer waits when queue is full")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testProducerBlocksWhenQueueFull() throws IOException, InterruptedException {
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "ABCDEFGHIJ");

        BlockingQueue queue = new BlockingQueue(2);
        Producer producer = new Producer(queue, testFile.toString());

        Thread t = new Thread(producer);
        t.start();
        Thread.sleep(300);

        assertEquals(2, queue.size(), "Queue should be at capacity");

        queue.take();
        Thread.sleep(100);

        t.interrupt();
        t.join();
    }

    @Test
    @DisplayName("Thread: Producer handles interruption gracefully")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testProducerHandlesInterrupt() throws IOException, InterruptedException {
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "TEST");

        BlockingQueue queue = new BlockingQueue(1);
        queue.put(new Item('X'));

        Producer producer = new Producer(queue, testFile.toString());
        Thread t = new Thread(producer);
        t.start();

        Thread.sleep(200);
        t.interrupt();
        t.join(1000);

        assertFalse(t.isAlive(), "Producer should have stopped after interrupt");
    }
}
