package Tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
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

    // ==================== STEPS ====================

    @Step("Cliquer sur le menu : {selector}")
    private void clicMenu(String selector) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement menu = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(selector)
                )
        );
        menu.click();
    }

    @Step("Vérifier que l'URL contient : {motCle}")
    private void verifierURL(String motCle) {
        String url = driver.getCurrentUrl();
        Assert.assertNotNull(url, "URL null");
        Assert.assertTrue(url.contains(motCle), "URL incorrecte : " + url);
    }

    // ==================== TESTS ====================

    @Test
    @Story("Navigation vers la section Femme")
    @Description("Vérifie que le clic sur le menu Femme redirige vers la page femme")
    @Severity(SeverityLevel.CRITICAL)
    public void testNavigationFemme() {
        clicMenu("a.menu-item[href*='femme']");
        verifierURL("femme");
    }

    @Test
    @Story("Navigation vers la section Homme")
    @Description("Vérifie que le clic sur le menu Homme redirige vers la page homme")
    @Severity(SeverityLevel.CRITICAL)
    public void testNavigationHomme() {
        clicMenu("a.menu-item[href*='homme']");
        verifierURL("homme");
    }

    @Test
    @Story("Navigation vers la section Enfants")
    @Description("Vérifie que le clic sur le menu Enfants redirige vers la page enfants")
    @Severity(SeverityLevel.CRITICAL)
    public void testNavigationEnfants() {
        clicMenu("a.menu-item[href*='enfants']");
        verifierURL("enfants");
    }

    @Test
    @Story("Navigation vers la section Beauté")
    @Description("Vérifie que le clic sur le menu Beauté redirige vers la page beauté")
    @Severity(SeverityLevel.NORMAL)
    public void testNavigationBeaute() {
        clicMenu("a.menu-item[href*='beaute']");
        verifierURL("beaute");
    }

    @Test
    @Story("Navigation vers la section Sport")
    @Description("Vérifie que le clic sur le menu Sport redirige vers la page sport")
    @Severity(SeverityLevel.NORMAL)
    public void testNavigationSport() {
        clicMenu("a.menu-item[href*='sport']");
        verifierURL("sport");
    }
}