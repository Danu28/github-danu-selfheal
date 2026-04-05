package org.example;

import com.epam.healenium.SelfHealingDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class GoogleTest {

    static WebDriver driver;

    public static void main(String[] args) {

        try {
            String url = "https://www.google.com/";
            // Setup for Docker
            // Selenium Grid URL (Docker)
//            DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
//            desiredCapabilities.setBrowserName("chrome");
//            URL gridUrl = new URL("http://localhost:4444/wd/hub");
//
            // Create Remote WebDriver
//            RemoteWebDriver delete = new RemoteWebDriver(gridUrl, desiredCapabilities);

            WebDriverManager.chromedriver().setup();
            WebDriver delete = new ChromeDriver();

            SelfHealingDriver.setup();
            driver = SelfHealingDriver.create(delete);

            driver.manage().window().maximize();
            driver.get(url);

            Thread.sleep(2000);
            System.out.println("Title: " + driver.getTitle());

            By searchBox = By.xpath("//textarea[@title='Search']");
            By googleLogo = By.cssSelector("svg[aria-label='Google']");

            driver.findElement(searchBox).sendKeys("hello");
            driver.findElement(googleLogo).click();

            // js injection to update html, username, password and login btn attribute to change
            JavascriptExecutor js = (JavascriptExecutor) driver;

            js.executeScript("document.querySelector('textarea[title=\"Search\"]').setAttribute('title','Search_New');");

            js.executeScript("document.querySelector('svg[aria-label=\"Google\"]').setAttribute('aria-label','Google_New');");

            System.out.println("DOM updated successfully!");
            Thread.sleep(10000);

            driver.findElement(searchBox).clear();
            driver.findElement(searchBox).sendKeys("hello again");
            Thread.sleep(1000);
            driver.findElement(googleLogo).click();
            Thread.sleep(1000);
        } catch (RuntimeException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            driver.quit();
        }

    }
}
