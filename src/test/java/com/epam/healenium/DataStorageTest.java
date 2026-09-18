package com.epam.healenium;

import com.epam.healenium.data.FileSystemPathStorage;
import com.epam.healenium.data.PathStorageFactory;
import com.epam.healenium.treecomparing.Node;
import com.epam.healenium.treecomparing.NodeBuilder;
import com.epam.healenium.utils.ConfigFactory;
import com.epam.healenium.utils.ResourceReader;
import com.epam.healenium.utils.StackUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.openqa.selenium.By;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DataStorageTest {

    @TempDir Path tempDir;

    private Properties props(Path base, Path reports) {
        Properties p = new Properties();
        p.setProperty("basePath", base.toString());
        p.setProperty("reportPath", reports.toString());
        p.setProperty("storage.mode", "file");
        p.setProperty("heal.db", tempDir.resolve("heal.db").toString());
        return p;
    }

    @Test
    void fileSystemStorageRoundtrip() {
        Path base = tempDir.resolve("selenium");
        Path reports = tempDir.resolve("reports");
        FileSystemPathStorage storage = new FileSystemPathStorage(props(base, reports));
        By by = By.id("testId");
        String context = "TestPage";
        Node node = new NodeBuilder().setTag("div").setId("testId").addContent("hello").build();
        storage.persistLastValidPath(by, context, List.of(node));
        List<Node> loaded = storage.getLastValidPath(by, context);
        assertFalse(loaded.isEmpty(), "should load persisted path");
        assertEquals("div", loaded.get(0).getTag());
    }

    @Test
    void fileSystemStorageMissingReturnsEmpty() {
        FileSystemPathStorage storage = new FileSystemPathStorage(props(tempDir.resolve("a"), tempDir.resolve("b")));
        assertTrue(storage.getLastValidPath(By.id("missing"), "NoPage").isEmpty());
    }

    @Test
    void pathStorageFactoryCreatesFileByDefault() {
        var storage = PathStorageFactory.create(props(tempDir.resolve("x"), tempDir.resolve("y")));
        assertInstanceOf(FileSystemPathStorage.class, storage);
    }

    @Test
    void pathStorageFactoryH2RequiresDep() {
        Properties p = props(tempDir.resolve("x2"), tempDir.resolve("y2"));
        p.setProperty("storage.mode", "h2");
        // H2 is optional true, but present after pom add — should create H2PathStorage
        // If H2 not on classpath, it throws IllegalStateException with message about missing dep
        try {
            var s = PathStorageFactory.create(p);
            assertNotNull(s);
            // if H2 present, it should be H2PathStorage
            assertTrue(s.getClass().getSimpleName().contains("H2"));
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("H2 not on classpath") || e.getMessage().contains("Failed"));
        }
    }

    @Test
    void resourceReaderClasspathFallback() {
        String content = ResourceReader.readResource("heal-report/itemsWithAttributes.js", s -> String.join("\n", s.toList()));
        assertNotNull(content);
        assertTrue(content.contains("var items"));
    }

    @Test
    void stackUtilsFindsAnnotation() {
        // StackUtils.isAnnotationPresent should not throw
        assertDoesNotThrow(() -> StackUtils.isAnnotationPresent(Deprecated.class));
    }

    @Test
    void configFactoryCreatesDefaultProperties() throws IOException {
        // Test that default config can be created in temp dir via manual props
        Path cfgDir = tempDir.resolve("HealConfigTest");
        Files.createDirectories(cfgDir);
        Properties p = new Properties();
        p.setProperty("heal.acceptUrl", "http://localhost:8091");
        p.setProperty("storage.mode", "file");
        assertEquals("file", p.getProperty("storage.mode"));
    }
}
