import java.util.LinkedList;
import java.util.Queue;

import model.Item;

public class BlockingQueue {
    private final Queue<Item> queue;
    private final int capacity;

    public BlockingQueue(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
    }

    public synchronized void put(Item item) throws InterruptedException {
        // Wait while queue is full
        while (queue.size() >= capacity) {
            System.out.println("Queue is full, producer waiting...");
            wait();
        }
        
        queue.add(item);
        
        // Notify consumer that item is available
        notifyAll();
    }

    public synchronized Item take() throws InterruptedException {
        // Wait while queue is empty
        while (queue.isEmpty()) {
            System.out.println("Queue is empty, consumer waiting...");
            wait();
        }
        
        Item item = queue.poll();
        
        // Notify producer that space is available
        notifyAll();
        
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized boolean isFull() {
        return queue.size() >= capacity;
    }
}
