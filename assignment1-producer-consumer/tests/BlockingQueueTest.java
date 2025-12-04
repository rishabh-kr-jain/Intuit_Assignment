import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import model.Item;

/**
 * Comprehensive test suite for BlockingQueue implementation.
 * 
 * Tests the following assignment objectives:
 * 1. BLOCKING QUEUES - Custom implementation with put/take operations
 * 2. WAIT/NOTIFY MECHANISM - Threads wait when queue is empty/full
 * 3. THREAD SYNCHRONIZATION - Multiple threads safely access shared queue
 * 4. CONCURRENT PROGRAMMING - Producer/Consumer coordination
 */
public class BlockingQueueTest {

    // ============================================================
    // BLOCKING QUEUE - Basic Operations
    // ============================================================

    @Test
    @DisplayName("BlockingQueue: Basic put and take operations")
    void testBasicPutAndTake() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(5);
        
        queue.put(new Item('A'));
        queue.put(new Item('B'));
        
        assertEquals(2, queue.size());
        assertEquals('A', queue.take().getCharacter());
        assertEquals('B', queue.take().getCharacter());
        assertEquals(0, queue.size());
    }

    @Test
    @DisplayName("BlockingQueue: Queue respects capacity limit")
    void testQueueCapacity() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(3);
        
        queue.put(new Item('X'));
        queue.put(new Item('Y'));
        queue.put(new Item('Z'));
        
        assertEquals(3, queue.size());
        assertTrue(queue.isFull());
        assertFalse(queue.isEmpty());
    }

    @Test
    @DisplayName("BlockingQueue: Empty queue state")
    void testEmptyQueue() {
        BlockingQueue queue = new BlockingQueue(5);
        
        assertTrue(queue.isEmpty());
        assertFalse(queue.isFull());
        assertEquals(0, queue.size());
    }

    // ============================================================
    // WAIT/NOTIFY MECHANISM - Consumer blocks on empty queue
    // ============================================================

    @Test
    @DisplayName("Wait/Notify: Consumer blocks (waits) when queue is empty")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testConsumerBlocksOnEmptyQueue() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(5);
        AtomicBoolean consumerBlocked = new AtomicBoolean(true);
        AtomicBoolean itemReceived = new AtomicBoolean(false);

        // Consumer thread - should block on empty queue
        Thread consumer = new Thread(() -> {
            try {
                Item item = queue.take(); // Should block here
                consumerBlocked.set(false);
                itemReceived.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        consumer.start();
        Thread.sleep(200); // Give consumer time to block

        // Consumer should still be blocked
        assertTrue(consumerBlocked.get(), "Consumer should be blocked on empty queue");
        assertFalse(itemReceived.get(), "Consumer should not have received item yet");

        // Add item to unblock consumer
        queue.put(new Item('X'));
        consumer.join(1000);

        assertTrue(itemReceived.get(), "Consumer should have received item after put");
    }

    // ============================================================
    // WAIT/NOTIFY MECHANISM - Producer blocks on full queue
    // ============================================================

    @Test
    @DisplayName("Wait/Notify: Producer blocks (waits) when queue is full")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testProducerBlocksOnFullQueue() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(2);
        AtomicBoolean producerBlocked = new AtomicBoolean(true);

        // Fill the queue
        queue.put(new Item('A'));
        queue.put(new Item('B'));
        assertTrue(queue.isFull());

        // Producer thread - should block on full queue
        Thread producer = new Thread(() -> {
            try {
                queue.put(new Item('C')); // Should block here
                producerBlocked.set(false);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        Thread.sleep(200); // Give producer time to block

        assertTrue(producerBlocked.get(), "Producer should be blocked on full queue");
        assertEquals(2, queue.size(), "Queue should still be at capacity");

        // Take one item to unblock producer
        queue.take();
        producer.join(1000);

        assertFalse(producerBlocked.get(), "Producer should have been unblocked");
        assertEquals(2, queue.size(), "Queue should be full again");
    }

    // ============================================================
    // THREAD SYNCHRONIZATION - Safe concurrent access
    // ============================================================

    @Test
    @DisplayName("Thread Sync: Concurrent put/take without data corruption")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testConcurrentPutAndTake() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(5);
        int itemCount = 100;
        AtomicInteger produced = new AtomicInteger(0);
        AtomicInteger consumed = new AtomicInteger(0);

        // Fast producer
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < itemCount; i++) {
                    queue.put(new Item((char) ('A' + (i % 26))));
                    produced.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Fast consumer
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < itemCount; i++) {
                    queue.take();
                    consumed.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        assertEquals(itemCount, produced.get(), "All items should be produced");
        assertEquals(itemCount, consumed.get(), "All items should be consumed");
        assertEquals(0, queue.size(), "Queue should be empty at the end");
    }

    // ============================================================
    // CONCURRENT PROGRAMMING - Speed mismatch handling
    // ============================================================

    @Test
    @DisplayName("Concurrent: Fast producer / slow consumer scenario")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testFastProducerSlowConsumer() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(3);
        int itemCount = 20;
        AtomicInteger produced = new AtomicInteger(0);
        AtomicInteger consumed = new AtomicInteger(0);
        AtomicInteger timesQueueWasFull = new AtomicInteger(0);

        // Fast producer (no delay)
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < itemCount; i++) {
                    if (queue.isFull()) {
                        timesQueueWasFull.incrementAndGet();
                    }
                    queue.put(new Item((char) ('A' + i)));
                    produced.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Slow consumer (with delay)
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < itemCount; i++) {
                    queue.take();
                    consumed.incrementAndGet();
                    Thread.sleep(50); // Slow consumer
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        assertEquals(itemCount, produced.get());
        assertEquals(itemCount, consumed.get());
        assertTrue(timesQueueWasFull.get() > 0, "Queue should have been full at some point");
    }

    @Test
    @DisplayName("Concurrent: Slow producer / fast consumer scenario")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testSlowProducerFastConsumer() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(5);
        int itemCount = 10;
        AtomicInteger produced = new AtomicInteger(0);
        AtomicInteger consumed = new AtomicInteger(0);
        AtomicInteger timesQueueWasEmpty = new AtomicInteger(0);

        // Slow producer (with delay)
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < itemCount; i++) {
                    Thread.sleep(50); // Slow producer
                    queue.put(new Item((char) ('A' + i)));
                    produced.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Fast consumer (no delay)
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < itemCount; i++) {
                    if (queue.isEmpty()) {
                        timesQueueWasEmpty.incrementAndGet();
                    }
                    queue.take();
                    consumed.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        assertEquals(itemCount, produced.get());
        assertEquals(itemCount, consumed.get());
        assertTrue(timesQueueWasEmpty.get() > 0, "Queue should have been empty at some point");
    }

    // ============================================================
    // EDGE CASES
    // ============================================================

    @Test
    @DisplayName("Edge Case: Queue with capacity of 1")
    void testQueueCapacityOne() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(1);
        
        assertTrue(queue.isEmpty());
        assertFalse(queue.isFull());
        
        queue.put(new Item('X'));
        
        assertFalse(queue.isEmpty());
        assertTrue(queue.isFull());
        assertEquals(1, queue.size());
        
        Item item = queue.take();
        assertEquals('X', item.getCharacter());
        assertTrue(queue.isEmpty());
    }


    // ============================================================
    // BLOCKING QUEUE - FIFO Order Guarantee
    // ============================================================

    @Test
    @DisplayName("BlockingQueue: Maintains FIFO order")
    void testFIFOOrder() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(10);
        String input = "HELLO";

        for (char c : input.toCharArray()) {
            queue.put(new Item(c));
        }

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            output.append(queue.take().getCharacter());
        }

        assertEquals(input, output.toString(), "Queue should maintain FIFO order");
    }
}
