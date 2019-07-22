package trufanov.ao1.process;

import trufanov.ao1.data.Product;
import trufanov.ao1.data.PriceBatchResultHolder;
import trufanov.ao1.util.FileUtils;

import java.io.File;
import java.util.List;

public class LinearProcessor implements Processor {

    public LinearProcessor() {
    }

    @Override
    public List<Product> process(File inputDir) {
        long start = System.currentTimeMillis();
        PriceBatchResultHolder<Long> resultHolder = new PriceBatchResultHolder<>(1000, 20, Product::getId);
        for (File file : inputDir.listFiles()) {
            FileUtils.readLines(file, line -> resultHolder.add(Product.parseProduct(line)));
        }

        long current = System.currentTimeMillis();
        System.out.println(current - start);
        return resultHolder.getProducts();
    }
}
