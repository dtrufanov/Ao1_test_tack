package trufanov.ao1.process;

import trufanov.ao1.data.Product;
import trufanov.ao1.util.FileUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class NullProcessor implements Processor {

    @Override
    public List<Product> process(File inputDir) {
        File[] files = inputDir.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        for (File file : files) {
            FileUtils.readLines(file, line -> {});
        }
        return Collections.emptyList();
    }
}
