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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                ChromeOptions options = new ChromeOptions();

                // === Anti-détection reCAPTCHA / bot detection ===
                options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
                options.setExperimentalOption("useAutomationExtension", false);
                options.addArguments("--disable-blink-features=AutomationControlled");
                options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

                // === Options standard ===
                options.addArguments("--start-maximized");
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-infobars");
                options.addArguments("--disable-popup-blocking");
                options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

                // === Mode headless (optionnel via -Dheadless=true) ===
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

                // === Masquer navigator.webdriver SUR CHAQUE nouvelle page (CDP) ===
                // Injection avant tout script de la page → reCAPTCHA voit un navigateur "propre"
                Map<String, Object> params = new HashMap<>();
                params.put("source",
                        "Object.defineProperty(navigator, 'webdriver', {get: () => undefined});" +
                                "window.chrome = { runtime: {} };" +
                                "Object.defineProperty(navigator, 'plugins', {get: () => [1, 2, 3, 4, 5]});" +
                                "Object.defineProperty(navigator, 'languages', {get: () => ['fr-FR', 'fr', 'en-US', 'en']});"
                );
                ((ChromeDriver) driver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", params);

                Thread.sleep(2000);
                driver.get("https://www.drest.tn");

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
                wait.until(d -> "complete".equals(
                        ((JavascriptExecutor) d).executeScript("return document.readyState")
                ));
                Thread.sleep(3000);

                return; // Setup réussi, on sort

            } catch (Exception e) {
                System.out.println("SetUp attempt " + attempt + "/" + maxAttempts + " failed: " + e.getMessage());

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

        // Si 3 tentatives échouent, on throw une exception CLAIRE
        // → statut "broken" (jaune) dans Allure avec un message explicite
        // → PAS de NullPointerException dans le corps du test
        throw new RuntimeException(
                "Impossible d'initialiser le driver après " + maxAttempts + " tentatives. " +
                        "Cause probable : CAPTCHA, réseau, ou site indisponible."
        );
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