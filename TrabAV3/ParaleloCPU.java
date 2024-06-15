import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class ParaleloCPU {

    public static void main(String[] args) throws IOException {
        String filePath = "C:\\Users\\João Vitor\\Desktop\\TrabAV3\\Dracula.txt";
        String wordToCount = "fear";

        long startTime = System.currentTimeMillis();
        int count = parallelCPU(filePath, wordToCount);
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        System.out.println("Contagem da palavra: " + count);
        System.out.println("Tempo de execução: " + executionTime + "ms");

        // Gerar arquivo CSV com os resultados
        String csvFilePath = "results_parallel_cpu.csv";
        String csvContent = "Method,Word,Count,ExecutionTime(ms)\n";
        csvContent += "ParaleloCPU," + wordToCount + "," + count + "," + executionTime + "\n";

        Files.write(Paths.get(csvFilePath), csvContent.getBytes());
        System.out.println("Resultados salvos em: " + csvFilePath);
    }

    public static int parallelCPU(String filePath, String wordToCount) throws IOException {
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            count = reader.lines()
                    .parallel()  // Executa em paralelo
                    .flatMap(line -> Arrays.stream(line.split("\\W+")))
                    .filter(word -> word.equalsIgnoreCase(wordToCount))
                    .mapToInt(word -> 1)  // Convertendo para int
                    .sum();  // Somando as contagens parciais
        }

        return count;
    }
}
