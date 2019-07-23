package trufanov.ao1.process;

import trufanov.ao1.data.Product;
import trufanov.ao1.data.MetricsProductResultHolder;
import trufanov.ao1.data.ProductResultHolder;
import trufanov.ao1.util.FileUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class LinearProcessor implements Processor {
    @Override
    public List<Product> process(File inputDir) {
        ProductResultHolder resultHolder = new MetricsProductResultHolder(1000, 20);
        File[] files = inputDir.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        for (File file : files) {
            FileUtils.readLines(file, line -> resultHolder.add(Product.parseProduct(line)));
        }
        return resultHolder.get();
    }
}
