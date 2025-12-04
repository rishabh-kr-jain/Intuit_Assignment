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
 * Tests for Consumer thread - reads from shared queue and stores items in destination.
 * 
 * Demonstrates:
 * - Reading data from shared blocking queue
 * - Storing items in destination container (file)
 * - Thread coordination with Producer
 */
public class ConsumerTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Consumer: Writes consumed characters to output file")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testConsumerWritesToFile() throws IOException, InterruptedException {
        Path outputFile = tempDir.resolve("output.txt");
        BlockingQueue queue = new BlockingQueue(10);
        
        queue.put(new Item('H'));
        queue.put(new Item('I'));

        Consumer consumer = new Consumer(queue, outputFile.toString());
        Thread t = new Thread(consumer);
        t.start();

        Thread.sleep(300);
        t.interrupt();
        t.join();

        String content = Files.readString(outputFile);
        assertEquals("HI", content, "Output file should contain consumed characters");
    }

    @Test
    @DisplayName("Blocking: Consumer waits when queue is empty")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testConsumerBlocksOnEmptyQueue() throws IOException, InterruptedException {
        Path outputFile = tempDir.resolve("output.txt");
        BlockingQueue queue = new BlockingQueue(10);

        Consumer consumer = new Consumer(queue, outputFile.toString());
        Thread t = new Thread(consumer);
        t.start();

        Thread.sleep(200);
        assertTrue(t.isAlive(), "Consumer should be blocked waiting for items");

        queue.put(new Item('X'));
        Thread.sleep(100);

        t.interrupt();
        t.join();

        String content = Files.readString(outputFile);
        assertEquals("X", content);
    }

    @Test
    @DisplayName("Thread: Consumer handles interruption gracefully")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testConsumerHandlesInterrupt() throws IOException, InterruptedException {
        Path outputFile = tempDir.resolve("output.txt");
        BlockingQueue queue = new BlockingQueue(10);

        Consumer consumer = new Consumer(queue, outputFile.toString());
        Thread t = new Thread(consumer);
        t.start();

        Thread.sleep(200);
        t.interrupt();
        t.join(1000);

        assertFalse(t.isAlive(), "Consumer should have stopped after interrupt");
    }

    @Test
    @DisplayName("Consumer: Maintains FIFO order when writing")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testConsumerMaintainsFIFOOrder() throws IOException, InterruptedException {
        Path outputFile = tempDir.resolve("output.txt");
        BlockingQueue queue = new BlockingQueue(10);

        queue.put(new Item('A'));
        queue.put(new Item('B'));
        queue.put(new Item('C'));

        Consumer consumer = new Consumer(queue, outputFile.toString());
        Thread t = new Thread(consumer);
        t.start();

        Thread.sleep(300);
        t.interrupt();
        t.join();

        String content = Files.readString(outputFile);
        assertEquals("ABC", content, "Consumer should maintain FIFO order");
    }
}
