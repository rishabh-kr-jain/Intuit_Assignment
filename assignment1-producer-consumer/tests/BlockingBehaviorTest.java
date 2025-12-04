import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import model.Item;

public class BlockingBehaviorTest {

    @TempDir
    Path tempDir;

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testConsumerBlocksUntilProducerProvidesData() throws Exception {
        SharedQueue queue = new SharedQueue(5);
        Consumer consumer = new Consumer(queue);
        AtomicBoolean consumerStarted = new AtomicBoolean(false);

        Thread consumerThread = new Thread(() -> {
            consumerStarted.set(true);
            consumer.run();
        });
        consumerThread.start();

        // Wait for consumer to start and block
        Thread.sleep(200);
        assertTrue(consumerStarted.get(), "Consumer should have started");
        assertEquals(0, consumer.getConsumedCharacters().size(), "Consumer should be blocked with no items");

        // Add an item to unblock consumer
        queue.put(new Item('Z'));

        Thread.sleep(200);
        consumerThread.interrupt();
        consumerThread.join();

        assertEquals(1, consumer.getConsumedCharacters().size());
        assertEquals('Z', consumer.getConsumedCharacters().get(0));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testProducerBlocksWhenQueueFull() throws IOException, InterruptedException {
        // Create a file with more content than queue capacity
        Path testFile = tempDir.resolve("large.txt");
        Files.writeString(testFile, "ABCDEFGHIJ"); // 10 chars

        SharedQueue queue = new SharedQueue(2); // Small queue
        Producer producer = new Producer(queue, testFile.toString());

        Thread producerThread = new Thread(producer);
        producerThread.start();

        // Producer should be blocked because queue is full
        Thread.sleep(300);
        assertEquals(2, queue.size(), "Queue should be at capacity");

        // Consume one to allow producer to continue
        Item item = queue.take();
        assertEquals('A', item.getCharacter());

        Thread.sleep(100);
        producerThread.interrupt();
        producerThread.join();
    }
}
