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
@Feature("Page d'accueil")
public class HomePageTest extends BaseTest {

    // ==================== STEPS ====================

    @Step("Récupérer le titre de la page")
    private String recupererTitrePage() {
        return driver.getTitle();
    }

    @Step("Vérifier que le titre contient 'Drest'")
    private void verifierTitre(String titre) {
        Assert.assertNotNull(titre, "Titre null");
        Assert.assertTrue(titre.contains("Drest"), "Titre incorrect : " + titre);
    }

    @Step("Vérifier la visibilité du logo")
    private void verifierLogo() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement logo = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("a.home-btn")
                )
        );
        Assert.assertTrue(logo.isDisplayed(), "Logo non visible");
    }

    @Step("Vérifier la visibilité du menu principal")
    private void verifierMenuPrincipal() {
        WebElement menuFemme   = driver.findElement(By.linkText("Femme"));
        WebElement menuHomme   = driver.findElement(By.linkText("Homme"));
        WebElement menuEnfants = driver.findElement(By.linkText("Enfants"));
        Assert.assertTrue(menuFemme.isDisplayed(),   "Menu Femme non visible");
        Assert.assertTrue(menuHomme.isDisplayed(),   "Menu Homme non visible");
        Assert.assertTrue(menuEnfants.isDisplayed(), "Menu Enfants non visible");
    }

    @Step("Récupérer et vérifier l'URL de la page")
    private void verifierURL() {
        String url = driver.getCurrentUrl();
        Assert.assertNotNull(url, "URL null");
        Assert.assertTrue(url.contains("drest.tn"), "URL incorrecte : " + url);
    }

    // ==================== TESTS ====================

    @Test
    @Story("Titre de la page")
    @Description("Vérifie que le titre de la page d'accueil contient 'Drest'")
    @Severity(SeverityLevel.NORMAL)
    public void testTitrePage() {
        String titre = recupererTitrePage();
        verifierTitre(titre);
    }

    @Test
    @Story("Logo Drest")
    @Description("Vérifie que le logo Drest est visible sur la page d'accueil")
    @Severity(SeverityLevel.CRITICAL)
    public void testLogoPresent() {
        verifierLogo();
    }

    @Test
    @Story("Menu principal")
    @Description("Vérifie que les menus principaux Femme, Homme et Enfants sont visibles")
    @Severity(SeverityLevel.CRITICAL)
    public void testMenuPrincipalPresent() {
        verifierMenuPrincipal();
    }

    @Test
    @Story("URL de la page d'accueil")
    @Description("Vérifie que l'URL de la page d'accueil contient 'drest.tn'")
    @Severity(SeverityLevel.NORMAL)
    public void testURLCorrecte() {
        verifierURL();
    }
}