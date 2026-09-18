package com.epam.healenium.data;

import com.epam.healenium.treecomparing.Node;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Optional H2 storage — requires com.h2database:h2 on classpath (optional dep).
 * Single file heal.db, MVStore WAL, row-level lock tolerates parallel workers
 * far better than SQLite single-writer. Falls back to file if H2 missing.
 */
public class H2PathStorage implements PathStorage {
    private static final Logger log = LoggerFactory.getLogger(H2PathStorage.class);
    private final String jdbcUrl;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Path reportsPath;

    public H2PathStorage(Properties properties) {
        String dbFile = properties.getProperty("heal.db", "heal-output/heal.db");
        try {
            Files.createDirectories(Paths.get(dbFile).getParent());
        } catch (IOException e) {
            log.warn("Failed to create db parent dir", e);
        }
        this.jdbcUrl = "jdbc:h2:file:" + Paths.get(dbFile).toAbsolutePath().toString().replace("\\", "/") + ";AUTO_SERVER=TRUE;LOCK_TIMEOUT=10000";
        this.reportsPath = Paths.get(properties.getProperty("reportPath", "heal-output/reports"));
        init();
    }

    private void init() {
        try (Connection c = DriverManager.getConnection(jdbcUrl);
             Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS heal_paths (locatorHash VARCHAR(512) PRIMARY KEY, locator VARCHAR(2000), context VARCHAR(512), nodesJson CLOB, updatedAt TIMESTAMP)");
            s.execute("CREATE TABLE IF NOT EXISTS heal_events (id BIGINT AUTO_INCREMENT PRIMARY KEY, locatorInfo CLOB, createdAt TIMESTAMP)");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to init H2 heal.db at " + jdbcUrl, e);
        }
    }

    private String key(By locator, String context) {
        return context + "_" + locator.hashCode();
    }

    @Override
    public synchronized void persistLastValidPath(By locator, String context, List<Node> nodes) {
        String k = key(locator, context);
        try {
            String json = mapper.writeValueAsBytes(nodes) != null ? mapper.writeValueAsString(nodes) : "[]";
            try (Connection c = DriverManager.getConnection(jdbcUrl);
                 PreparedStatement ps = c.prepareStatement("MERGE INTO heal_paths (locatorHash, locator, context, nodesJson, updatedAt) KEY(locatorHash) VALUES (?,?,?,?,CURRENT_TIMESTAMP)")) {
                ps.setString(1, k);
                ps.setString(2, locator.toString());
                ps.setString(3, context);
                ps.setString(4, json);
                ps.executeUpdate();
            }
        } catch (JsonProcessingException e) {
            log.error("Could not map nodes to JSON", e);
        } catch (SQLException e) {
            log.error("Failed to persist H2 path", e);
        }
    }

    @Override
    public synchronized List<Node> getLastValidPath(By locator, String context) {
        String k = key(locator, context);
        try (Connection c = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = c.prepareStatement("SELECT nodesJson FROM heal_paths WHERE locatorHash=?")) {
            ps.setString(1, k);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String json = rs.getString(1);
                    return mapper.readValue(json, List.class);
                }
            }
        } catch (Exception e) {
            log.error("Failed to get H2 path", e);
        }
        return Collections.emptyList();
    }

    @Override
    public synchronized void saveLocatorInfo(LocatorInfo info) throws IOException {
        try (Connection c = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = c.prepareStatement("INSERT INTO heal_events (locatorInfo, createdAt) VALUES (?, CURRENT_TIMESTAMP)")) {
            ps.setString(1, mapper.writeValueAsString(info));
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IOException("Failed to save H2 locatorInfo", e);
        }
        // also keep file reports for backward compat
        try {
            Files.createDirectories(reportsPath);
            mapper.writeValue(reportsPath.resolve("data.json").toFile(), info);
        } catch (Exception e) {
            log.warn("Failed to write data.json mirror", e);
        }
    }
}
