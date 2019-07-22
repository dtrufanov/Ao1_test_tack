package trufanov.ao1.process;

import trufanov.ao1.data.Product;
import trufanov.ao1.data.ResultHolder;

import java.util.concurrent.BlockingQueue;

public class ProcessWorker extends Thread {
    private final BlockingQueue<String> queue;
    private final ResultHolder<Long> resultHolder;

    public ProcessWorker(BlockingQueue<String> queue) {
        this.queue = queue;
        resultHolder = new ResultHolder<>(1000, 20, Product::getId);
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
    public ResultHolder<Long> getResultHolder() {
        return resultHolder;
    }
}
