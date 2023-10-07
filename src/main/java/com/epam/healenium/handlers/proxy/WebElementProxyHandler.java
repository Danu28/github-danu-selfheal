/**
 * WebElementProxyHandler is responsible for handling WebElement method calls and potentially replacing
 * the WebElement with a wrapped SelfHealingWebElement if needed.
 */
package com.epam.healenium.handlers.proxy;

import com.epam.healenium.PageAwareBy;
import com.epam.healenium.SelfHealingEngine;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;

public class WebElementProxyHandler extends BaseHandler {
    private static final Logger log = LoggerFactory.getLogger(WebElementProxyHandler.class);
    private final WebElement delegate;

    /**
     * Constructor for WebElementProxyHandler.
     *
     * @param delegate The original WebElement instance.
     * @param engine   The SelfHealingEngine instance.
     */
    public WebElementProxyHandler(WebElement delegate, SelfHealingEngine engine) {
        super(engine);
        this.delegate = delegate;
    }

    /**
     * Handles method invocation on the WebElement proxy.
     *
     * @param proxy  The proxy object.
     * @param method The method to invoke.
     * @param args   The arguments for the method.
     * @return The result of the method invocation.
     * @throws Throwable If an error occurs during method invocation.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            ClassLoader loader = driver.getClass().getClassLoader();
            if ("findElement".equals(method.getName())) {
                WebElement element = findElement((By) args[0]);
                return Optional.ofNullable(element)
                        .map(it -> wrapElement(it, loader))
                        .orElse(null);
            } else if ("getWrappedElement".equals(method.getName())) {
                return delegate;
            } else {
                return method.invoke(delegate, args);
            }
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    /**
     * Finds the WebElement using the given By locator.
     *
     * @param by The By locator to use for finding the WebElement.
     * @return The found WebElement.
     * @throws NoSuchElementException If the WebElement cannot be found.
     */
    @Override
    protected WebElement findElement(By by) {
        try {
            PageAwareBy pageBy = awareBy(by);
            return engine.isHealingEnabled() ? lookUp(pageBy) : delegate.findElement(pageBy.getBy());
        } catch (Exception e) {
            throw new NoSuchElementException("Failed to find element using " + by.toString(), e);
        }
    }

    /**
     * Looks up the WebElement using the PageAwareBy locator and handles potential healing.
     *
     * @param key The PageAwareBy locator to use for looking up the WebElement.
     * @return The found or healed WebElement.
     * @throws NoSuchElementException If the WebElement cannot be found or healed.
     */
    @Override
    protected WebElement lookUp(PageAwareBy key) {
        try {
            WebElement element = delegate.findElement(key.getBy());
            engine.savePath(key, element);
            return element;
        } catch (NoSuchElementException e) {
            log.warn("Failed to find an element using locator {}\nReason: {}\nTrying to heal...",
                    key.getBy().toString(), e.getMessage());
            return heal(key, e).orElseThrow(() -> e);
        }
    }
}
