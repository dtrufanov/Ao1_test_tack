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
    private static final int BATCH_SIZE = 10_000;
    private static final int PROCESS_CAPACITY = 1_000;
    private static final String[] STOP_FLAG = new String[0];

    private final int processWorkerCount;
    private final List<ProcessWorker> processWorkers;
    private final ArrayBlockingQueue<String[]> processingQueue;


    public AsyncProcessor(int processWorkerCount) {
        if (processWorkerCount < 1) {
            throw new IllegalArgumentException();
        }
        this.processWorkerCount = processWorkerCount;
        processWorkers = new ArrayList<>(processWorkerCount);
        processingQueue = new ArrayBlockingQueue<>(PROCESS_CAPACITY);
    }

    @Override
    public List<Product> process(File inputDir) {
        try {
            startWorkers();
            processFiles(inputDir);
            waitForProcessingFinish();
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
        String[] batch = new String[BATCH_SIZE];
        int[] i = new int[1];
        i[0] = 0;
        File[] files = inputDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            FileUtils.readLines(file, line -> {
                try {
                    if (line != null) {
                        batch[i[0]++] = line;
                        if (i[0] == BATCH_SIZE) {
                            processingQueue.put(Arrays.copyOf(batch, BATCH_SIZE));
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
        processingQueue.put(STOP_FLAG);
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
