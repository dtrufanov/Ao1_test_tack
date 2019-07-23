package trufanov.ao1.test;

import org.junit.Test;
import trufanov.ao1.data.Product;
import trufanov.ao1.data.MetricsProductResultHolder;
import trufanov.ao1.data.ProductResultHolder;
import trufanov.ao1.process.AsyncProcessor;
import trufanov.ao1.process.LinearProcessor;
import trufanov.ao1.process.Processor;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ProcessingTest {
    @Test
    public void testResultHolder() throws IOException {
        baseResultHolderTest("in_1", 1, 1);
        baseResultHolderTest("in_1", 2, 1);
        baseResultHolderTest("in_1", 2, 2);
        baseResultHolderTest("in_1", 4, 2);
        baseResultHolderTest("in_1", 6, 1);
        baseResultHolderTest("in_1", 6, 3);
    }

    private void baseResultHolderTest(String filename, int maxSize, int maxSameSize) throws IOException {
        List<String> lines = TestUtils.readFile("input/" + filename + ".csv");
        ProductResultHolder resultHolder = new MetricsProductResultHolder(maxSize, maxSameSize);
        for (String line : lines) {
            resultHolder.add(Product.parseProduct(line));
        }
        List<String> expected = TestUtils.readFile("input/" + filename + "_check_" + maxSize + "_" + maxSameSize + ".csv");
        List<Product> result = resultHolder.get();
        assertEquals("Unexpected result size", expected.size(), result.size());
        assertEquals(expected, result.stream().map(Product::getLine).collect(Collectors.toList()));
    }

    @Test
    public void testLinearProcessor() throws IOException {
        baseProcessorTest(new LinearProcessor(), "in_1");
    }
    @Test
    public void testAsyncProcessor() throws IOException {
        baseProcessorTest(new AsyncProcessor(1), "in_1");
        baseProcessorTest(new AsyncProcessor(2), "in_1");
    }

    private void baseProcessorTest(Processor processor, String dir) throws IOException {
        List<Product> result = processor.process(TestUtils.getAsResource("input/" + dir, ProcessingTest.class));
        List<String> expected = TestUtils.readFile("input/" + dir + "_check.csv");
        assertEquals("Unexpected result size", expected.size(), result.size());
        assertEquals(expected, result.stream().map(Product::getLine).collect(Collectors.toList()));
    }
}
