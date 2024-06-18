import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Counters counters = new Counters();
        String[] files = {"DonQuixote.txt", "Dracula.txt", "MobyDick.txt"};
        String targetWord = "man";
        int[] coreSizes = {5, 10};

        // Create sample text files
        createSampleFile("DonQuixote.txt");
        createSampleFile("Dracula.txt");
        createSampleFile("MobyDick.txt");

        // Run serial counter 3 times for each file
        for (String file : files) {
            for (int i = 0; i < 3; i++) {
                System.out.println("Running serial counter for file: " + file + " (Run " + (i + 1) + ")");
                counters.CPUSerialcounter(file, targetWord);
            }
        }

        // Run parallel counter 3 times for each file with 5 cores and 10 cores
        for (String file : files) {
            for (int cores : coreSizes) {
                for (int i = 0; i < 3; i++) {
                    System.out.println("Running parallel counter for file: " + file + " with " + cores + " cores (Run " + (i + 1) + ")");
                    counters.CPUparalelcounter(file, targetWord, cores);
                }
            }
        }

        System.out.println("Counters have finished executing. Check results.csv for the output.");

        callPythonScript("graph.py");

        System.out.println("Python script has finished executing.");
    }

    // Method to create sample text files for testing
    private static void createSampleFile(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            if (fileName.equals("DonQuixote.txt")) {
                writer.write("This is a sample text from Don Quixote. Don Quixote is a man.\n");
                writer.write("The adventures of Don Quixote, a man of La Mancha.\n");
            } else if (fileName.equals("Dracula.txt")) {
                writer.write("This is a sample text from Dracula. Dracula is a man.\n");
                writer.write("The story of Count Dracula, a man who became a vampire.\n");
            } else if (fileName.equals("MobyDick.txt")) {
                writer.write("This is a sample text from Moby Dick. Moby Dick was hunted by a man.\n");
                writer.write("The tale of Captain Ahab, a man obsessed with a whale.\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void callPythonScript(String scriptName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", scriptName);
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
