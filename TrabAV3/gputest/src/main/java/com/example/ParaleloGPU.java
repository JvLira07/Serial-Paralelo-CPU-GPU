package com.example;

import org.jocl.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.jocl.CL.*;

public class ParaleloGPU {

    private static final String PROGRAM_SOURCE =
    "__kernel void countWords(__global const char* text, int textLen, __global const char* targetWord, int wordLen, __global int* result) {\n" +
    "    int gid = get_global_id(0);\n" +
    "    int start = gid * wordLen;\n" +
    "    int count = 0;\n" +
    "    while (start <= textLen - wordLen) {\n" +
    "        int match = 1;\n" +
    "        for (int i = 0; i < wordLen; i++) {\n" +
    "            if (text[start + i] != targetWord[i]) {\n" +
    "                match = 0;\n" +
    "                break;\n" +
    "            }\n" +
    "        }\n" +
    "        if (match) {\n" +
    "            count++;\n" +
    "            start += wordLen;\n" +
    "        } else {\n" +
    "            start++;\n" +
    "        }\n" +
    "    }\n" +
    "    result[gid] = count;\n" +
    "}\n";

    public static void main(String[] args) throws IOException {
        String filePath = "C:\\Users\\João Vitor\\Desktop\\Dracula.txt";
        String wordToCount = "fear";

        long startTime = System.currentTimeMillis();
        int count = parallelGPU(filePath, wordToCount);
        long endTime = System.currentTimeMillis();

        System.out.println("Contagem: " + count);
        System.out.println("Tempo de execução: " + (endTime - startTime) + "ms");

        
        String csvFilePath = "results_parallel_gpu.csv";
        String csvContent = "Method,Word,Count,ExecutionTime(ms)\n";
        csvContent += "ParaleloGPU," + wordToCount + "," + count + "," + (endTime - startTime) + "\n";

        Files.write(Paths.get(csvFilePath), csvContent.getBytes());
        System.out.println("Resultados salvos em: " + csvFilePath);
    }

    public static int parallelGPU(String filePath, String wordToCount) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        int textLen = content.length();
        int wordLen = wordToCount.length();

        
        CL.setExceptionsEnabled(true);

        
        cl_platform_id[] platforms = new cl_platform_id[1];
        clGetPlatformIDs(platforms.length, platforms, null);

        cl_device_id[] devices = new cl_device_id[1];
        clGetDeviceIDs(platforms[0], CL_DEVICE_TYPE_GPU, devices.length, devices, null);

        
        cl_context context = clCreateContext(null, 1, devices, null, null, null);
        @SuppressWarnings("deprecation")
        cl_command_queue commandQueue = clCreateCommandQueue(context, devices[0], 0, null);

        
        cl_mem textMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_char * textLen, Pointer.to(content.getBytes()), null);
        cl_mem wordMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_char * wordLen, Pointer.to(wordToCount.getBytes()), null);

        
        int[] result = new int[1];
        cl_mem resultMem = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_int, Pointer.to(result), null);

        
        cl_program program = clCreateProgramWithSource(context, 1, new String[]{PROGRAM_SOURCE}, null, null);
        clBuildProgram(program, 0, null, null, null, null);
        cl_kernel kernel = clCreateKernel(program, "countWords", null);

        
        clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(textMem));
        clSetKernelArg(kernel, 1, Sizeof.cl_int, Pointer.to(new int[]{textLen}));
        clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(wordMem));
        clSetKernelArg(kernel, 3, Sizeof.cl_int, Pointer.to(new int[]{wordLen}));
        clSetKernelArg(kernel, 4, Sizeof.cl_mem, Pointer.to(resultMem));

        
        long globalWorkSize = textLen / wordLen; 
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, new long[]{globalWorkSize}, null, 0, null, null);

        
        clEnqueueReadBuffer(commandQueue, resultMem, CL_TRUE, 0, Sizeof.cl_int, Pointer.to(result), 0, null, null);

        
        clReleaseMemObject(textMem);
        clReleaseMemObject(wordMem);
        clReleaseMemObject(resultMem);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);

        return result[0];
    }
}