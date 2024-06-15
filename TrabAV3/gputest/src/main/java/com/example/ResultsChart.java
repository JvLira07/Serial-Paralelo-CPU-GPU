package com.example;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.general.*;

public class ResultsChart extends JFrame {

    @SuppressWarnings("unchecked")
    public ResultsChart() {
        super("Comparativo de Resultados");

        
        String[] methods = {"SerialCPU", "ParaleloCPU", "ParaleloGPU"}; 
        String[] files = {"results_serial_cpu.csv", "results_parallel_cpu.csv", "results_parallel_gpu.csv"}; 

        // Arrays para armazenar os dados
        String[] words = new String[methods.length];
        int[] counts = new int[methods.length];
        long[] executionTimes = new long[methods.length];

        // Paleta de cores pré-definida
        Color[] colors = {Color.BLUE, Color.GREEN, Color.RED}; // Adicione mais cores se houver mais métodos

        // Leitura dos dados de cada arquivo
        for (int i = 0; i < methods.length; i++) {
            try (BufferedReader reader = new BufferedReader(new FileReader(files[i]))) {
                String line = reader.readLine(); // Lê o cabeçalho
                line = reader.readLine(); // Lê a linha de dados
                String[] values = line.split(",");
                words[i] = values[1]; // Palavra
                counts[i] = Integer.parseInt(values[2]); // Contagem
                executionTimes[i] = Long.parseLong(values[3]); // Tempo de execução
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Criando o dataset para o gráfico de pizza
        @SuppressWarnings("rawtypes")
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (int i = 0; i < methods.length; i++) {
            dataset.setValue(methods[i], counts[i]);
        }

        // Criando o gráfico de pizza
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Comparativo de Contagem de Palavras",
                dataset,
                true, 
                true, 
                false); 

        
        @SuppressWarnings("rawtypes")
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setSectionPaint(methods[0], colors[0]);
        plot.setSectionPaint(methods[1], colors[1]);
        plot.setSectionPaint(methods[2], colors[2]);


        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
    }

    public static void main(String[] args) {
        ResultsChart chart = new ResultsChart();
        chart.pack();
        chart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chart.setVisible(true);
    }
}
