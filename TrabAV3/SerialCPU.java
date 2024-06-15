import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class SerialCPU {

    public static void main(String[] args) throws IOException {
        String filePath = "C:\\Users\\João Vitor\\Desktop\\TrabAV3\\Dracula.txt";
        String wordToCount = "fear";

        long startTime = System.currentTimeMillis();
        int count = serialCPU(filePath, wordToCount);
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        System.out.println("Contagem da palavra: " + count);
        System.out.println("Tempo de execução: " + executionTime + "ms");

        // Gerar arquivo CSV com os resultados
        String csvFilePath = "results_serial_cpu.csv";
        String csvContent = "Method,Word,Count,ExecutionTime(ms)\n";
        csvContent += "SerialCPU," + wordToCount + "," + count + "," + executionTime + "\n";
        
        Files.write(Paths.get(csvFilePath), csvContent.getBytes());
        System.out.println("Resultados salvos em: " + csvFilePath);
    }

    public static int serialCPU(String filePath, String wordToCount) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        String[] words = content.split("\\W+");
        return (int) Arrays.stream(words).filter(word -> word.equalsIgnoreCase(wordToCount)).count();
    }
}
