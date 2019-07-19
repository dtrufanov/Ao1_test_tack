package trufanov.ao1.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class TestUtils {
    public static List<String> readFile(String filename) throws IOException {
        return Files.lines(getAsResource(filename, TestUtils.class).toPath())
                .collect(Collectors.toList());
    }

    public static File getAsResource(String name, Class<?> clazz) {
        URL url = clazz.getClassLoader().getResource(name);
        if (url == null) {
            throw new IllegalStateException("Не найден ресурс: " + name);
        }
        return new File(url.getFile());
    }
}
