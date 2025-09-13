package cura;

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

public class TestLoginPage {
    WebDriver driver;

    @BeforeTest
    public void init() {

        System.setProperty("webdriver.chrome.driver", "C:/Program Files/Google/Chrome/Application/chromedriver.exe");
        driver = new ChromeDriver();
        driver.navigate().to("https://katalon-demo-cura.herokuapp.com/profile.php#login");
        driver.manage().window().maximize();
    }

    @Test
    public void checkElement() {
        Assert.assertEquals(driver.findElement(By.cssSelector("section h2")).getText(),"Login");
        Assert.assertEquals(driver.findElement(By.cssSelector("section p")).getText(),"Please login to make appointment.");
        Assert.assertEquals(driver.findElement(By.id("txt-username")).getAttribute("placeholder"),"Username");
        Assert.assertEquals(driver.findElement(By.id("txt-password")).getAttribute("placeholder"),"Password");
    }

    @Test
    public void loginWithNullValues() {
        driver.findElement(By.id("btn-login")).click();
        Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\"login\"]/div/div/div[1]/p[2]")).getText(),"Login failed! Please ensure the username and password are valid.");
    }

    @Test
    public void loginWithWrongValues() {
        driver.findElement(By.id("txt-username")).sendKeys("John Doe");
        driver.findElement(By.id("txt-password")).sendKeys("Wrong Password");
        driver.findElement(By.id("btn-login")).click();
        //Assert.assertEquals(driver.findElement(By.cssSelector("section p")).getText(), "Login failed! Please ensure the username and password are valid.");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement errorMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("section p.text-danger"))
        );
        Assert.assertEquals(
                errorMsg.getText(),
                "Login failed! Please ensure the username and password are valid."
        );
    }

    @Test
    public void loginWithCorrectValues() {
        driver.findElement(By.id("txt-username")).sendKeys("John Doe");
        driver.findElement(By.id("txt-password")).sendKeys("ThisIsNotAPassword");
        driver.findElement(By.id("btn-login")).click();
        Assert.assertEquals(driver.getCurrentUrl(),"https://katalon-demo-cura.herokuapp.com/#appointment");
    }

    @AfterTest
    private void closeBrowser() throws InterruptedException {
        Thread.sleep(3000);
        driver.quit();
    }

}
