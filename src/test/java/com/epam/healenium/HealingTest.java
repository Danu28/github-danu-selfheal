package com.epam.healenium;

import com.epam.healenium.annotation.DisableHealing;
import com.epam.healenium.data.LocatorInfo;
import com.epam.healenium.handlers.proxy.SelfHealingProxyInvocationHandler;
import com.epam.healenium.utils.ConfigFactory;
import com.epam.healenium.utils.ProxyFactory;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HealingTest {

    @Test
    void selfHealingEngineIsHealingEnabledDefaultTrue() {
        WebDriver mockDriver = mock(WebDriver.class);
        Properties props = new Properties();
        props.setProperty("heal-enabled", "true");
        props.setProperty("basePath", "heal-output/selenium");
        props.setProperty("reportPath", "heal-output/reports");
        props.setProperty("screenshotPath", "heal-output/screenshots/");
        props.setProperty("recovery-tries", "3");
        props.setProperty("match-score", ".75");
        props.setProperty("storage.mode", "file");
        SelfHealingEngine engine = new SelfHealingEngine(mockDriver, props);
        assertTrue(engine.isHealingEnabled());
    }

    @Test
    @DisableHealing
    public void disableHealingAnnotationDisablesHealing() {
        WebDriver mockDriver = mock(WebDriver.class);
        Properties props = new Properties();
        props.setProperty("heal-enabled", "true");
        props.setProperty("basePath", "heal-output/selenium");
        props.setProperty("reportPath", "heal-output/reports");
        props.setProperty("screenshotPath", "heal-output/screenshots/");
        props.setProperty("recovery-tries", "3");
        props.setProperty("match-score", ".75");
        SelfHealingEngine engine = new SelfHealingEngine(mockDriver, props);
        // annotated method should disable healing via StackUtils
        assertFalse(engine.isHealingEnabled());
    }

    @Test
    void proxyFactoryCreatesDriverProxy() {
        WebDriver mockDriver = mock(WebDriver.class, withSettings().extraInterfaces(org.openqa.selenium.JavascriptExecutor.class));
        Properties props = new Properties();
        props.setProperty("heal-enabled", "true");
        props.setProperty("basePath", "heal-output/selenium");
        props.setProperty("reportPath", "heal-output/reports");
        props.setProperty("screenshotPath", "heal-output/screenshots/");
        props.setProperty("recovery-tries", "3");
        props.setProperty("match-score", ".75");
        props.setProperty("storage.mode", "file");
        SelfHealingEngine engine = new SelfHealingEngine(mockDriver, props);
        SelfHealingProxyInvocationHandler handler = new SelfHealingProxyInvocationHandler(engine);
        var proxy = ProxyFactory.createDriverProxy(Thread.currentThread().getContextClassLoader(), handler, mockDriver.getClass());
        assertNotNull(proxy);
        assertInstanceOf(SelfHealingDriver.class, proxy);
    }

    @Test
    void locatorInfoEquality() {
        LocatorInfo.Entry e1 = new LocatorInfo.Entry();
        e1.setFailedLocatorValue("old");
        e1.setHealedLocatorValue("new");
        LocatorInfo.Entry e2 = new LocatorInfo.Entry();
        e2.setFailedLocatorValue("old");
        e2.setHealedLocatorValue("new");
        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void pageAwareByEquality() {
        By base = By.id("x");
        PageAwareBy a = PageAwareBy.by("PageA", base);
        PageAwareBy b = PageAwareBy.by("PageA", base);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
