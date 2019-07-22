package trufanov.ao1.process;

import trufanov.ao1.data.Product;
import trufanov.ao1.data.ResultHolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class AsyncProcessor implements Processor {
    private final int processWorkerCount;
    private static final int processCapacity = 100_000_000;
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
                try (BufferedReader in = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        processingQueue.put(line);
                    }
                } catch (IOException e) {
                    System.out.println("Failed to read from " + file.getAbsolutePath());
                }
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
        ResultHolder<Long> resultHolder = null;
        for (ProcessWorker processWorker : processWorkers) {
            if (resultHolder == null) {
                resultHolder = processWorker.getResultHolder();
            } else {
                resultHolder.merge(processWorker.getResultHolder());
            }
        }
        return resultHolder.getProducts();
    }
}
