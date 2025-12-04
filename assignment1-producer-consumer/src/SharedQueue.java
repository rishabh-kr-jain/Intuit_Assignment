import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import model.Item;

public class SharedQueue {
    private final BlockingQueue<Item> queue;
// Using Blocking Queue to handle synchronization
    public SharedQueue(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    public void put(Item item) throws InterruptedException {
        queue.put(item);
    }

    public Item take() throws InterruptedException {
        return queue.take();
    }

    public int size() {
        return queue.size();
    }
}
