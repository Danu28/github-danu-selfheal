package com.epam.healenium.data;

import java.util.Properties;

/**
 * Factory for PathStorage — pluggable file vs H2.
 * Default is FileSystemPathStorage (zero dep). H2 is optional and loaded reflectively
 * so main jar stays lean when storage.mode=file.
 */
public final class PathStorageFactory {

    private PathStorageFactory() {}

    public static PathStorage create(Properties config) {
        String mode = config.getProperty("storage.mode", "file").trim().toLowerCase();
        if ("h2".equals(mode)) {
            try {
                Class<?> clazz = Class.forName("com.epam.healenium.data.H2PathStorage");
                return (PathStorage) clazz.getConstructor(Properties.class).newInstance(config);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("storage.mode=h2 but H2 not on classpath. Add com.h2database:h2:2.2.224", e);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to create H2PathStorage", e);
            }
        }
        return new FileSystemPathStorage(config);
    }
}
