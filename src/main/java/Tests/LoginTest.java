package Tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

@Epic("Drest.tn E-Commerce")
@Feature("Authentification")
public class LoginTest extends BaseTest {

    private static final String URL_LOGIN      = "https://www.drest.tn/mon-compte/";
    private static final String EMAIL_VALIDE   = "benrhouma.samar44@gmail.com";
    private static final String MDP_VALIDE     = "@Samar123";
    private static final String EMAIL_INVALIDE = "faux@inexistant.tn";
    private static final String MDP_INVALIDE   = "mauvaismdp123";

    // ==================== STEPS ====================

    @Step("Ouvrir la page de connexion")
    private void ouvrirPageLogin() throws InterruptedException {
        driver.get(URL_LOGIN);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
        Thread.sleep(1000);
    }

    @Step("Saisir l'email : {email}")
    private void saisirEmail(String email) {
        driver.findElement(By.id("username")).sendKeys(email);
    }

    @Step("Saisir le mot de passe")
    private void saisirMotDePasse(String mdp) {
        driver.findElement(By.id("password")).sendKeys(mdp);
    }

    @Step("Cliquer sur le bouton Se connecter")
    private void clicConnexion() {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", driver.findElement(By.name("login"))
        );
    }

    @Step("Attendre le chargement de la page")
    private void attendreChargement() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")
        ));
        Thread.sleep(1000);
    }

    @Step("Vérifier que la connexion est réussie")
    private void verifierConnexionReussie() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("mon-compte"),
                "Connexion echouee — URL incorrecte : " + url);
        Assert.assertFalse(driver.getPageSource().contains("woocommerce-form-login"),
                "Formulaire de login encore visible apres connexion");
    }

    @Step("Vérifier l'affichage du message d'erreur")
    private void verifierMessageErreur() {
        String pageSource = driver.getPageSource();
        Assert.assertTrue(
                pageSource.contains("woocommerce-error") ||
                        pageSource.contains("Erreur") ||
                        pageSource.contains("erreur") ||
                        pageSource.contains("incorrect") ||
                        pageSource.contains("invalide"),
                "Aucun message d'erreur affiche"
        );
    }

    // ==================== CAS OK ====================

    @Test
    @Story("Chargement de la page login")
    @Description("Vérifie que la page de connexion charge correctement")
    @Severity(SeverityLevel.NORMAL)
    public void testPageLoginCharge() throws InterruptedException {
        ouvrirPageLogin();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement champEmail = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("username"))
        );
        Assert.assertTrue(champEmail.isDisplayed(), "Champ email non visible");
        WebElement champMdp = driver.findElement(By.id("password"));
        Assert.assertTrue(champMdp.isDisplayed(), "Champ mot de passe non visible");
    }

    @Test
    @Story("Connexion avec identifiants valides")
    @Description("Vérifie qu'un utilisateur peut se connecter avec succès")
    @Severity(SeverityLevel.BLOCKER)
    public void testConnexionIdentifiantsValides() throws InterruptedException {
        ouvrirPageLogin();
        saisirEmail(EMAIL_VALIDE);
        saisirMotDePasse(MDP_VALIDE);
        clicConnexion();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".woocommerce-form-login")
        ));
        verifierConnexionReussie();
    }

    // ==================== CAS KO ====================

    @Test
    @Story("Connexion avec identifiants invalides")
    @Description("KO - Vérifie qu'un message d'erreur s'affiche avec de mauvais identifiants")
    @Severity(SeverityLevel.CRITICAL)
    public void testConnexionIdentifiantsInvalides() throws InterruptedException {
        ouvrirPageLogin();
        saisirEmail(EMAIL_INVALIDE);
        saisirMotDePasse(MDP_INVALIDE);
        clicConnexion();
        attendreChargement();
        verifierMessageErreur();
    }

    @Test
    @Story("Connexion avec email vide")
    @Description("KO - Vérifie qu'une erreur s'affiche quand l'email est vide")
    @Severity(SeverityLevel.CRITICAL)
    public void testConnexionEmailVide() throws InterruptedException {
        ouvrirPageLogin();
        saisirMotDePasse(MDP_VALIDE);
        clicConnexion();
        attendreChargement();
        verifierMessageErreur();
    }

    @Test
    @Story("Connexion avec mot de passe vide")
    @Description("KO - Vérifie qu'une erreur s'affiche quand le mot de passe est vide")
    @Severity(SeverityLevel.CRITICAL)
    public void testConnexionMotDePasseVide() throws InterruptedException {
        ouvrirPageLogin();
        saisirEmail(EMAIL_VALIDE);
        clicConnexion();
        attendreChargement();
        verifierMessageErreur();
    }

    @Test
    @Story("Connexion avec email format invalide")
    @Description("KO - Vérifie qu'une erreur s'affiche avec un email au format incorrect")
    @Severity(SeverityLevel.NORMAL)
    public void testConnexionEmailFormatInvalide() throws InterruptedException {
        ouvrirPageLogin();
        saisirEmail("emailsansarobas");
        saisirMotDePasse(MDP_INVALIDE);
        clicConnexion();
        attendreChargement();
        verifierMessageErreur();
    }
}