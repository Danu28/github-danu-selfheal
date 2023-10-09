//////
////// Source code recreated from a .class file by IntelliJ IDEA
////// (powered by FernFlower decompiler)
//////
////
////package com.epam.healenium.handlers.proxy;
////
////import com.epam.healenium.PageAwareBy;
////import com.epam.healenium.SelfHealingEngine;
////import com.epam.healenium.data.LocatorInfo;
////import com.epam.healenium.treecomparing.Scored;
////import com.epam.healenium.utils.ProxyFactory;
////import com.epam.healenium.utils.StackUtils;
////import com.google.common.cache.CacheBuilder;
////import com.google.common.cache.CacheLoader;
////import com.google.common.cache.LoadingCache;
////import org.jetbrains.annotations.NotNull;
////import org.openqa.selenium.*;
////import org.openqa.selenium.io.FileHandler;
////import org.openqa.selenium.remote.Augmenter;
////import org.slf4j.Logger;
////import org.slf4j.LoggerFactory;
////
////import java.io.File;
////import java.io.IOException;
////import java.lang.reflect.InvocationHandler;
////import java.nio.file.Files;
////import java.nio.file.OpenOption;
////import java.nio.file.StandardOpenOption;
////import java.time.LocalDateTime;
////import java.time.format.DateTimeFormatter;
////import java.util.List;
////import java.util.Locale;
////import java.util.Optional;
////import java.util.concurrent.TimeUnit;
////
////public abstract class BaseHandler implements InvocationHandler {
////    private static final Logger log = LoggerFactory.getLogger(BaseHandler.class);
////    private final LoadingCache<PageAwareBy, WebElement> stash;
////    protected final SelfHealingEngine engine;
////    protected final WebDriver driver;
////    private final LocatorInfo info = new LocatorInfo();
////
////    public BaseHandler(SelfHealingEngine engine) {
////        this.engine = engine;
////        this.driver = engine.getWebDriver();
////        this.stash = CacheBuilder.newBuilder().maximumSize(300L).expireAfterWrite(10L, TimeUnit.SECONDS).build(new CacheLoader<PageAwareBy, WebElement>() {
////            public WebElement load(@NotNull PageAwareBy key) {
////                return BaseHandler.this.lookUp(key);
////            }
////        });
////    }
////
////    protected WebElement findElement(By by) {
////        try {
////            PageAwareBy pageBy = this.awareBy(by);
////            By inner = pageBy.getBy();
////            return this.engine.isHealingEnabled() ? (WebElement)this.stash.get(pageBy) : this.driver.findElement(inner);
////        } catch (Exception var4) {
////            throw new NoSuchElementException("Failed to find element using " + by.toString(), var4);
////        }
////    }
////
////    protected WebElement lookUp(PageAwareBy key) {
////        try {
////            WebElement element = this.driver.findElement(key.getBy());
////            this.engine.savePath(key, element);
////            return element;
////        } catch (NoSuchElementException var3) {
////            log.warn("Failed to find an element using locator {}\nReason: {}\nTrying to heal...", key.getBy().toString(), var3.getMessage());
////            return (WebElement)this.heal(key, var3).orElseThrow(() -> {
////                return var3;
////            });
////        }
////    }
////
////    protected Optional<WebElement> heal(PageAwareBy pageBy, NoSuchElementException e) {
////        LocatorInfo.Entry entry = this.reportBasicInfo(pageBy, e);
////        return this.healLocator(pageBy).map((healed) -> {
////            this.reportFailedInfo(pageBy, entry, healed);
////            this.engine.saveLocator(this.info);
////            return this.driver.findElement(healed);
////        });
////    }
////
////    private void reportFailedInfo(PageAwareBy by, LocatorInfo.Entry infoEntry, By healed) {
////        String failedByValue = by.getBy().toString();
////        int splitIndex = failedByValue.indexOf(58);
////        infoEntry.setFailedLocatorType(failedByValue.substring(0, splitIndex).trim());
////        ++splitIndex;
////        infoEntry.setFailedLocatorValue(failedByValue.substring(splitIndex).trim());
////        String healedByValue = healed.toString();
////        splitIndex = healedByValue.indexOf(58);
////        infoEntry.setHealedLocatorType(healedByValue.substring(0, splitIndex).trim());
////        ++splitIndex;
////        infoEntry.setHealedLocatorValue(healedByValue.substring(splitIndex).trim());
////        infoEntry.setScreenShotPath(this.captureScreen(healed));
////        int pos = this.info.getElementsInfo().indexOf(infoEntry);
////        if (pos != -1) {
////            this.info.getElementsInfo().set(pos, infoEntry);
////        } else {
////            this.info.getElementsInfo().add(infoEntry);
////        }
////
////    }
////
////    private LocatorInfo.Entry reportBasicInfo(PageAwareBy pageBy, NoSuchElementException e) {
////        String targetClass = pageBy.getPageName();
////        Optional<StackTraceElement> elOpt = StackUtils.getElementByClass(e.getStackTrace(), targetClass);
////        return (LocatorInfo.Entry) elOpt.map((el) -> {
////            LocatorInfo.PageAsClassEntry entry = new LocatorInfo.PageAsClassEntry();
////            entry.setFileName(el.getFileName());
////            entry.setLineNumber(el.getLineNumber());
////            entry.setMethodName(el.getMethodName());
////            entry.setDeclaringClass(el.getClassName());
////            return (LocatorInfo.Entry) entry; // Explicitly cast to LocatorInfo.Entry
////        }).orElseGet(() -> {
////            LocatorInfo.SimplePageEntry entry = new LocatorInfo.SimplePageEntry();
////            entry.setPageName(pageBy.getPageName());
////            return (LocatorInfo.Entry) entry; // Explicitly cast to LocatorInfo.Entry
////        });
////    }
////
////
////    private Optional<By> healLocator(PageAwareBy pageBy) {
////        log.debug("* healLocator start: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
////        List<Scored<By>> choices = this.engine.findNewLocations(pageBy, this.pageSource());
////        Optional<Scored<By>> result = choices.stream().findFirst();
////        result.ifPresent((primary) -> {
////            log.warn("Using healed locator: {}", primary.toString());
////        });
////        choices.stream().skip(1L).forEach((otherChoice) -> {
////            log.warn("Other choice: {}", otherChoice.toString());
////        });
////        if (!result.isPresent()) {
////            log.warn("New element locators have not been found");
////        }
////
////        log.debug("* healLocator finish: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
////        return result.map(Scored::getValue);
////    }
////
////    private String captureScreen(By by) {
////        WebElement element = this.findElement(by);
////
////        String path;
////        try {
////            JavascriptExecutor jse = (JavascriptExecutor)this.driver;
////            jse.executeScript("arguments[0].style.border='3px solid red'", new Object[]{element});
////            WebDriver augmentedDriver = (new Augmenter()).augment(this.driver);
////            byte[] source = (byte[])((TakesScreenshot)augmentedDriver).getScreenshotAs(OutputType.BYTES);
////            FileHandler.createDir(new File(this.engine.getScreenshotPath()));
////            File file = new File(this.engine.getScreenshotPath() + "screenshot_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy-hh-mm-ss").withLocale(Locale.US)) + ".png");
////            Files.write(file.toPath(), source, new OpenOption[]{StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE});
////            path = file.getPath().replaceAll("\\\\", "/");
////            path = ".." + path.substring(path.indexOf("/sc"));
////        } catch (IOException var8) {
////            path = "Failed to capture screenshot: " + var8.getMessage();
////        }
////
////        return path;
////    }
////
////    private String pageSource() {
////        return this.driver instanceof JavascriptExecutor ? ((JavascriptExecutor)this.driver).executeScript("return document.body.outerHTML;", new Object[0]).toString() : this.driver.getPageSource();
////    }
////
////    protected PageAwareBy awareBy(By by) {
////        return by instanceof PageAwareBy ? (PageAwareBy)by : PageAwareBy.by(this.driver.getTitle(), by);
////    }
////
////    protected void clearStash() {
////        this.stash.invalidateAll();
////    }
////
////    protected WebElement wrapElement(WebElement element, ClassLoader loader) {
////        WebElementProxyHandler elementProxyHandler = new WebElementProxyHandler(element, this.engine);
////        return ProxyFactory.createWebElementProxy(loader, elementProxyHandler);
////    }
////
////    protected WebDriver.TargetLocator wrapTarget(WebDriver.TargetLocator locator, ClassLoader loader) {
////        TargetLocatorProxyInvocationHandler handler = new TargetLocatorProxyInvocationHandler(locator, this.engine);
////        return ProxyFactory.createTargetLocatorProxy(loader, handler);
////    }
////}
//package com.epam.healenium.handlers.proxy;
//
//import com.epam.healenium.PageAwareBy;
//import com.epam.healenium.SelfHealingEngine;
//import com.epam.healenium.data.LocatorInfo;
//import com.epam.healenium.treecomparing.Scored;
//import com.epam.healenium.utils.ProxyFactory;
//import com.epam.healenium.utils.StackUtils;
//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.CacheLoader;
//import com.google.common.cache.LoadingCache;
//import org.jetbrains.annotations.NotNull;
//import org.openqa.selenium.*;
//import org.openqa.selenium.io.FileHandler;
//import org.openqa.selenium.remote.Augmenter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.io.IOException;
//import java.lang.reflect.InvocationHandler;
//import java.nio.file.Files;
//import java.nio.file.OpenOption;
//import java.nio.file.StandardOpenOption;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Locale;
//import java.util.Optional;
//import java.util.concurrent.TimeUnit;
//
//public abstract class BaseHandler implements InvocationHandler {
//    private static final Logger log = LoggerFactory.getLogger(BaseHandler.class);
//    private final LoadingCache<PageAwareBy, WebElement> stash;
//    protected final SelfHealingEngine engine;
//    protected final WebDriver driver;
//    private final LocatorInfo info = new LocatorInfo();
//
//    public BaseHandler(SelfHealingEngine engine) {
//        this.engine = engine;
//        this.driver = engine.getWebDriver();
//        this.stash = CacheBuilder.newBuilder()
//                .maximumSize(300L)
//                .expireAfterWrite(10L, TimeUnit.SECONDS)
//                .build(new CacheLoader<PageAwareBy, WebElement>() {
//                    public WebElement load(@NotNull PageAwareBy key) {
//                        return BaseHandler.this.lookUp(key);
//                    }
//                });
//    }
//
//    protected WebElement findElement(By by) {
//        try {
//            PageAwareBy pageBy = this.awareBy(by);
//            By inner = pageBy.getBy();
//            return this.engine.isHealingEnabled() ? this.stash.get(pageBy) : this.driver.findElement(inner);
//        } catch (Exception e) {
//            throw new NoSuchElementException("Failed to find element using " + by.toString(), e);
//        }
//    }
//
//    protected WebElement lookUp(PageAwareBy key) {
//        try {
//            WebElement element = this.driver.findElement(key.getBy());
//            this.engine.savePath(key, element);
//            return element;
//        } catch (NoSuchElementException e) {
//            log.warn("Failed to find an element using locator {}\nReason: {}\nTrying to heal...", key.getBy().toString(), e.getMessage());
//            return this.heal(key, e).orElseThrow(() -> e);
//        }
//    }
//
//    protected Optional<WebElement> heal(PageAwareBy pageBy, NoSuchElementException e) {
//        LocatorInfo.Entry entry = this.reportBasicInfo(pageBy, e);
//        return this.healLocator(pageBy).map(healed -> {
//            this.reportFailedInfo(pageBy, entry, healed);
//            this.engine.saveLocator(this.info);
//            return this.driver.findElement(healed);
//        });
//    }
//
//    private void reportFailedInfo(PageAwareBy by, LocatorInfo.Entry infoEntry, By healed) {
//        String failedByValue = by.getBy().toString();
//        int splitIndex = failedByValue.indexOf(":");
//        infoEntry.setFailedLocatorType(failedByValue.substring(0, splitIndex).trim());
//        splitIndex++;
//        infoEntry.setFailedLocatorValue(failedByValue.substring(splitIndex).trim());
//        String healedByValue = healed.toString();
//        splitIndex = healedByValue.indexOf(":");
//        infoEntry.setHealedLocatorType(healedByValue.substring(0, splitIndex).trim());
//        splitIndex++;
//        infoEntry.setHealedLocatorValue(healedByValue.substring(splitIndex).trim());
//        infoEntry.setScreenShotPath(this.captureScreen(healed));
//        int pos = this.info.getElementsInfo().indexOf(infoEntry);
//        if (pos != -1) {
//            this.info.getElementsInfo().set(pos, infoEntry);
//        } else {
//            this.info.getElementsInfo().add(infoEntry);
//        }
//    }
//
//        private LocatorInfo.Entry reportBasicInfo(PageAwareBy pageBy, NoSuchElementException e) {
//        String targetClass = pageBy.getPageName();
//        Optional<StackTraceElement> elOpt = StackUtils.getElementByClass(e.getStackTrace(), targetClass);
//        return (LocatorInfo.Entry) elOpt.map((el) -> {
//            LocatorInfo.PageAsClassEntry entry = new LocatorInfo.PageAsClassEntry();
//            entry.setFileName(el.getFileName());
//            entry.setLineNumber(el.getLineNumber());
//            entry.setMethodName(el.getMethodName());
//            entry.setDeclaringClass(el.getClassName());
//            return (LocatorInfo.Entry) entry; // Explicitly cast to LocatorInfo.Entry
//        }).orElseGet(() -> {
//            LocatorInfo.SimplePageEntry entry = new LocatorInfo.SimplePageEntry();
//            entry.setPageName(pageBy.getPageName());
//            return (LocatorInfo.Entry) entry; // Explicitly cast to LocatorInfo.Entry
//        });
//    }
//
//    private Optional<By> healLocator(PageAwareBy pageBy) {
//        log.debug("* healLocator start: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
//        List<Scored<By>> choices = this.engine.findNewLocations(pageBy, this.pageSource());
//        Optional<Scored<By>> result = choices.stream().findFirst();
//        result.ifPresent(primary -> log.warn("Using healed locator: {}", primary.toString()));
//        choices.stream().skip(1).forEach(otherChoice -> log.warn("Other choice: {}", otherChoice.toString()));
//        if (!result.isPresent()) {
//            log.warn("New element locators have not been found");
//        }
//        log.debug("* healLocator finish: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
//        return result.map(Scored::getValue);
//    }
//
//    private String captureScreen(By by) {
//        WebElement element = this.findElement(by);
//        String path;
//        try {
//            JavascriptExecutor jse = (JavascriptExecutor)this.driver;
//            jse.executeScript("arguments[0].style.border='3px solid red'", element);
//            WebDriver augmentedDriver = new Augmenter().augment(this.driver);
//            byte[] source = ((TakesScreenshot)augmentedDriver).getScreenshotAs(OutputType.BYTES);
//            FileHandler.createDir(new File(this.engine.getScreenshotPath()));
//            File file = new File(this.engine.getScreenshotPath() + "screenshot_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy-hh-mm-ss").withLocale(Locale.US)) + ".png");
//            Files.write(file.toPath(), source, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
//            path = file.getPath().replaceAll("\\\\", "/");
//            path = ".." + path.substring(path.indexOf("/sc"));
//        } catch (IOException e) {
//            path = "Failed to capture screenshot: " + e.getMessage();
//        }
//        return path;
//    }
//
//    private String pageSource() {
//        return this.driver instanceof JavascriptExecutor ?
//                ((JavascriptExecutor)this.driver).executeScript("return document.body.outerHTML;").toString() :
//                this.driver.getPageSource();
//    }
//
//    protected PageAwareBy awareBy(By by) {
//        return by instanceof PageAwareBy ? (PageAwareBy)by : PageAwareBy.by(this.driver.getTitle(), by);
//    }
//
//    protected void clearStash() {
//        this.stash.invalidateAll();
//    }
//
//    protected WebElement wrapElement(WebElement element, ClassLoader loader) {
//        WebElementProxyHandler elementProxyHandler = new WebElementProxyHandler(element, this.engine);
//        return ProxyFactory.createWebElementProxy(loader, elementProxyHandler);
//    }
////    protected WebElement wrapElement(WebElement element, ClassLoader loader) {
////        WebElementProxyHandler elementProxyHandler = new WebElementProxyHandler(element, this.engine);
////        return ProxyFactory.createWebElementProxy(loader, (InvocationHandler) elementProxyHandler);
////    }
//
//    protected WebDriver.TargetLocator wrapTarget(WebDriver.TargetLocator locator, ClassLoader loader) {
//        TargetLocatorProxyInvocationHandler handler = new TargetLocatorProxyInvocationHandler(locator, this.engine);
//        return ProxyFactory.createTargetLocatorProxy(loader, handler);
//    }
//}
/**
 * BaseHandler is an abstract class for handling web elements with self-healing capabilities.
 */
package com.epam.healenium.handlers.proxy;

import com.epam.healenium.PageAwareBy;
import com.epam.healenium.SelfHealingEngine;
import com.epam.healenium.data.LocatorInfo;
import com.epam.healenium.treecomparing.Scored;
import com.epam.healenium.utils.ProxyFactory;
import com.epam.healenium.utils.StackUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.remote.Augmenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class BaseHandler implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(BaseHandler.class);
    private final LoadingCache<PageAwareBy, WebElement> stash;
    protected final SelfHealingEngine engine;
    protected final WebDriver driver;
    final LocatorInfo info = new LocatorInfo();

    /**
     * Constructor for BaseHandler.
     *
     * @param engine The SelfHealingEngine instance.
     */
    public BaseHandler(SelfHealingEngine engine) {
        this.engine = engine;
        this.driver = engine.getWebDriver();
        this.stash = CacheBuilder.newBuilder()
                .maximumSize(300L)
                .expireAfterWrite(10L, TimeUnit.SECONDS)
                .build(new CacheLoader<PageAwareBy, WebElement>() {
                    public @NotNull WebElement load(@NotNull PageAwareBy key) {
                        return BaseHandler.this.lookUp(key);
                    }
                });
    }

    /**
     * Find a web element using a given By locator.
     *
     * @param by The By locator to find the element.
     * @return The found WebElement.
     * @throws NoSuchElementException If the element is not found.
     */
    protected WebElement findElement(By by) {
        try {
            PageAwareBy pageBy = this.awareBy(by);
            By inner = pageBy.getBy();
            return this.engine.isHealingEnabled() ? this.stash.get(pageBy) : this.driver.findElement(inner);
        } catch (Exception e) {
            throw new NoSuchElementException("Failed to find element using " + by.toString(), e);
        }
    }

    // added
    protected List<WebElement> findElements(By by) {
        try {
            PageAwareBy pageBy = awareBy(by);
            By inner = pageBy.getBy();
            if (engine.isHealingEnabled()) {
                return lookUpAll(pageBy);
            }
            return driver.findElements(inner);
        } catch (Exception ex) {
            throw new NoSuchElementException("Failed to find elements using " + by.toString(), ex);
        }
    }

    /**
     * Look up a WebElement using a PageAwareBy locator.
     *
     * @param key The PageAwareBy locator.
     * @return The found WebElement.
     * @throws NoSuchElementException If the element is not found.
     */
    protected WebElement lookUp(PageAwareBy key) {
        try {
            WebElement element = this.driver.findElement(key.getBy());
            this.engine.savePath(key, element);
            return element;
        } catch (NoSuchElementException e) {
            log.warn("Failed to find an element using locator {}\nReason: {}\nTrying to heal...", key.getBy().toString(), e.getMessage());
            return heal(key, e).orElseThrow(() -> e);
        }
    }

    /**
     * Attempt to heal a NoSuchElementException by finding a new locator for the element.
     *
     * @param pageBy The PageAwareBy locator.
     * @param e      The NoSuchElementException that occurred.
     * @return An optional containing the healed WebElement if successful, empty otherwise.
     */
    protected Optional<WebElement> heal(PageAwareBy pageBy, NoSuchElementException e) {
        LocatorInfo.Entry entry = this.reportBasicInfo(pageBy, e);
        return this.healLocator(pageBy).map(healed -> {
            this.reportFailedInfo(pageBy, entry, healed);
            this.engine.saveLocator(this.info);
            return this.driver.findElement(healed);
        });
    }

    //added
    protected List<WebElement> healAll(PageAwareBy pageBy, NoSuchElementException e) {
        List<WebElement> healedElements = new ArrayList<>();
        Optional<List<By>> healedLocators = healLocators(pageBy);

        if (healedLocators.isPresent()) {
            LocatorInfo.Entry entry = this.reportBasicInfo(pageBy, e);
            for (By healed : healedLocators.get()) {
                reportFailedInfo(pageBy, entry, healed);
                engine.saveLocator(info);
                healedElements.addAll(driver.findElements(healed));
            }
        }

        return healedElements;
    }

    // added
    protected List<WebElement> lookUpAll(PageAwareBy key) {
        List<WebElement> elements = new ArrayList<>();
        try {
            List<WebElement> foundElements = this.driver.findElements(key.getBy());
            for (WebElement element : foundElements) {
                this.engine.savePath(key, element);
                elements.add(element);
            }
            if (elements.isEmpty())
                throw new NoSuchElementException("No elements found using locator " + key.getBy().toString());
            else
                return elements;
        } catch (NoSuchElementException e) {
            log.warn("Failed to find elements using locator {}\nReason: {}\nTrying to heal...", key.getBy().toString(), e.getMessage());
            return this.healAll(key, e);
        }
    }


    void reportFailedInfo(PageAwareBy by, LocatorInfo.Entry infoEntry, By healed) {
        String failedByValue = by.getBy().toString();
        int splitIndex = failedByValue.indexOf(":");
        infoEntry.setFailedLocatorType(failedByValue.substring(0, splitIndex).trim());
        splitIndex++;
        infoEntry.setFailedLocatorValue(failedByValue.substring(splitIndex).trim());
        String healedByValue = healed.toString();
        splitIndex = healedByValue.indexOf(":");
        infoEntry.setHealedLocatorType(healedByValue.substring(0, splitIndex).trim());
        splitIndex++;
        infoEntry.setHealedLocatorValue(healedByValue.substring(splitIndex).trim());
        infoEntry.setScreenShotPath(this.captureScreen(healed));
        int pos = this.info.getElementsInfo().indexOf(infoEntry);
        if (pos != -1) {
            this.info.getElementsInfo().set(pos, infoEntry);
        } else {
            this.info.getElementsInfo().add(infoEntry);
        }
    }

    LocatorInfo.Entry reportBasicInfo(PageAwareBy pageBy, NoSuchElementException e) {
        String targetClass = pageBy.getPageName();
        Optional<StackTraceElement> elOpt = StackUtils.getElementByClass(e.getStackTrace(), targetClass);
        return (LocatorInfo.Entry) elOpt.map((el) -> {
            LocatorInfo.PageAsClassEntry entry = new LocatorInfo.PageAsClassEntry();
            entry.setFileName(el.getFileName());
            entry.setLineNumber(el.getLineNumber());
            entry.setMethodName(el.getMethodName());
            entry.setDeclaringClass(el.getClassName());
            return (LocatorInfo.Entry) entry; // Explicitly cast to LocatorInfo.Entry
        }).orElseGet(() -> {
            LocatorInfo.SimplePageEntry entry = new LocatorInfo.SimplePageEntry();
            entry.setPageName(pageBy.getPageName());
            return (LocatorInfo.Entry) entry; // Explicitly cast to LocatorInfo.Entry
        });
    }

    private Optional<By> healLocator(PageAwareBy pageBy) {
        log.debug("* healLocator start: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        List<Scored<By>> choices = this.engine.findNewLocations(pageBy, this.pageSource());
        Optional<Scored<By>> result = choices.stream().findFirst();
        result.ifPresent(primary -> log.warn("Using healed locator: {}", primary.toString()));
        choices.stream().skip(1).forEach(otherChoice -> log.warn("Other choice: {}", otherChoice.toString()));
        if (!result.isPresent()) {
            log.warn("New element locators have not been found");
        }
        log.debug("* healLocator finish: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        return result.map(Scored::getValue);
    }

    //added
    private boolean isScoreMatch(Scored<By> scoredBy, double threshold) {
        double elementScore = scoredBy.getScore();
        return elementScore >= threshold;
    }

    //added
    protected Optional<List<By>> healLocators(PageAwareBy pageBy) {
        log.debug("* healLocators start: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        List<Scored<By>> choices = this.engine.findNewLocations(pageBy, this.pageSource());

        double scoreThreshold = engine.getMatchScore();
        List<Scored<By>> filteredChoices = choices.stream()
                .filter(scoredBy -> isScoreMatch(scoredBy, scoreThreshold))
                .collect(Collectors.toList());

        List<By> healedLocators = filteredChoices.stream()
                .map(Scored::getValue)
                .collect(Collectors.toList());

        // Reverse the list
        Collections.reverse(healedLocators);

        if (healedLocators.isEmpty()) {
            log.warn("No new element locators have been found");
            return Optional.empty();
        }

        log.debug("* healLocators finish: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        return Optional.of(healedLocators);
    }


    private static int imageCounter = 1;

    private String captureScreen(By by) {
        WebElement element = this.findElement(by);
        String path;
        try {
            JavascriptExecutor jse = (JavascriptExecutor) this.driver;
            jse.executeScript("arguments[0].style.border='3px solid red'", element);
            WebDriver augmentedDriver = new Augmenter().augment(this.driver);
            byte[] source = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.BYTES);
            FileHandler.createDir(new File(this.engine.getScreenshotPath()));
            File file = new File(this.engine.getScreenshotPath() + "screenshot_image_" + imageCounter + ".png");
            Files.write(file.toPath(), source, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            path = file.getPath().replaceAll("\\\\", "/");
            path = ".." + path.substring(path.indexOf("/sc"));

            imageCounter++;
        } catch (IOException e) {
            path = "Failed to capture screenshot: " + e.getMessage();
        }
        return path;
    }

    String pageSource() {
        return this.driver instanceof JavascriptExecutor ?
                ((JavascriptExecutor) this.driver).executeScript("return document.body.outerHTML;").toString() :
                this.driver.getPageSource();
    }

    protected PageAwareBy awareBy(By by) {
        return by instanceof PageAwareBy ? (PageAwareBy) by : PageAwareBy.by(this.driver.getTitle(), by);
    }

    protected void clearStash() {
        this.stash.invalidateAll();
    }

    protected WebElement wrapElement(WebElement element, ClassLoader loader) {
        WebElementProxyHandler elementProxyHandler = new WebElementProxyHandler(element, this.engine);
        return ProxyFactory.createWebElementProxy(loader, elementProxyHandler);
    }

    protected WebDriver.TargetLocator wrapTarget(WebDriver.TargetLocator locator, ClassLoader loader) {
        TargetLocatorProxyInvocationHandler handler = new TargetLocatorProxyInvocationHandler(locator, this.engine);
        return ProxyFactory.createTargetLocatorProxy(loader, handler);
    }
}
