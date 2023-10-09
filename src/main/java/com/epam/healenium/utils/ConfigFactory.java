package com.epam.healenium.utils;

import org.openqa.selenium.io.FileHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * A utility class for handling configuration settings and file operations in the HealConfig system.
 * It provides methods to load, create, and manage configuration properties, directories, and support files.
 */
public class ConfigFactory {

    private static final String CONFIG_DIRECTORY = "HealConfig/";
    private static final String CONFIG_FILE_PATH = CONFIG_DIRECTORY + "application.properties";

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
        FileHandler.createDir(new File(CONFIG_DIRECTORY));

        if (new File(CONFIG_FILE_PATH).exists())
            return getConfig(CONFIG_FILE_PATH);

        // Define default properties
        Properties properties = new Properties();
        properties.setProperty("recovery-tries", "3");
        properties.setProperty("match-score", ".75");
        properties.setProperty("basePath", "heal-output/selenium");
        properties.setProperty("reportPath", "heal-output/reports");
        properties.setProperty("screenshotPath", "heal-output/screenshots/");
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
        FileHandler.createDir(new File(config.getProperty("basePath")));
        FileHandler.createDir(new File(config.getProperty("reportPath")));
        FileHandler.createDir(new File(config.getProperty("screenshotPath")));

        cleanDirectory(Paths.get(config.getProperty("screenshotPath")).toFile());

        createSupportFile(CONFIG_DIRECTORY+"itemsWithAttributes.js", script.jsScript);
        createSupportFile(CONFIG_DIRECTORY+"skippedAttributes.txt", script.skippedAttribute);
        createSupportFile(config.getProperty("reportPath")+"/index.html", script.index);
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
    }
}
