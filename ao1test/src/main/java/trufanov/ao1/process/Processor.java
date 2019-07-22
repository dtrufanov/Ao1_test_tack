package trufanov.ao1.process;

import trufanov.ao1.data.Product;

import java.io.File;
import java.util.List;

public interface Processor {
    List<Product> process(File inputDir);
}
