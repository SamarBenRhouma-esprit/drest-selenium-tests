package Tests;

import io.qameta.allure.Attachment;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--start-maximized");
                options.addArguments("--disable-notifications");
                options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

                boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--window-size=1920,1080");
                    options.addArguments("--disable-gpu");
                }

                driver = new ChromeDriver(options);
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));
                driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(60));

                Thread.sleep(2000);
                driver.get("https://www.drest.tn");

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
                wait.until(d -> "complete".equals(
                        ((JavascriptExecutor) d).executeScript("return document.readyState")
                ));
                Thread.sleep(3000);

                return; // Succès, on sort

            } catch (Exception e) {
                System.out.println("SetUp attempt " + attempt + " failed: " + e.getMessage());

                if (driver != null) {
                    try {
                        driver.quit();
                    } catch (Exception ex) {
                        // ignore
                    }
                    driver = null;
                }

                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        // Si on arrive ici, les 3 tentatives ont échoué
        // On ne lance PAS d'exception pour éviter le "broken" dans Allure
        System.out.println("SetUp failed after 3 attempts, test will proceed but likely fail");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            try {
                capturerEcran("Echec - " + result.getName());
            } catch (Exception e) {
                // ignore
            }
        }
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    @Attachment(value = "{name}", type = "image/png")
    public byte[] capturerEcran(String name) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            return new byte[0];
        }
    }
}