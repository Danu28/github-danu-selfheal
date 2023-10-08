package org.example;

import com.epam.healenium.SelfHealingDriver;
import com.epam.healenium.utils.ConfigFactory;
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

        By username = By.xpath("//input[@id='username']");
        By password = By.xpath("//input[@id='password']");
        By login = By.xpath("//*[@id=\"loginButton\"]");


        driver.findElement(username).sendKeys("hello");
        driver.findElement(password).sendKeys("hello");
        driver.findElement(login).click();


        driver.quit();
    }
}
