package com.epam.healenium;

import com.epam.healenium.handlers.proxy.SelfHealingProxyInvocationHandler;
import com.epam.healenium.utils.ConfigFactory;
import com.epam.healenium.utils.ProxyFactory;
import org.openqa.selenium.WebDriver;

import java.util.Properties;

public interface SelfHealingDriver extends WebDriver {

    /**
     * Configures the WebDriver with the appropriate settings using ConfigFactory.
     */
    static void setup() {
        ConfigFactory.setConfig();
    }

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
        return SelfHealingEngine.create(delegate, config);
    }
}
