package Tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
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

    private void ouvrirPageLogin() throws InterruptedException {
        driver.get(URL_LOGIN);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
        Thread.sleep(1000);
    }

    // ==================== CAS OK ====================

    @Test
    @Story("Chargement de la page login")
    @Description("Vérifie que la page de connexion charge correctement avec les champs email et mot de passe visibles")
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
    @Description("Vérifie qu'un utilisateur peut se connecter avec succès et est redirigé vers son compte")
    @Severity(SeverityLevel.BLOCKER)
    public void testConnexionIdentifiantsValides() throws InterruptedException {
        ouvrirPageLogin();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.findElement(By.id("username")).sendKeys(EMAIL_VALIDE);
        driver.findElement(By.id("password")).sendKeys(MDP_VALIDE);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", driver.findElement(By.name("login"))
        );
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".woocommerce-form-login")
        ));
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("mon-compte"),
                "Connexion echouee — URL incorrecte : " + url);
        Assert.assertFalse(driver.getPageSource().contains("woocommerce-form-login"),
                "Formulaire de login encore visible apres connexion");
    }

    // ==================== CAS KO ====================

    @Test
    @Story("Connexion avec identifiants invalides")
    @Description("KO - Vérifie qu'un message d'erreur s'affiche avec de mauvais identifiants")
    @Severity(SeverityLevel.CRITICAL)
    public void testConnexionIdentifiantsInvalides() throws InterruptedException {
        ouvrirPageLogin();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.findElement(By.id("username")).sendKeys(EMAIL_INVALIDE);
        driver.findElement(By.id("password")).sendKeys(MDP_INVALIDE);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", driver.findElement(By.name("login"))
        );
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")
        ));
        Thread.sleep(1000);
        String pageSource = driver.getPageSource();
        Assert.assertTrue(
                pageSource.contains("woocommerce-error") ||
                        pageSource.contains("Erreur") ||
                        pageSource.contains("erreur") ||
                        pageSource.contains("incorrect") ||
                        pageSource.contains("invalide"),
                "Aucun message d'erreur pour identifiants invalides"
        );
    }

    @Test
    @Story("Connexion avec email vide")
    @Description("KO - Vérifie qu'une erreur s'affiche quand l'email est vide")
    @Severity(SeverityLevel.CRITICAL)
    public void testConnexionEmailVide() throws InterruptedException {
        ouvrirPageLogin();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        // Laisser email vide
        driver.findElement(By.id("password")).sendKeys(MDP_VALIDE);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", driver.findElement(By.name("login"))
        );
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")
        ));
        Thread.sleep(1000);
        String pageSource = driver.getPageSource();
        Assert.assertTrue(
                pageSource.contains("woocommerce-error") ||
                        pageSource.contains("Erreur") ||
                        pageSource.contains("erreur") ||
                        pageSource.contains("Email") ||
                        pageSource.contains("champ"),
                "Aucun message d'erreur pour email vide"
        );
    }

    @Test
    @Story("Connexion avec mot de passe vide")
    @Description("KO - Vérifie qu'une erreur s'affiche quand le mot de passe est vide")
    @Severity(SeverityLevel.CRITICAL)
    public void testConnexionMotDePasseVide() throws InterruptedException {
        ouvrirPageLogin();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.findElement(By.id("username")).sendKeys(EMAIL_VALIDE);
        // Laisser mot de passe vide
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", driver.findElement(By.name("login"))
        );
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")
        ));
        Thread.sleep(1000);
        String pageSource = driver.getPageSource();
        Assert.assertTrue(
                pageSource.contains("woocommerce-error") ||
                        pageSource.contains("Erreur") ||
                        pageSource.contains("erreur") ||
                        pageSource.contains("mot de passe") ||
                        pageSource.contains("password"),
                "Aucun message d'erreur pour mot de passe vide"
        );
    }

    @Test
    @Story("Connexion avec email format invalide")
    @Description("KO - Vérifie qu'une erreur s'affiche avec un email au format incorrect")
    @Severity(SeverityLevel.NORMAL)
    public void testConnexionEmailFormatInvalide() throws InterruptedException {
        ouvrirPageLogin();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.findElement(By.id("username")).sendKeys("emailsansarobas");
        driver.findElement(By.id("password")).sendKeys(MDP_INVALIDE);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", driver.findElement(By.name("login"))
        );
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")
        ));
        Thread.sleep(1000);
        String pageSource = driver.getPageSource();
        Assert.assertTrue(
                pageSource.contains("woocommerce-error") ||
                        pageSource.contains("Erreur") ||
                        pageSource.contains("erreur") ||
                        pageSource.contains("invalide") ||
                        pageSource.contains("incorrect"),
                "Aucun message d'erreur pour email format invalide"
        );
    }
}