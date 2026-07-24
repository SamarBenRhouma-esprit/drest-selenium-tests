package Tests;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
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
    public void setUp() throws InterruptedException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL); // Changé de NONE à NORMAL

        // Mode headless pour CI/CD GitHub Actions
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-features=IsolateOrigins,site-per-process");
            options.addArguments("--disable-web-security");
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        }

        driver = new ChromeDriver(options);

        // Timeouts plus longs pour CI/CD
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(180));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(90));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

        // Laisser Chrome s'initialiser complètement en headless
        if (headless) {
            Thread.sleep(3000);
        }

        driver.get("https://www.drest.tn");

        // Attendre le chargement complet du DOM
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")
        ));

        // Attente supplémentaire pour les ressources annexes (Amazon S3, scripts JS)
        Thread.sleep(headless ? 8000 : 3000);

        // Scroll pour déclencher le lazy-loading des images
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight/2);");
        Thread.sleep(2000);
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
        Thread.sleep(2000);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE || result.getStatus() == ITestResult.SKIP) {
            try {
                capturerEcran("Echec - " + result.getName());
            } catch (Exception e) {
                // ignore
            }
        }
        if (driver != null) {
            driver.quit();
        }
    }

    @Attachment(value = "{name}", type = "image/png")
    public byte[] capturerEcran(String name) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}