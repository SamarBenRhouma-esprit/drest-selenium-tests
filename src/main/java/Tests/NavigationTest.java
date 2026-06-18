package Tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

@Epic("Drest.tn E-Commerce")
@Feature("Navigation")
public class NavigationTest extends BaseTest {

    @Test
    @Story("Navigation vers la section Femme")
    @Description("Vérifie que le clic sur le menu Femme redirige vers la page femme")
    @Severity(SeverityLevel.CRITICAL)
    public void testNavigationFemme() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement menuFemme = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a.menu-item[href*='femme']")
                )
        );
        menuFemme.click();
        String url = driver.getCurrentUrl();
        Assert.assertNotNull(url, "URL null");
        Assert.assertTrue(url.contains("femme"), "URL incorrecte : " + url);
    }

    @Test
    @Story("Navigation vers la section Homme")
    @Description("Vérifie que le clic sur le menu Homme redirige vers la page homme")
    @Severity(SeverityLevel.CRITICAL)
    public void testNavigationHomme() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement menuHomme = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a.menu-item[href*='homme']")
                )
        );
        menuHomme.click();
        String url = driver.getCurrentUrl();
        Assert.assertNotNull(url, "URL null");
        Assert.assertTrue(url.contains("homme"), "URL incorrecte : " + url);
    }

    @Test
    @Story("Navigation vers la section Enfants")
    @Description("Vérifie que le clic sur le menu Enfants redirige vers la page enfants")
    @Severity(SeverityLevel.CRITICAL)
    public void testNavigationEnfants() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement menuEnfants = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a.menu-item[href*='enfants']")
                )
        );
        menuEnfants.click();
        String url = driver.getCurrentUrl();
        Assert.assertNotNull(url, "URL null");
        Assert.assertTrue(url.contains("enfants"), "URL incorrecte : " + url);
    }

    @Test
    @Story("Navigation vers la section Beauté")
    @Description("Vérifie que le clic sur le menu Beauté redirige vers la page beauté")
    @Severity(SeverityLevel.NORMAL)
    public void testNavigationBeaute() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement menuBeaute = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a.menu-item[href*='beaute']")
                )
        );
        menuBeaute.click();
        String url = driver.getCurrentUrl();
        Assert.assertNotNull(url, "URL null");
        Assert.assertTrue(url.contains("beaute"), "URL incorrecte : " + url);
    }

    @Test
    @Story("Navigation vers la section Sport")
    @Description("Vérifie que le clic sur le menu Sport redirige vers la page sport")
    @Severity(SeverityLevel.NORMAL)
    public void testNavigationSport() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement menuSport = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a.menu-item[href*='sport']")
                )
        );
        menuSport.click();
        String url = driver.getCurrentUrl();
        Assert.assertNotNull(url, "URL null");
        Assert.assertTrue(url.contains("sport"), "URL incorrecte : " + url);
    }
}