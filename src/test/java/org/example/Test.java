package org.example;

import com.epam.healenium.SelfHealingDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class Test {

    static WebDriver driver;

    public static void main(String[] args) {

        String url = "file:///C:/Users/KaDh550/Desktop/Sample.html";

        WebDriverManager.chromedriver().setup();
        WebDriver delete = new ChromeDriver();
        driver = SelfHealingDriver.create(delete);

        driver.manage().window().maximize();
        driver.get(url);

        driver.findElement(By.xpath("//*[@id=\"loginButton\"]")).click();

        driver.quit();
    }
}
