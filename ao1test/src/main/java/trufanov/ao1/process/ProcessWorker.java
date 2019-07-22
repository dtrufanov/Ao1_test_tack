package trufanov.ao1.process;

import trufanov.ao1.data.Product;
import trufanov.ao1.data.PriceBatchResultHolder;

import java.util.concurrent.BlockingQueue;

public class ProcessWorker extends Thread {
    private final BlockingQueue<String> queue;
    private final PriceBatchResultHolder<Long> resultHolder;

    public ProcessWorker(BlockingQueue<String> queue) {
        this.queue = queue;
        resultHolder = new PriceBatchResultHolder<>(1000, 20, Product::getId);
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
    public PriceBatchResultHolder<Long> getResultHolder() {
        return resultHolder;
    }
}
