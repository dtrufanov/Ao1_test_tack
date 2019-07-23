package trufanov.ao1.test;

import org.junit.Test;
import trufanov.ao1.data.Product;
import trufanov.ao1.data.PriceBatchResultHolder;

import java.io.IOException;
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
        PriceBatchResultHolder resultHolder = new PriceBatchResultHolder(maxSize, maxSameSize);
        for (String line : lines) {
            resultHolder.add(Product.parseProduct(line));
        }
        List<String> expected = TestUtils.readFile("input/" + filename + "_check_" + maxSize + "_" + maxSameSize + ".csv");
        List<Product> result = resultHolder.get();
        assertEquals("Unexpected result size", expected.size(), result.size());
        assertEquals(expected, result.stream().map(Product::getLine).collect(Collectors.toList()));
    }
}
