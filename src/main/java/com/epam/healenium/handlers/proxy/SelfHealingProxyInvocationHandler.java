/**
 * SelfHealingProxyInvocationHandler is responsible for handling WebDriver method calls
 * and adding self-healing functionality.
 */
package com.epam.healenium.handlers.proxy;

import com.epam.healenium.SelfHealingEngine;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;

public class SelfHealingProxyInvocationHandler extends BaseHandler {
    private static final Logger log = LoggerFactory.getLogger(SelfHealingProxyInvocationHandler.class);

    /**
     * Constructor for SelfHealingProxyInvocationHandler.
     *
     * @param engine The SelfHealingEngine instance.
     */
    public SelfHealingProxyInvocationHandler(SelfHealingEngine engine) {
        super(engine);
    }

    /**
     * Handles method invocation on the WebDriver proxy.
     *
     * @param proxy  The proxy object.
     * @param method The method to invoke.
     * @param args   The arguments for the method.
     * @return The result of the method invocation.
     * @throws Throwable If an error occurs during method invocation.
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ClassLoader loader = this.driver.getClass().getClassLoader();
        switch (method.getName()) {
            case "findElement":
                WebElement element = this.findElement((By) args[0]);
                return Optional.ofNullable(element).map(it -> this.wrapElement(it, loader)).orElse(null);
            case "getCurrentEngine":
                return this.engine;
            case "getDelegate":
                return this.driver;
            case "switchTo":
                this.clearStash();
                WebDriver.TargetLocator switched = (WebDriver.TargetLocator) method.invoke(this.driver, args);
                return this.wrapTarget(switched, loader);
            default:
                return method.invoke(this.driver, args);
        }
    }
}
