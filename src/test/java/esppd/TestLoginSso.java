package esppd;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;

public class TestLoginSso {
    WebDriver driver;

    @BeforeTest
    private void initLoginSso() {
        System.setProperty("webdriver.chrome.driver", "C:/Program Files/Google/Chrome/Application/chromedriver.exe");
        driver = new ChromeDriver();
        driver.navigate().to("http://10.99.248.60:31300/");
        driver.manage().window().maximize();
        driver.findElement(By.xpath("//button[span[text()='Login via SSO']]")).click();
    }

    @Test
    public void loginWithCorrectValues() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement userIdInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("userId")));
        userIdInput.sendKeys("usersso2.travel");
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys("Sep09@2025");
        driver.findElement(By.cssSelector("button.ui.button.login-button.primary")).click();
        wait.until(ExpectedConditions.urlContains("/trips"));
        Assert.assertEquals(driver.getCurrentUrl(), "http://10.99.248.60:31300/trips");
    }

    @AfterTest
    private void closeBrowser() throws InterruptedException {
        Thread.sleep(3000);
        driver.quit();
    }
}
