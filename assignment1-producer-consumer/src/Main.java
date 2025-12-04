import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        // Default file paths - can be overridden via command line arguments
        String inputFilePath = "input.txt";
        String outputFilePath = "output.txt";

        if (args.length > 0) {
            inputFilePath = args[0];
        }
        if (args.length > 1) {
            outputFilePath = args[1];
        }

        System.out.println("Reading from file: " + inputFilePath);
        System.out.println("Writing to file: " + outputFilePath);
        System.out.println("Press Ctrl+C to stop...\n");

        SharedQueue sharedQueue = new SharedQueue(10);

        Producer producer = new Producer(sharedQueue, inputFilePath);
        Consumer consumer = new Consumer(sharedQueue, outputFilePath);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        // Add shutdown hook to gracefully stop on Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down...");
            executor.shutdownNow();
        }));

        executor.submit(producer);
        executor.submit(consumer);

        // Don't shutdown - let it run forever until interrupted
        // executor.shutdown();
    }
}
