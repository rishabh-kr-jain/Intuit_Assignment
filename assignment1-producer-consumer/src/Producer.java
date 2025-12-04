import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import model.Item;

public class Producer implements Runnable {

    private final SharedQueue sharedQueue;
    private final String filePath;

    public Producer(SharedQueue sharedQueue, String filePath) {
        this.sharedQueue = sharedQueue;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Read file and produce each character
                try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                    int charCode;
                    while ((charCode = reader.read()) != -1) {
                        char c = (char) charCode;
                        System.out.println("Producer produced: '" + c + "'");
                        sharedQueue.put(new Item(c));
                    }
                } catch (IOException e) {
                    System.err.println("Error reading file: " + e.getMessage());
                }
                System.out.println("--- Producer finished reading file, restarting in 10 seconds... ---");

                // Sleep for a while before restarting                
                Thread.sleep(10000); // 10 seconds
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Producer interrupted, shutting down...");
        }
    }
}
