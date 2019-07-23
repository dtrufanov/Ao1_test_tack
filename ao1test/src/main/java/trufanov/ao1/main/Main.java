package trufanov.ao1.main;

import trufanov.ao1.data.Product;
import trufanov.ao1.process.AsyncProcessor;
import trufanov.ao1.process.LinearProcessor;
import trufanov.ao1.process.NullProcessor;
import trufanov.ao1.process.Processor;

import java.io.*;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getGlobal();

    public static void main(String[] args) throws IOException {
        LogManager.getLogManager().readConfiguration(Main.class.getClassLoader().getResourceAsStream("log.properties"));

        if (args.length < 2 || args.length > 3) {
            logger.warning("Expected arguments: <input directory> <output file> [<processing thread number>]");
            System.exit(1);
        }
        String inputDirName = args[0];
        String outputFileName = args[1];

        File inputDir = new File(inputDirName);
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            logger.warning("Directory " + inputDirName + " is not found");
            return;
        }

        Processor processor = getProcessor(args);
        logger.info("Start processing using " + processor);
        long start = System.currentTimeMillis();
        List<Product> products = processor.process(inputDir);
        logger.info("Processing is finished in " + (System.currentTimeMillis() - start) + " ms");

        File outputFile = new File(outputFileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Product product : products) {
                writer.write(product.getLine());
                writer.newLine();
            }
        }
    }

    private static Processor getProcessor(String[] args) {
        int workers = Runtime.getRuntime().availableProcessors() - 1;
        if (args.length > 2) {
            workers = Integer.parseInt(args[2]);
        }
        if (workers > 0) {
            return new AsyncProcessor(workers);
        }
        if (workers < 0) {
            return new NullProcessor();
        }
        return new LinearProcessor();
    }
}
