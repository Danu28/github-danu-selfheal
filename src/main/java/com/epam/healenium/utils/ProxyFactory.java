package com.epam.healenium.utils;

import com.epam.healenium.SelfHealingDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.interactions.Locatable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Utility class for creating dynamic proxies.
 */
public final class ProxyFactory {
    /**
     * Creates a proxy for a WebDriver class.
     *
     * @param loader  The ClassLoader to use.
     * @param handler The InvocationHandler to handle method calls.
     * @param clazz   The WebDriver class to create a proxy for.
     * @param <T>     The type of the WebDriver.
     * @return A proxy instance of the WebDriver.
     */
    public static <T extends WebDriver> SelfHealingDriver createDriverProxy(ClassLoader loader, InvocationHandler handler, Class<T> clazz) {
        Class<?>[] interfaces = Stream.concat(Arrays.stream(clazz.getInterfaces()), Stream.of(JavascriptExecutor.class, SelfHealingDriver.class, Interactive.class, Interactive.class))
                .distinct()
                .toArray(Class<?>[]::new);
        return (SelfHealingDriver) Proxy.newProxyInstance(loader, interfaces, handler);
    }

    /**
     * Creates a proxy for a WebElement.
     *
     * @param loader  The ClassLoader to use.
     * @param handler The InvocationHandler to handle method calls.
     * @param <T>     The type of the WebElement.
     * @return A proxy instance of the WebElement.
     */
    public static <T extends WebElement> T createWebElementProxy(ClassLoader loader, InvocationHandler handler) {
        Class<?>[] interfaces = new Class[]{WebElement.class, WrapsElement.class, Locatable.class};
        return (T) Proxy.newProxyInstance(loader, interfaces, handler);
    }

    /**
     * Creates a proxy for a TargetLocator.
     *
     * @param loader  The ClassLoader to use.
     * @param handler The InvocationHandler to handle method calls.
     * @param <T>     The type of the TargetLocator.
     * @return A proxy instance of the TargetLocator.
     */
    public static <T extends WebDriver.TargetLocator> T createTargetLocatorProxy(ClassLoader loader, InvocationHandler handler) {
        Class<?>[] interfaces = new Class[]{WebDriver.TargetLocator.class};
        return (T) Proxy.newProxyInstance(loader, interfaces, handler);
    }

    private ProxyFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
