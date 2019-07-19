package trufanov.ao1.test;

import org.junit.Test;
import trufanov.ao1.data.Product;
import trufanov.ao1.data.ResultHolder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ResultHolderTest {
    @Test
    public void test() throws IOException {
        baseTest("test_1", 1, 1);
        baseTest("test_1", 2, 1);
        baseTest("test_1", 2, 2);
        baseTest("test_1", 4, 2);
        baseTest("test_1", 6, 1);
        baseTest("test_1", 6, 3);
    }

    public void baseTest(String filename, int maxSize, int maxSameSize) throws IOException {
        List<String> lines = TestUtils.readFile("input/" + filename + ".csv");
//        Collections.shuffle(lines);
        ResultHolder resultHolder = new ResultHolder<>(maxSize, maxSameSize, Product::getId);
        for (String line : lines) {
            resultHolder.add(Product.parseProduct(line));
        }
        List<String> expected = TestUtils.readFile("input/" + filename + "_check_" + maxSize + "_" + maxSameSize + ".csv");
        List<Product> result = resultHolder.getProducts();
        assertEquals("Unexpected result size", expected.size(), result.size());
        assertEquals(expected, result.stream().map(Product::getLine).collect(Collectors.toList()));
    }
}
