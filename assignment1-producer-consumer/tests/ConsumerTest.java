import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import model.Item;

public class ConsumerTest {

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testConsumerRemovesCharacters() throws InterruptedException {
        SharedQueue queue = new SharedQueue(10);
        queue.put(new Item('A'));
        queue.put(new Item('B'));
        queue.put(new Item('C'));

        Consumer consumer = new Consumer(queue);
        Thread t = new Thread(consumer);
        t.start();

        // Wait for consumer to process items
        Thread.sleep(300);
        t.interrupt();
        t.join();

        assertEquals(3, consumer.getConsumedCharacters().size());
        assertEquals("ABC", consumer.getConsumedString());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testConsumerIgnoresPoisonPillInInfiniteMode() throws InterruptedException {
        SharedQueue queue = new SharedQueue(10);
        queue.put(new Item('X'));
        queue.put(new Item(Item.POISON_PILL)); // poison pill
        queue.put(new Item('Y'));

        Consumer consumer = new Consumer(queue);
        Thread t = new Thread(consumer);
        t.start();

        Thread.sleep(300);
        t.interrupt();
        t.join();

        // Consumer should have consumed X and Y, skipping poison pill
        assertEquals(2, consumer.getConsumedCharacters().size());
        assertEquals("XY", consumer.getConsumedString());
    }
}
