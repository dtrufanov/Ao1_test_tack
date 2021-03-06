package trufanov.ao1.process;

import trufanov.ao1.data.Product;
import trufanov.ao1.data.MetricsProductResultHolder;
import trufanov.ao1.data.ProductResultHolder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class ProcessWorker implements Callable<ProductResultHolder> {
    private final BlockingQueue<String[]> queue;
    private final ProductResultHolder resultHolder;

    ProcessWorker(BlockingQueue<String[]> queue) {
        this.queue = queue;
        resultHolder = new MetricsProductResultHolder(1000, 20);
    }

    @Override
    public ProductResultHolder call() throws Exception {
        String[] batch = null;
        while (true) {
            batch = queue.take();
            if (batch.length == 0) {
                queue.put(batch);
                break;
            }
            for (String s : batch) {
                if (s != null) {
                    resultHolder.add(Product.parseProduct(s));
                }
            }
        }
        return resultHolder;
    }
}
