import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        // Load configuration
        AppConfig config = new AppConfig();
        config.printConfig();

        System.out.println("Press Ctrl+C to stop...\n");

        BlockingQueue queue = new BlockingQueue(config.getQueueCapacity());

        Producer producer = new Producer(queue, config.getInputFile());
        Consumer consumer = new Consumer(queue, config.getOutputFile());

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Add shutdown hook to gracefully stop on Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down...");
            executor.shutdownNow();
        }));

        executor.submit(producer);
        executor.submit(consumer);
    }
}
