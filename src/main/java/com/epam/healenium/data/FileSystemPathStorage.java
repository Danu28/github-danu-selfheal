package com.epam.healenium.data;

import com.epam.healenium.treecomparing.Node;
import com.epam.healenium.treecomparing.NodeBuilder;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.codec.digest.DigestUtils;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Provides a file-based storage solution for path data.
 */
public class FileSystemPathStorage implements PathStorage {
    private static final Logger log = LoggerFactory.getLogger(FileSystemPathStorage.class);
    private static final int MAX_FILE_LENGTH = 128;
    private static final String FILENAME_REGEX = "[\\w\\-]+";
    private final Path basePath;
    private final Path reportsPath;
    private final ObjectMapper objectMapper = initMapper();

    /**
     * Creates a new instance of FileSystemPathStorage.
     *
     * @param properties The configuration properties for the storage.
     */
    public FileSystemPathStorage(Properties properties) {
        basePath = Paths.get(properties.getProperty("basePath"));
        reportsPath = Paths.get(properties.getProperty("reportPath"));
        cleanDirectory(Paths.get(properties.getProperty("screenshotPath")).toFile());
        basePath.toFile().mkdirs();
        reportsPath.toFile().mkdirs();
    }

    private ObjectMapper initMapper() {
        SimpleModule module = new SimpleModule("node");
        module.addSerializer(Node.class, new NodeSerializer());
        module.addDeserializer(Node.class, new NodeDeserializer());
        ObjectMapper objectMapper = new ObjectMapper().registerModule(module);
        objectMapper.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);
        return objectMapper;
    }

    /**
     * Persists the last valid path for a given locator and context.
     *
     * @param locator The locator for the element.
     * @param context The context in which the element was found.
     * @param nodes   The list of nodes representing the path.
     */
    public synchronized void persistLastValidPath(By locator, String context, List<Node> nodes) {
        log.debug("* persistLastValidPath start: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        Path persistedNodePath = getPersistedNodePath(locator, context);

        try {
            byte[] newContent = objectMapper.writeValueAsBytes(nodes);
            Files.write(persistedNodePath, newContent);
        } catch (JsonProcessingException var7) {
            log.error("Could not map the contents to JSON!", var7);
        } catch (IOException var8) {
            log.error("Failed to persist last valid path", var8);
        }

        log.debug("* persistLastValidPath finish: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
    }

    /**
     * Retrieves the last valid path for a given locator and context.
     *
     * @param locator The locator for the element.
     * @param context The context in which the element was found.
     * @return The list of nodes representing the last valid path.
     */
    public synchronized List<Node> getLastValidPath(By locator, String context) {
        Path path = getPersistedNodePath(locator, context);
        if (Files.exists(path)) {
            try {
                byte[] bytes = Files.readAllBytes(path);
                return objectMapper.readValue(bytes, List.class);
            } catch (IOException var5) {
                throw new RuntimeException(var5);
            }
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Saves locator information to a report file.
     *
     * @param info The locator information to save.
     * @throws IOException If an I/O error occurs while saving the information.
     */
    public synchronized void saveLocatorInfo(LocatorInfo info) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(reportsPath.resolve("data.json").toFile(), info);
        Path target = reportsPath.resolve("index_old.html");
        if (!Files.exists(target)) {
            log.debug(String.format("File '%s' does not exist.", target));
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream source = classLoader.getResourceAsStream("index_old.html");
            if (source != null) {
                log.debug(String.format("Copy input stream of reading the '%s' resource to '%s' file.", "index_old.html", target));
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            } else {
                log.debug(String.format("Input stream for reading the '%s' resource is null. Copy to '%s' was skipped.", "index_old.html", target));
            }
        } else {
            log.debug(String.format("File '%s' exists.", target));
        }
    }

    private Path getPersistedNodePath(By locator, String context) {
        return basePath.resolve(getFileName(context) + "_" + locator.hashCode());
    }

    private String getFileName(String context) {
        return context.matches(FILENAME_REGEX) && context.length() < MAX_FILE_LENGTH ? context : DigestUtils.md5Hex(context);
    }

    private static void cleanDirectory(File directory) {
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

    private static class NodeDeserializer extends JsonDeserializer<Node> {
        public NodeDeserializer() {
        }

        public Node deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = parser.getCodec();
            TreeNode tree = parser.readValueAsTree();
            String tag = codec.treeToValue(tree.path("tag"), String.class);
            Integer index = codec.treeToValue(tree.path("index"), Integer.class);
            String innerText = codec.treeToValue(tree.path("innerText"), String.class);
            String id = codec.treeToValue(tree.path("id"), String.class);
            String classes = codec.treeToValue(tree.path("classes"), String.class);
            Map<String, String> attributes = codec.treeToValue(tree.path("other"), Map.class);
            attributes.put("id", id);
            attributes.put("class", classes);
            return new NodeBuilder()
                    .setTag(tag)
                    .setIndex(index)
                    .addContent(innerText)
                    .setAttributes(attributes)
                    .build();
        }
    }

    private static class NodeSerializer extends JsonSerializer<Node> {
        public NodeSerializer() {
        }

        public void serializeWithType(Node value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
            WritableTypeId typeId = typeSer.typeId(value, Node.class, JsonToken.START_OBJECT);
            typeSer.writeTypePrefix(gen, typeId);
            serialize(value, gen, serializers);
            typeSer.writeTypeSuffix(gen, typeId);
        }

        public void serialize(Node value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStringField("tag", value.getTag());
            gen.writeNumberField("index", value.getIndex());
            gen.writeStringField("innerText", value.getInnerText());
            gen.writeStringField("id", value.getId());
            gen.writeStringField("classes", String.join(" ", value.getClasses()));
            gen.writeObjectField("other", value.getOtherAttributes());
            gen.flush();
        }
    }
}
