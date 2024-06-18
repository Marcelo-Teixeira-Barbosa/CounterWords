import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Counters {

    // Method to count occurrences of targetWord in file using a single thread (serial)
    public void CPUSerialcounter(String fileName, String targetWord) {
        long startTime = System.currentTimeMillis();
        int totalCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    if (word.equals(targetWord)) {
                        totalCount++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        recordCSV(targetWord, fileName, executionTime, totalCount, "Serial", 1);
    }

    // Method to count occurrences of targetWord in file using multiple threads (parallel)
    public void CPUparalelcounter(String fileName, String targetWord, int coreSize) {
        long startTime = System.currentTimeMillis();
        int totalCount = 0;
        ExecutorService executor = Executors.newFixedThreadPool(coreSize);

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    Callable<Integer> task = () -> word.equals(targetWord) ? 1 : 0;
                    Future<Integer> future = executor.submit(task);
                    totalCount += future.get();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        recordCSV(targetWord, fileName, executionTime, totalCount, "Parallel", coreSize);
    }

    // Method to record the results into a CSV file
    private void recordCSV(String targetWord, String fileName, long executionTime, int totalCount, String mode, int core) {
        String csvFile = "results.csv";

        try (FileWriter writer = new FileWriter(csvFile, true)) {
            writer.append(String.format("%s,%s,%d,%d,%s,%s\n", targetWord, fileName, executionTime, totalCount, mode, core));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
