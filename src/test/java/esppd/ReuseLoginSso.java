package esppd;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class ReuseLoginSso {
    protected WebDriver driver;

    @BeforeTest
    public void DoLoginSso() {
        System.setProperty("webdriver.chrome.driver",
                "C:/Program Files/Google/Chrome/Application/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.navigate().to("https://esppd.pln.co.id/");
        driver.findElement(By.xpath("//button[span[text()='Login via SSO']]")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement userIdInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("userId")));
        userIdInput.sendKeys("usersso2.travel");
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys("Sep09@2025");
        driver.findElement(By.cssSelector("button.ui.button.login-button.primary")).click();

        try {
            wait.until(ExpectedConditions.urlContains("/trips"));
            Assert.assertEquals(driver.getCurrentUrl(), "https://esppd.pln.co.id/trips");
            System.out.println("Login sukses");
        } catch (Exception e) {
            System.out.println("Login gagal");
        }
    }

    @AfterSuite
    public void closeBrowser() throws InterruptedException {
        Thread.sleep(3000);
        driver.quit();
    }
}
