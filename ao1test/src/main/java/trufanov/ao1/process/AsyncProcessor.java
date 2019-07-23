package trufanov.ao1.process;

import trufanov.ao1.data.Product;
import trufanov.ao1.data.PriceBatchResultHolder;
import trufanov.ao1.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class AsyncProcessor implements Processor {
    private final int processWorkerCount;
    private static final int processCapacity = 10_000;
    private final List<ProcessWorker> processWorkers;
    private final ArrayBlockingQueue<String> processingQueue = new ArrayBlockingQueue<>(processCapacity);


    public AsyncProcessor(int processWorkerCount) {
        if (processWorkerCount < 1) {
            throw new IllegalArgumentException();
        }
        this.processWorkerCount = processWorkerCount;
        processWorkers = new ArrayList<>();
    }

    @Override
    public List<Product> process(File inputDir) {
        try {
            long start = System.currentTimeMillis();
            for (int i = 0; i < processWorkerCount; i++) {
                ProcessWorker processWorker = new ProcessWorker(processingQueue);
                processWorkers.add(processWorker);
                processWorker.start();
            }
            for (File file : inputDir.listFiles()) {
                FileUtils.readLines(file, e -> {
                    try {
                        processingQueue.put(e);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            long current = System.currentTimeMillis();
            System.out.println(current - start);

            processingQueue.put("stop");
            for (ProcessWorker processWorker : processWorkers) {
                processWorker.join();
            }

            System.out.println(System.currentTimeMillis() - current);
            System.out.println(System.currentTimeMillis() - start);

        } catch (InterruptedException e) {

        }
        PriceBatchResultHolder resultHolder = null;
        for (ProcessWorker processWorker : processWorkers) {
            if (resultHolder == null) {
                resultHolder = processWorker.getResultHolder();
            } else {
                resultHolder.addAll(processWorker.getResultHolder().get());
            }
        }
        return resultHolder.get();
    }
}
