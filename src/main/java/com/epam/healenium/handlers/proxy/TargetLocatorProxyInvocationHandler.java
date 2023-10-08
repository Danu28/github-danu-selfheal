/**
 * TargetLocatorProxyInvocationHandler is responsible for handling WebDriver.TargetLocator method calls
 * and replacing the result with a SelfHealingDriver instance if needed.
 */
package com.epam.healenium.handlers.proxy;

import com.epam.healenium.SelfHealingDriver;
import com.epam.healenium.SelfHealingEngine;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.openqa.selenium.WebDriver;

class TargetLocatorProxyInvocationHandler implements InvocationHandler {
    private final WebDriver.TargetLocator delegate;
    private final SelfHealingEngine engine;

    /**
     * Handles method invocation on the WebDriver.TargetLocator proxy.
     *
     * @param proxy  The proxy object.
     * @param method The method to invoke.
     * @param args   The arguments for the method.
     * @return The result of the method invocation.
     * @throws Throwable If an error occurs during method invocation.
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(delegate, args);
        return (result instanceof WebDriver && !(result instanceof SelfHealingDriver))
                ? SelfHealingEngine.create(engine)
                : result;
    }

    /**
     * Constructor for TargetLocatorProxyInvocationHandler.
     *
     * @param delegate The original WebDriver.TargetLocator instance.
     * @param engine   The SelfHealingEngine instance.
     */
    public TargetLocatorProxyInvocationHandler(WebDriver.TargetLocator delegate, SelfHealingEngine engine) {
        this.delegate = delegate;
        this.engine = engine;
    }
}
