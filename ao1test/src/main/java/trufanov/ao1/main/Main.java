package trufanov.ao1.main;

import trufanov.ao1.data.Product;
import trufanov.ao1.data.ResultHolder;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Expected two arguments: the input files directory and the output file name.");
            System.exit(-1);
        }
        ResultHolder resultHolder = new ResultHolder<Long>(1000, 20, Product::getId);
        String inputDirName = args[0];
        String outputFileName = args[1];
        File inputDir = new File(inputDirName);
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            System.out.println("Directory " + inputDirName + " is not found");
        }
        for (File file : inputDir.listFiles()) {
            try (BufferedReader in = new BufferedReader(new FileReader(file))) {
                String str;
                while ((str = in.readLine()) != null) {
                    resultHolder.add(Product.parseProduct(str));
                }
            } catch (IOException e) {
                System.out.println("Failed to read from " + file.getAbsolutePath());
            }
        }
        File outputFile = new File(outputFileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Object product : resultHolder.getProducts()) {
                writer.write(((Product) product).getLine());
                writer.newLine();
            }
        }
    }
}
