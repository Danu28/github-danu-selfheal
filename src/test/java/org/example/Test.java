package org.example;

import com.epam.healenium.SelfHealingDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Properties;

public class Test {

    static WebDriver driver;

    public static void main(String[] args) {

        File htmlFile = new File("src/test/resources/Sample.html");

        String url = null;
        try {
            url = htmlFile.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        WebDriverManager.chromedriver().setup();
        WebDriver delete = new ChromeDriver();

        SelfHealingDriver.setup();
        driver = SelfHealingDriver.create(delete);

        driver.manage().window().maximize();
        driver.get(url);

        By locator = By.xpath("//*[@id=\"loginButton\"]");

        WebElement element = driver.findElement(locator);
        element.click();
        driver.quit();
    }
}
