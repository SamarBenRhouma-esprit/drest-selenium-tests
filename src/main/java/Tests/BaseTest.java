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
import java.util.HashMap;
import java.util.Map;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() throws InterruptedException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        if (headless) {
            // Configuration ANTI-CLOUDFLARE
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");

            // === CAMOUFLAGE ANTI-DÉTECTION ===
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-features=IsolateOrigins,site-per-process,BlockInsecurePrivateNetworkRequests");
            options.addArguments("--disable-web-security");
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--disable-infobars");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-plugins-discovery");
            options.addArguments("--no-first-run");
            options.addArguments("--no-default-browser-check");
            options.addArguments("--disable-default-apps");

            // User-agent MacOS Chrome (moins suspect que Windows)
            options.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

            // Langues acceptées (comme un vrai user)
            options.addArguments("--accept-lang=fr-FR,fr;q=0.9,en;q=0.8");

            // Désactiver les flags qui trahissent l'automatisation
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setExperimentalOption("useAutomationExtension", false);

            // Préférences pour un comportement plus humain
            Map<String, Object> prefs = new HashMap<>();
            prefs.put("credentials_enable_service", false);
            prefs.put("profile.password_manager_enabled", false);
            options.setExperimentalOption("prefs", prefs);
        }

        driver = new ChromeDriver(options);

        // Masquer webdriver dans navigator (Cloudflare check this)
        if (headless) {
            ((JavascriptExecutor) driver).executeScript(
                    "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
            );
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(180));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(90));

        if (headless) {
            Thread.sleep(3000);
        }

        // Aller sur Drest.tn
        driver.get("https://www.drest.tn");

        // Attendre le chargement de base
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")
        ));

        // === CLOUDFLARE CHALLENGE HANDLER ===
        if (headless) {
            // Attendre que Cloudflare valide le challenge JS (jusqu'à 30 secondes)
            for (int i = 0; i < 30; i++) {
                String title = driver.getTitle();
                String source = driver.getPageSource();

                // Si on est encore sur la page Cloudflare, on attend
                boolean stillCloudflare = title.contains("Just a moment")
                        || title.contains("Attention Required")
                        || source.contains("Performing security verification")
                        || source.contains("Verifying you are human")
                        || source.contains("cf-browser-verification")
                        || source.contains("challenge-platform");

                if (!stillCloudflare) {
                    System.out.println("Cloudflare passed after " + i + " seconds");
                    break;
                }

                Thread.sleep(1000);
            }

            // Attente supplémentaire pour que la vraie page se charge
            Thread.sleep(5000);
        }

        // Scroll pour déclencher lazy-loading
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight/2);");
            Thread.sleep(2000);
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
            Thread.sleep(2000);
        } catch (Exception e) {
            // ignore
        }
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