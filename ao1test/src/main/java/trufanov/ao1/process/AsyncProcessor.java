package trufanov.ao1.process;

import trufanov.ao1.data.Product;
import trufanov.ao1.data.ProductResultHolder;
import trufanov.ao1.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class AsyncProcessor implements Processor {
    Logger logger = Logger.getGlobal();

    private static final int BATCH_SIZE = 1_000;
    private static final int PROCESS_CAPACITY = 25;
    private static final String[] STOP_FLAG = new String[0];

    private final int processWorkerCount;
    private final List<Future<ProductResultHolder>> futures;
    private final ArrayBlockingQueue<String[]> processingQueue;
    private final ExecutorService executorService;


    public AsyncProcessor(int processWorkerCount) {

        if (processWorkerCount < 1) {
            throw new IllegalArgumentException();
        }
        this.processWorkerCount = processWorkerCount;
        executorService = Executors.newFixedThreadPool(processWorkerCount);
        futures = new ArrayList<>(processWorkerCount);
        processingQueue = new ArrayBlockingQueue<>(PROCESS_CAPACITY);
    }

    @Override
    public String toString() {
        return "AsyncProcessor{" +
                "processWorkerCount=" + processWorkerCount +
                '}';
    }

    @Override
    public List<Product> process(File inputDir) {
        try {
            startWorkers();
            processFiles(inputDir);
            processingQueue.put(STOP_FLAG);
            return getResult();
        } catch (ExecutionException e) {
            logger.severe("Failed to process: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("Failed to process: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
        return Collections.emptyList();
    }

    private void startWorkers() {
        processingQueue.clear();
        futures.clear();
        for (int i = 0; i < processWorkerCount; i++) {
            futures.add(executorService.submit(new ProcessWorker(processingQueue)));
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

    private List<Product> getResult() throws ExecutionException, InterruptedException {
        ProductResultHolder resultHolder = null;
        for (Future<ProductResultHolder> future : futures) {
            if (resultHolder == null) {
                resultHolder = future.get();
            } else {
                resultHolder.addAll(future.get().get());
            }
        }
        return resultHolder == null ? Collections.emptyList() : resultHolder.get();
    }
}
