package com.epam.healenium.utils;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * A utility class for handling configuration settings and file operations in the HealConfig system.
 * It provides methods to load, create, and manage configuration properties, directories, and support files.
 */
public class ConfigFactory {

    private static final String CONFIG_DIRECTORY = "HealConfig";
    private static final String CONFIG_FILE_PATH = CONFIG_DIRECTORY + "/application.properties";

    private ConfigFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Loads configuration properties from the specified file path.
     *
     * @param configPath The path to the configuration properties file.
     * @return A Properties object containing the loaded configuration.
     * @throws RuntimeException If an error occurs while loading the properties.
     */
    public static Properties getConfig(String configPath) {
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get(configPath))) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties from file: " + configPath, e);
        }
        return properties;
    }

    /**
     * Loads the default configuration properties from the predefined file path.
     *
     * @return A Properties object containing the default configuration.
     */
    public static Properties getDefaultConfig() {
        return getConfig(CONFIG_FILE_PATH);
    }

    private static Properties createDefaultConfig() {
        createDirectoryIfNeeded(CONFIG_DIRECTORY);

        if (new File(CONFIG_FILE_PATH).exists())
            return getConfig(CONFIG_FILE_PATH);

        // Define default properties
        Properties properties = new Properties();
        properties.setProperty("recovery-tries", "3");
        properties.setProperty("basePath", "build/selenium");
        properties.setProperty("reportPath", "build/reports");
        properties.setProperty("screenshotPath", "build/screenshots/");
        properties.setProperty("heal-enabled", "true");

        try (FileOutputStream outputStream = new FileOutputStream(CONFIG_FILE_PATH)) {
            properties.store(outputStream, "Application Configuration Properties");
            System.out.println("Properties file created successfully at: " + CONFIG_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Error creating the properties file: " + e.getMessage(), e);
        }
        return getConfig(CONFIG_FILE_PATH);
    }

    /**
     * Sets up the HealConfig by creating default configuration properties,
     * directories, and support files as needed.
     */
    public static void setConfig() {
        Properties config = createDefaultConfig();
        createDirectoryIfNeeded(config.getProperty("basePath"));
        createDirectoryIfNeeded(config.getProperty("reportPath"));
        createDirectoryIfNeeded(config.getProperty("screenshotPath"));
        cleanDirectory(Paths.get(config.getProperty("screenshotPath")).toFile());

        createSupportFile("HealConfig/itemsWithAttributes.js", script.jsScript);
        createSupportFile("HealConfig/skippedAttributes.txt", script.skippedAttribute);
        createSupportFile("build/reports/index.html", script.index);
    }

    private static void createDirectoryIfNeeded(String dirPath) {
        Path directoryPath = Paths.get(dirPath);
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
                System.out.println("Directory created successfully: " + dirPath);
            } catch (IOException e) {
                throw new RuntimeException("Error creating directory: " + e.getMessage(), e);
            }
        }
    }

    public static void createSupportFile(String fileName, String content) {
        try {
            if (new File(fileName).exists())
                return;

            // Create a FileWriter with the specified file name
            FileWriter fileWriter = new FileWriter(fileName);

            // Create a BufferedWriter for efficient writing
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Write the content to the file
            bufferedWriter.write(content);

            // Close the BufferedWriter to flush and save the content to the file
            bufferedWriter.close();

            System.out.println("File '" + fileName + "' has been created successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred while creating the File: " + e.getMessage());
        }
    }

    public static void cleanDirectory(File directory) {
        if (!directory.exists()) {
            System.out.println("Directory does not exist: " + directory.getAbsolutePath());
            return;
        }

        if (!directory.isDirectory()) {
            System.out.println("Not a directory: " + directory.getAbsolutePath());
            return;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    cleanDirectory(file);
                }
                if (file.delete()) {
                    System.out.println("Deleted file: " + file.getAbsolutePath());
                } else {
                    System.err.println("Failed to delete file: " + file.getAbsolutePath());
                }
            }
        }

        // After deleting all files and subdirectories, delete the directory itself
        if (directory.delete()) {
            System.out.println("Deleted directory: " + directory.getAbsolutePath());
        } else {
            System.err.println("Failed to delete directory: " + directory.getAbsolutePath());
        }
    }
}
