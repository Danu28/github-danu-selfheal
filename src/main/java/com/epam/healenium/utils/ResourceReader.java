package com.epam.healenium.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A utility class for reading resources from files.
 */
public class ResourceReader {

    private ResourceReader() {
        // Private constructor to prevent instantiation
    }

    /**
     * Reads a resource from a file and applies a function to its content.
     *
     * @param filePath  The path to the resource file.
     * @param function  The function to apply to the content of the file.
     * @param <T>       The type of the result produced by the function.
     * @return The result produced by applying the function to the file's content.
     * @throws IllegalStateException If an error occurs while reading the resource.
     */
    public static <T> T readResource(String filePath, Function<Stream<String>, T> function) {
        try {
            Path resourcePath = Paths.get(filePath);

            // If the path is relative, resolve it against the current working directory
            if (!resourcePath.isAbsolute()) {
                Path currentDirectory = Paths.get("").toAbsolutePath();
                resourcePath = currentDirectory.resolve(resourcePath);
            }

            try (InputStream stream = Files.newInputStream(resourcePath);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                return function.apply(reader.lines());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error reading resource from path: " + filePath, e);
        }
    }
}
