package cura;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class TestSubmit {
    WebDriver driver;

    @BeforeTest
    public void init() {
        System.setProperty("webdriver.chrome.driver", "C:/Program Files/Google/Chrome/Application/chromedriver.exe");
        driver = new ChromeDriver();
        driver.navigate().to("https://katalon-demo-cura.herokuapp.com/profile.php#login");
        driver.manage().window().maximize();
        //login
        driver.findElement(By.id("txt-username")).sendKeys("John Doe");
        driver.findElement(By.id("txt-password")).sendKeys("ThisIsNotAPassword");
        driver.findElement(By.id("btn-login")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        wait.until(ExpectedConditions.urlToBe("https://katalon-demo-cura.herokuapp.com/#appointment"));
        Assert.assertEquals(driver.getCurrentUrl(),"https://katalon-demo-cura.herokuapp.com/#appointment");
    }

    @Test (priority = 0)
    public void checkElement() {
        Assert.assertEquals(driver.findElement(By.cssSelector("section h2")).getText(),"Make Appointment");
        //for dropdown
        Select dropdownFacility = new Select(driver.findElement(By.id("combo_facility")));
        List<WebElement> dropdownOptions = dropdownFacility.getOptions();
        Assert.assertEquals(dropdownOptions.get(0).getAttribute("value"),"Tokyo CURA Healthcare Center");
        Assert.assertEquals(dropdownOptions.get(1).getAttribute("value"),"Hongkong CURA Healthcare Center");
        Assert.assertEquals(dropdownOptions.get(2).getAttribute("value"),"Seoul CURA Healthcare Center");

        //text area
        Assert.assertEquals(driver.findElement(By.id("txt_comment")).getAttribute("placeholder"), "Comment");
    }

    @Test (priority = 1)
    public void submitWithCorrectValues() {
        Select dropdownFacility = new Select(driver.findElement(By.id("combo_facility")));
        dropdownFacility.selectByValue("Seoul CURA Healthcare Center");
        WebElement checkbox = driver.findElement(By.id("chk_hospotal_readmission"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
        ((JavascriptExecutor) driver).executeScript("document.getElementById('radio_program_medicaid').click();");
        WebElement dateInput = driver.findElement(By.id("txt_visit_date"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='09/09/2025';", dateInput);
        WebElement commentBox = driver.findElement(By.id("txt_comment"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='Nama Pasein Tomy Sutedjo';", commentBox);
        WebElement bookButton = driver.findElement(By.id("btn-book-appointment"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", bookButton);
        Assert.assertEquals(driver.getCurrentUrl(),"https://katalon-demo-cura.herokuapp.com/appointment.php#summary");
    }

    @Test (priority = 2, dependsOnMethods = {"submitWithCorrectValues"})
    public void checkAppointmentSummary() {
        Assert.assertEquals(driver.findElement(By.cssSelector("section h2")).getText(),"Appointment Confirmation");
        Assert.assertEquals(driver.findElement(By.cssSelector("section p")).getText(),"Please be informed that your appointment has been booked as following:");
        Assert.assertEquals(driver.findElement(By.id("facility")).getText(),"Seoul CURA Healthcare Center");
        Assert.assertEquals(driver.findElement(By.id("hospital_readmission")).getText(),"Yes");
        Assert.assertEquals(driver.findElement(By.id("program")).getText(),"Medicaid");
        Assert.assertEquals(driver.findElement(By.id("visit_date")).getText(),"09/09/2025");
        Assert.assertEquals(driver.findElement(By.id("comment")).getText(),"Nama Pasein Tomy Sutedjo");
        WebElement btnHome = driver.findElement(By.linkText("Go to Homepage"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnHome);
        Assert.assertEquals(driver.getCurrentUrl(),"https://katalon-demo-cura.herokuapp.com/");
    }

    @AfterTest
    private void closeBrowser() throws InterruptedException {
        Thread.sleep(3000);
        driver.quit();
    }
}
