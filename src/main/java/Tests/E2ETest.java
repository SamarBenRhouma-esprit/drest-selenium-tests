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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

@Epic("Drest.tn E-Commerce")
@Feature("Tests End-to-End")
public class E2ETest extends BaseTest {

    private static final String URL_LOGIN     = "https://www.drest.tn/mon-compte/";
    private static final String URL_CATEGORIE = "https://www.drest.tn/product-category/femme/vetements/";
    private static final String URL_PANIER    = "https://www.drest.tn/cart/";
    private static final String EMAIL_VALIDE  = "benrhouma.samar44@gmail.com";
    private static final String MDP_VALIDE    = "@Samar123";

    // ==================== STEPS ====================

    @Step("Ouvrir la page de connexion")
    private void ouvrirPageLogin() throws InterruptedException {
        driver.get(URL_LOGIN);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
        Thread.sleep(1000);
    }

    @Step("Se connecter avec l'email : {email}")
    private void seConnecter(String email, String mdp) throws InterruptedException {
        driver.findElement(By.id("username")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(mdp);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", driver.findElement(By.name("login"))
        );
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".woocommerce-form-login")
        ));
    }

    @Step("Vérifier que la connexion est réussie")
    private void verifierConnexionReussie() {
        Assert.assertTrue(driver.getCurrentUrl().contains("mon-compte"),
                "Connexion echouee");
    }

    @Step("Naviguer vers la catégorie vêtements femme")
    private void naviguerVersCategorie() {
        driver.get(URL_CATEGORIE);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.urlContains("product-category"));
        Assert.assertTrue(driver.getCurrentUrl().contains("femme"),
                "Navigation categorie echouee");
    }

    @Step("Cliquer sur le premier produit")
    private void clicPremierProduit() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        WebElement produit = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("li.product a.woocommerce-loop-product__link")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", produit);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", produit);
        Thread.sleep(8000);
    }

    @Step("Vérifier que la page produit est chargée")
    private void verifierPageProduit() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        WebElement titre = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1.product_title"))
        );
        Assert.assertTrue(titre.isDisplayed(), "Page produit non chargee");
    }

    @Step("Ajouter le produit au panier")
    private void ajouterAuPanier() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        WebElement boutonAjout = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button.single_add_to_cart_button, " +
                                "input.single_add_to_cart_button, " +
                                "button[name='add-to-cart']")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", boutonAjout);
        Thread.sleep(3000);
        String pageSource = driver.getPageSource();
        Assert.assertTrue(
                pageSource.contains("panier") || pageSource.contains("cart") || pageSource.contains("added"),
                "Ajout au panier echoue"
        );
    }

    @Step("Vérifier que le panier n'est pas vide")
    private void verifierPanierNonVide() throws InterruptedException {
        driver.get(URL_PANIER);
        Thread.sleep(3000);
        Assert.assertFalse(
                driver.getPageSource().contains("cart is currently empty"),
                "Panier vide apres ajout"
        );
    }

    @Step("Aller à la page checkout")
    private void allerAuCheckout() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        WebElement boutonCheckout = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".checkout-button, a.checkout-button, " +
                                ".wc-proceed-to-checkout a, a[href*='checkout']")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", boutonCheckout);
        Thread.sleep(5000);
        Assert.assertTrue(
                driver.getCurrentUrl().contains("checkout") ||
                        driver.getCurrentUrl().contains("commande"),
                "Page checkout non chargee"
        );
    }

    @Step("Vérifier le formulaire de livraison")
    private void verifierFormulaireCheckout() {
        Assert.assertTrue(
                driver.getPageSource().contains("billing") ||
                        driver.getPageSource().contains("facturation") ||
                        driver.getPageSource().contains("livraison"),
                "Formulaire checkout non present"
        );
    }

    @Step("Saisir des identifiants invalides")
    private void saisirIdentifiantsInvalides() throws InterruptedException {
        driver.findElement(By.id("username")).sendKeys("faux@inexistant.tn");
        driver.findElement(By.id("password")).sendKeys("mauvaismdp123");
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", driver.findElement(By.name("login"))
        );
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")
        ));
        Thread.sleep(1000);
    }

    @Step("Vérifier l'affichage du message d'erreur")
    private void verifierMessageErreur() {
        String pageSource = driver.getPageSource();
        Assert.assertTrue(
                pageSource.contains("woocommerce-error") ||
                        pageSource.contains("Erreur") ||
                        pageSource.contains("erreur") ||
                        pageSource.contains("incorrect"),
                "Message erreur connexion non affiche"
        );
    }

    @Step("Vérifier qu'on est toujours sur la page login")
    private void verifierToujoursSurPageLogin() {
        Assert.assertTrue(
                driver.getCurrentUrl().contains("mon-compte"),
                "Redirection incorrecte apres echec connexion"
        );
    }

    @Step("Naviguer vers la section Femme via le menu")
    private void naviguerViaMenu() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement menuFemme = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a.menu-item[href*='femme']")
                )
        );
        Assert.assertTrue(menuFemme.isDisplayed(), "Menu Femme non visible");
        menuFemme.click();
        wait.until(ExpectedConditions.urlContains("femme"));
        Assert.assertTrue(driver.getCurrentUrl().contains("femme"),
                "Navigation Femme echouee");
    }

    @Step("Rechercher le mot-clé : {motCle}")
    private void rechercherProduit(String motCle) throws InterruptedException {
        driver.get("https://www.drest.tn");
        Thread.sleep(2000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement searchInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("dgwt-wcas-search-input-1")
                )
        );
        searchInput.click();
        searchInput.sendKeys(motCle);
        Thread.sleep(1500);
        searchInput.sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.urlContains(motCle));
        Assert.assertTrue(driver.getCurrentUrl().contains(motCle),
                "Recherche echouee");
    }

    @Step("Vérifier la fiche produit")
    private void verifierFicheProduit() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement produit = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("li.product a.woocommerce-loop-product__link")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", produit);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", produit);
        Thread.sleep(6000);
        WebElement titreProduit = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1.product_title"))
        );
        Assert.assertTrue(titreProduit.isDisplayed(), "Titre produit non visible");
        Assert.assertFalse(titreProduit.getText().isEmpty(), "Titre produit vide");
    }

    // ==================== TESTS ====================

    @Test
    @Story("Parcours achat complet")
    @Description("E2E OK - Connexion → Navigation → Ajout panier → Checkout")
    @Severity(SeverityLevel.BLOCKER)
    public void testParcourAchatComplet() throws InterruptedException {
        ouvrirPageLogin();
        seConnecter(EMAIL_VALIDE, MDP_VALIDE);
        verifierConnexionReussie();
        naviguerVersCategorie();
        clicPremierProduit();
        verifierPageProduit();
        ajouterAuPanier();
        verifierPanierNonVide();
        allerAuCheckout();
        verifierFormulaireCheckout();
    }

    @Test
    @Story("Parcours connexion invalide")
    @Description("E2E KO - Tentative connexion avec mauvais identifiants")
    @Severity(SeverityLevel.CRITICAL)
    public void testParcourConnexionInvalide() throws InterruptedException {
        ouvrirPageLogin();
        saisirIdentifiantsInvalides();
        verifierMessageErreur();
        verifierToujoursSurPageLogin();
    }

    @Test
    @Story("Parcours navigation et recherche")
    @Description("E2E - Navigation menu → Recherche produit → Consultation fiche produit")
    @Severity(SeverityLevel.NORMAL)
    public void testParcourNavigationRecherche() throws InterruptedException {
        naviguerViaMenu();
        rechercherProduit("robe");
        verifierFicheProduit();
    }
}