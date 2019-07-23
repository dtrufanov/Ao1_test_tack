package trufanov.ao1.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class FileUtils {
    private static final Logger logger = Logger.getGlobal();
    private FileUtils(){}

    public static void readLines(File file, Consumer<String> consumer) {
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            in.lines().forEach(consumer);
        } catch (IOException e) {
            logger.severe("Failed to read from " + file.getAbsolutePath());
        }
    }
}
