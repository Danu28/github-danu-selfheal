package com.epam.healenium.utils;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class ConfigFactory {

    private static final String CONFIG_DIRECTORY = "HealConfig";
    private static final String CONFIG_FILE_PATH = CONFIG_DIRECTORY + "/application.properties";

    private ConfigFactory() {
        // Private constructor to prevent instantiation
    }

    public static Properties getConfig(String configPath) {
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get(configPath))) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties from file: " + configPath, e);
        }
        return properties;
    }

    public static Properties getDefaultConfig() {
//        createConfigDirectoryIfNeeded();

        // Define default properties
        Properties properties = new Properties();
        properties.setProperty("recovery-tries", "3");
        properties.setProperty("score-cap", "0.7");
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

    static {
        createConfigDirectoryIfNeeded();
        createSupportFile("HealConfig/itemsWithAttributes.js", script.jsScript);
        createSupportFile("HealConfig/skippedAttributes.txt", script.skippedAttribute);
        createSupportFile("build/reports/index.html", script.index);
    }

    private static void createConfigDirectoryIfNeeded() {
        Path directoryPath = Paths.get(CONFIG_DIRECTORY);
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
                System.out.println("Directory created successfully: " + CONFIG_DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("Error creating directory: " + e.getMessage(), e);
            }
        }
    }

    public static void createSupportFile(String fileName, String content) {
        try {
            // Check if the file already exists
            File file = new File(fileName);
            if (file.exists())
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
}
