import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
    private static final String CONFIG_FILE = "config.properties";
    
    private final String inputFile;
    private final String outputFile;
    private final int queueCapacity;

    public AppConfig() {
        Properties props = new Properties();
        
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            System.out.println("Loaded configuration from: " + CONFIG_FILE);
        } catch (IOException e) {
            System.out.println("Config file not found, using defaults.");
        }

        // Load properties with defaults
        this.inputFile = props.getProperty("input.file", "input.txt");
        this.outputFile = props.getProperty("output.file", "output.txt");
        this.queueCapacity = Integer.parseInt(props.getProperty("queue.capacity", "10"));
    }

    public String getInputFile() {
        return inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void printConfig() {
        System.out.println("=== Configuration ===");
        System.out.println("Input file: " + inputFile);
        System.out.println("Output file: " + outputFile);
        System.out.println("Queue capacity: " + queueCapacity);
        System.out.println("====================\n");
    }
}
