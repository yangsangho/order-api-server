package util;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class JsonFileReader {
    public static String read(String filePath) throws IOException {
        File file = ResourceUtils.getFile("classpath:json/" + filePath + ".json");
        return new String(Files.readAllBytes(file.toPath()));
    }
}
