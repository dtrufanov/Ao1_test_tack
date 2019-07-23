package trufanov.ao1.process;

import trufanov.ao1.data.Product;
import trufanov.ao1.data.MetricsProductResultHolder;
import trufanov.ao1.data.ProductResultHolder;

import java.util.concurrent.BlockingQueue;

public class ProcessWorker extends Thread {
    private final BlockingQueue<String> queue;
    private final ProductResultHolder resultHolder;

    public ProcessWorker(BlockingQueue<String> queue) {
        this.queue = queue;
        resultHolder = new MetricsProductResultHolder(1000, 20);
    }

    @Override
    public void run() {
        String line = null;
        while (true) {
            try {
                line = queue.take();
                if ("stop".equals(line)) {
                    queue.put("stop");
                    break;
                }
                resultHolder.add(Product.parseProduct(line));
            } catch (InterruptedException e) {
                //todo
            }
        }
    }
    public ProductResultHolder getResultHolder() {
        return resultHolder;
    }
}
