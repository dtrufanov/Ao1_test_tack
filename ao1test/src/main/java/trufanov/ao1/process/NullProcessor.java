package trufanov.ao1.process;

import trufanov.ao1.data.Product;
import trufanov.ao1.util.FileUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class NullProcessor implements Processor {

    @Override
    public List<Product> process(File inputDir) {
        long start = System.currentTimeMillis();
        for (File file : inputDir.listFiles()) {
            FileUtils.readLines(file, line -> {});
        }
        System.out.println(System.currentTimeMillis() - start);
        return Collections.emptyList();
    }
}
