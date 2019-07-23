package trufanov.ao1.process;

import trufanov.ao1.data.Product;
import trufanov.ao1.data.ProductResultHolder;
import trufanov.ao1.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class AsyncProcessor implements Processor {
    private final int processWorkerCount;
    private final int batchSize = 10_000;
    private static final int processCapacity = 1_000;
    private final List<ProcessWorker> processWorkers;
    private final ArrayBlockingQueue<String[]> processingQueue = new ArrayBlockingQueue<>(processCapacity);


    public AsyncProcessor(int processWorkerCount) {
        if (processWorkerCount < 1) {
            throw new IllegalArgumentException();
        }
        this.processWorkerCount = processWorkerCount;
        processWorkers = new ArrayList<>(processWorkerCount);
    }

    @Override
    public List<Product> process(File inputDir) {
        try {

            long start = System.currentTimeMillis();
            startWorkers();
            processFiles(inputDir);
            long current = System.currentTimeMillis();
            System.out.println(current - start);

            waitForProcessingFinish();
            System.out.println(System.currentTimeMillis() - current);
            System.out.println(System.currentTimeMillis() - start);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        }
        return getResult();
    }

    private void startWorkers() {
        processWorkers.clear();
        processingQueue.clear();
        for (int i = 0; i < processWorkerCount; i++) {
            ProcessWorker processWorker = new ProcessWorker(processingQueue);
            processWorkers.add(processWorker);
            processWorker.start();
        }
    }

    private void processFiles(File inputDir) throws InterruptedException {
        if (inputDir == null) {
            throw new NullPointerException();
        }
        String[] batch = new String[batchSize];
        int[] i = new int[1];
        i[0] = 0;
        for (File file : inputDir.listFiles()) {
            FileUtils.readLines(file, line -> {
                try {
                    if (line != null) {
                        batch[i[0]++] = line;
                        if (i[0] == batchSize) {
                            processingQueue.put(Arrays.copyOf(batch, batchSize));
                            Arrays.fill(batch, null);
                            i[0] = 0;
                        }
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            });
            if (i[0] > 0) {
                processingQueue.put(Arrays.copyOf(batch, i[0]));
            }
        }
    }

    private void waitForProcessingFinish() throws InterruptedException {
        processingQueue.put(new String[0]);
        for (ProcessWorker processWorker : processWorkers) {
            processWorker.join();
        }
    }

    private List<Product> getResult() {
        ProductResultHolder resultHolder = null;
        for (ProcessWorker processWorker : processWorkers) {
            if (resultHolder == null) {
                resultHolder = processWorker.getResultHolder();
            } else {
                resultHolder.addAll(processWorker.getResultHolder().get());
            }
        }
        return resultHolder == null ? Collections.emptyList() : resultHolder.get();
    }
}
