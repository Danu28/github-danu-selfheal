/**
 * The SelfHealingEngine class is responsible for self-healing functionality
 * in WebDriver-based automation tests.
 */
package com.epam.healenium;

import com.epam.healenium.annotation.DisableHealing;
import com.epam.healenium.data.FileSystemPathStorage;
import com.epam.healenium.data.LocatorInfo;
import com.epam.healenium.data.PathStorage;
import com.epam.healenium.handlers.proxy.SelfHealingProxyInvocationHandler;
import com.epam.healenium.treecomparing.*;
import com.epam.healenium.utils.ProxyFactory;
import com.epam.healenium.utils.ResourceReader;
import com.epam.healenium.utils.StackUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The SelfHealingEngine class provides self-healing functionality for WebDriver-based tests.
 */
public class SelfHealingEngine {
    private static final Logger log = LoggerFactory.getLogger(SelfHealingEngine.class);
    private static final String SCRIPT = ResourceReader.readResource("HealConfig/itemsWithAttributes.js", s -> s.collect(Collectors.joining()));

    @Getter
    private final Properties config;
    private final WebDriver webDriver;
    private final PathStorage storage;
    private final int recoveryTries;

    private final double matchScore;
    private final List<Set<SelectorComponent>> selectorDetailLevels;

    public double getMatchScore()
    {
        return matchScore;
    }

    /**
     * Constructs a SelfHealingEngine instance.
     *
     * @param delegate The WebDriver delegate.
     * @param config   Properties containing configuration settings.
     */
    SelfHealingEngine(WebDriver delegate, Properties config) {
        this.webDriver = delegate;
        this.config = config;
        this.storage = new FileSystemPathStorage(config);
        this.recoveryTries = Integer.parseInt(config.getProperty("recovery-tries"));
        this.matchScore = Double.parseDouble(config.getProperty("match-score"));
        // Initialize selectorDetailLevels
        this.selectorDetailLevels = initSelectorDetailLevels();
    }


    /**
     * Create a self-healing driver with custom configuration.
     *
     * @param delegate The delegate WebDriver.
     * @param config   The custom configuration properties.
     * @return A self-healing driver instance.
     */
    static SelfHealingDriver create(WebDriver delegate, Properties config) {
        return create(new SelfHealingEngine(delegate, config));
    }

    /**
     * Create a self-healing driver with a pre-configured self-healing engine.
     *
     * @param engine The self-healing engine instance.
     * @return A self-healing driver instance.
     */
    public static SelfHealingDriver create(SelfHealingEngine engine) {
        ClassLoader classLoader = SelfHealingDriver.class.getClassLoader();
        Class<? extends WebDriver> driverClass = engine.getWebDriver().getClass();
        SelfHealingProxyInvocationHandler handler = new SelfHealingProxyInvocationHandler(engine);
        return ProxyFactory.createDriverProxy(classLoader, handler, driverClass);
    }

    /**
     * Initializes the selectorDetailLevels list with selector detail levels.
     *
     * @return List of selector detail levels.
     */
    private List<Set<SelectorComponent>> initSelectorDetailLevels() {
        List<Set<SelectorComponent>> temp = new ArrayList<>();
        temp.add(EnumSet.of(SelectorComponent.TAG, SelectorComponent.ID));
        temp.add(EnumSet.of(SelectorComponent.TAG, SelectorComponent.CLASS));
        temp.add(EnumSet.of(SelectorComponent.PARENT, SelectorComponent.TAG, SelectorComponent.ID, SelectorComponent.CLASS));
        temp.add(EnumSet.of(SelectorComponent.PARENT, SelectorComponent.TAG, SelectorComponent.CLASS, SelectorComponent.POSITION));
        temp.add(EnumSet.of(SelectorComponent.PARENT, SelectorComponent.TAG, SelectorComponent.ID, SelectorComponent.CLASS, SelectorComponent.ATTRIBUTES));
        temp.add(EnumSet.of(SelectorComponent.PATH));
        return Collections.unmodifiableList(temp);
    }

    /**
     * Saves the path of a WebElement for self-healing purposes.
     *
     * @param by         The PageAwareBy object representing the element locator.
     * @param webElement The WebElement to save the path for.
     */
    public void savePath(PageAwareBy by, WebElement webElement) {
        log.debug("* savePath start: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        List<Node> nodePath = getNodePath(webElement);
        storage.persistLastValidPath(by, by.getPageName(), nodePath);
        log.debug("* savePath finish: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
    }

    /**
     * Saves the LocatorInfo for self-healing purposes.
     *
     * @param info The LocatorInfo object to save.
     */
    @SneakyThrows
    public void saveLocator(LocatorInfo info) {
        try {
            storage.saveLocatorInfo(info);
        } catch (Throwable var3) {
            throw var3;
        }
    }

    /**
     * Retrieves the node path of a WebElement.
     *
     * @param webElement The WebElement to retrieve the node path for.
     * @return List of Node objects representing the node path.
     */
    private List<Node> getNodePath(WebElement webElement) {
        log.debug("* getNodePath start: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        String data = (String) executor.executeScript(SCRIPT, webElement);
        List<Node> path = new LinkedList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode treeNode = mapper.readTree(data);
            if (treeNode.isArray()) {
                Iterator<JsonNode> iterator = treeNode.iterator();
                while (iterator.hasNext()) {
                    JsonNode jsonNode = iterator.next();
                    Node node = toNode(mapper.treeAsTokens(jsonNode));
                    path.add(node);
                }
            }
        } catch (Exception var10) {
            log.error("Failed to get element node path!", var10);
        }

        log.debug("* getNodePath finish: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        return path;
    }

    /**
     * Converts a JsonParser into a Node object.
     *
     * @param parser The JsonParser to convert.
     * @return The Node object.
     * @throws IOException If an error occurs during parsing.
     */
    private Node toNode(JsonParser parser) throws IOException {
        ObjectCodec codec = parser.getCodec();
        TreeNode tree = parser.readValueAsTree();
        String tag = codec.treeToValue(tree.path("tag"), String.class);
        Integer index = codec.treeToValue(tree.path("index"), Integer.class);
        String innerText = codec.treeToValue(tree.path("innerText"), String.class);
        String id = codec.treeToValue(tree.path("id"), String.class);
        Set<String> classes = codec.treeToValue(tree.path("classes"), Set.class);
        Map<String, String> attributes = codec.treeToValue(tree.path("other"), Map.class);
        return new NodeBuilder()
                .setAttributes(attributes)
                .setTag(tag)
                .setIndex(index)
                .setId(id)
                .addContent(innerText)
                .setClasses(classes)
                .build();
    }

    /**
     * Finds new element locations on a target page for self-healing.
     *
     * @param by         The PageAwareBy object representing the element locator.
     * @param targetPage The target page where the element should be located.
     * @return List of Scored<By> objects representing the new element locations.
     */
    public List<Scored<By>> findNewLocations(PageAwareBy by, String targetPage) {
        List<Node> nodes = storage.getLastValidPath(by, by.getPageName());
        return nodes.isEmpty() ? Collections.emptyList() : findNearest(nodes.toArray(new Node[0]), targetPage).stream()
                .map(this::toLocator)
                .collect(Collectors.toList());
    }

    /**
     * Checks if healing is enabled based on configuration and annotations.
     *
     * @return True if healing is enabled, false otherwise.
     */
    public boolean isHealingEnabled() {
        boolean isDisabled = StackUtils.isAnnotationPresent(DisableHealing.class);
        return Boolean.parseBoolean(config.getProperty("heal-enabled")) && !isDisabled;
    }

    /**
     * Gets the screenshot path from the configuration.
     *
     * @return The screenshot path.
     */
    public String getScreenshotPath() {
        return config.getProperty("screenshotPath");
    }

    /**
     * Converts a Scored<Node> into a Scored<By> object.
     *
     * @param node The Scored<Node> object to convert.
     * @return The Scored<By> object.
     */
    private Scored<By> toLocator(Scored<Node> node) {
        Iterator<Set<SelectorComponent>> iterator = selectorDetailLevels.iterator();
        By locator;
        List<WebElement> elements;
        do {
            if (!iterator.hasNext()) {
                throw new HealException();
            }
            Set<SelectorComponent> detailLevel = iterator.next();
            locator = construct(node.getValue(), detailLevel);
            elements = webDriver.findElements(locator);
        } while (elements.size() != 1);

        return new Scored<>(node.getScore(), locator);
    }

    /**
     * Constructs a By locator based on the Node and selector detail level.
     *
     * @param node        The Node object representing the element.
     * @param detailLevel The selector detail level.
     * @return The By locator.
     */
    private By construct(Node node, Set<SelectorComponent> detailLevel) {
        return By.cssSelector(detailLevel.stream()
                .map(component -> component.createComponent(node))
                .collect(Collectors.joining()));
    }

    /**
     * Finds the nearest element path to a target tree.
     *
     * @param nodePath         The node path of the element.
     * @param destinationTree  The target tree to compare against.
     * @return List of Scored<Node> objects representing the nearest element path.
     */
    private List<Scored<Node>> findNearest(Node[] nodePath, String destinationTree) {
        Node destination = parseTree(destinationTree);
        PathFinder pathFinder = new PathFinder(new LCSPathDistance(), new HeuristicNodeDistance());
        return pathFinder.find(new Path(nodePath), destination, recoveryTries);
    }

    /**
     * Parses a tree represented as a string into a Node object.
     *
     * @param tree The tree string to parse.
     * @return The parsed Node object.
     */
    private Node parseTree(String tree) {
        return new JsoupHTMLParser().parse(new ByteArrayInputStream(tree.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Gets the WebDriver instance associated with this SelfHealingEngine.
     *
     * @return The WebDriver instance.
     */
    public WebDriver getWebDriver() {
        return webDriver;
    }
}
