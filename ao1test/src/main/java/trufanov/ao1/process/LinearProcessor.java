package trufanov.ao1.process;

import trufanov.ao1.data.Product;
import trufanov.ao1.data.ResultHolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class LinearProcessor implements Processor {

    public LinearProcessor() {
    }

    @Override
    public List<Product> process(File inputDir) {
        long start = System.currentTimeMillis();
        ResultHolder<Long> resultHolder = new ResultHolder<>(1000, 20, Product::getId);
        for (File file : inputDir.listFiles()) {
            try (BufferedReader in = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = in.readLine()) != null) {
                    resultHolder.add(Product.parseProduct(line));
                }
            } catch (IOException e) {
                System.out.println("Failed to read from " + file.getAbsolutePath());
            }
        }

        long current = System.currentTimeMillis();
        System.out.println(current - start);
        return resultHolder.getProducts();
    }
}
