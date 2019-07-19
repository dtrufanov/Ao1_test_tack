package trufanov.ao1.test;

import org.junit.Test;
import trufanov.ao1.data.Product;
import trufanov.ao1.data.ResultHolder;

import java.io.IOException;
import java.util.List;

public class ResultHolderTest {
    @Test
    public void test() throws IOException {
        List<String> lines = TestUtils.readFile("input/sample.csv");
        ResultHolder resultHolder = new ResultHolder(10, 3);
        for (String line : lines) {
            resultHolder.add(Product.parseProduct(line));
        }
        System.out.println(resultHolder.getProducts());
    }
}
