import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import model.Item;

public class Consumer implements Runnable {

    private final SharedQueue sharedQueue;
    private final String outputFilePath;

    public Consumer(SharedQueue sharedQueue, String outputFilePath) {
        this.sharedQueue = sharedQueue;
        this.outputFilePath = outputFilePath;
    }

    @Override
    public void run() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            System.out.println("Writing output to: " + outputFilePath);

            while (!Thread.currentThread().isInterrupted()) {
                Item item = sharedQueue.take();
                char c = item.getCharacter();
                System.out.println("Consumer consumed: '" + c + "'");
                writer.write(c);
                writer.flush();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Consumer interrupted, shutting down...");
        } catch (IOException e) {
            System.err.println("Error with output file: " + e.getMessage());
        }
    }
}
