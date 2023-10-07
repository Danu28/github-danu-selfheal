package com.epam.healenium;

import com.epam.healenium.handlers.proxy.SelfHealingProxyInvocationHandler;
import com.epam.healenium.utils.ConfigFactory;
import com.epam.healenium.utils.ProxyFactory;
import org.openqa.selenium.WebDriver;

import java.util.Properties;

public interface SelfHealingDriver extends WebDriver {

    /**
     * Get the current self-healing engine associated with this driver.
     *
     * @return The self-healing engine.
     */
    SelfHealingEngine getCurrentEngine();

    /**
     * Get the delegate WebDriver that this self-healing driver wraps.
     *
     * @param <T> The type of WebDriver.
     * @return The delegate WebDriver.
     */
    <T extends WebDriver> T getDelegate();

    /**
     * Create a self-healing driver with default configuration.
     *
     * @param delegate The delegate WebDriver.
     * @return A self-healing driver instance.
     */
    static SelfHealingDriver create(WebDriver delegate) {
        Properties config = ConfigFactory.getDefaultConfig();
        return create(delegate, config);
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
    static SelfHealingDriver create(SelfHealingEngine engine) {
        ClassLoader classLoader = SelfHealingDriver.class.getClassLoader();
        Class<? extends WebDriver> driverClass = engine.getWebDriver().getClass();
        SelfHealingProxyInvocationHandler handler = new SelfHealingProxyInvocationHandler(engine);
        return ProxyFactory.createDriverProxy(classLoader, handler, driverClass);
    }
}
