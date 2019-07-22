package trufanov.ao1.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

public class FileUtils {
    public static void readLines(File file, Consumer<String> consumer) {
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                consumer.accept(line);
            }
        } catch (IOException e) {
            System.out.println("Failed to read from " + file.getAbsolutePath());
        }
    }
}
