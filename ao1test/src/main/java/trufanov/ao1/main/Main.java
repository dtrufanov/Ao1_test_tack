package trufanov.ao1.main;

import trufanov.ao1.data.Product;
import trufanov.ao1.process.AsyncProcessor;
import trufanov.ao1.process.LinearProcessor;
import trufanov.ao1.process.Processor;

import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 2 || args.length > 3) {
            System.out.println("Expected arguments: <input directory> <output file> [<processing thread number>]");
            System.exit(1);
        }
        String inputDirName = args[0];
        String outputFileName = args[1];

        File inputDir = new File(inputDirName);
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            System.out.println("Directory " + inputDirName + " is not found");
        }
        Processor processor = getProcessor(args);
        List<Product> products = processor.process(inputDir);
        File outputFile = new File(outputFileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Product product : products) {
                writer.write(product.getLine());
                writer.newLine();
            }
        }
    }

    private static Processor getProcessor(String[] args) {
        int workers = 0;
        if (args.length > 2) {
            workers = Integer.parseInt(args[3]);
        }
        return workers > 0 ? new AsyncProcessor(workers) : new LinearProcessor();
    }
}
