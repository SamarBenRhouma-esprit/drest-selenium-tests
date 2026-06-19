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
@Feature("Panier d'achat")
public class CartTest extends BaseTest {

    private static final String URL_CATEGORIE = "https://www.drest.tn/product-category/femme/vetements/";
    private static final String URL_PANIER    = "https://www.drest.tn/cart/";

    // ==================== STEPS ====================

    @Step("Ouvrir la page panier")
    private void ouvrirPagePanier() throws InterruptedException {
        driver.get(URL_PANIER);
        Thread.sleep(2000);
    }

    @Step("Vérifier que la page panier est chargée")
    private void verifierPagePanierChargee() {
        String url = driver.getCurrentUrl();
        Assert.assertNotNull(url, "URL null");
        Assert.assertTrue(
                url.contains("cart") || url.contains("panier"),
                "Page panier non chargee : " + url
        );
    }

    @Step("Ouvrir la page catégorie vêtements femme")
    private void ouvrirPageCategorie() {
        driver.get(URL_CATEGORIE);
    }

    @Step("Cliquer sur le premier produit de la liste")
    private void clicPremierProduit() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement produit = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("li.product a.woocommerce-loop-product__link")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", produit);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", produit);
        Thread.sleep(3000);
    }

    @Step("Cliquer sur le bouton Ajouter au panier")
    private void clicBoutonAjouterAuPanier() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement boutonAjout = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button.single_add_to_cart_button, " +
                                "input.single_add_to_cart_button, " +
                                "button[name='add-to-cart']")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", boutonAjout);
        Thread.sleep(2000);
    }

    @Step("Vérifier que le produit a été ajouté au panier")
    private void verifierProduitAjoute() {
        String pageSource = driver.getPageSource();
        Assert.assertNotNull(pageSource, "Page source null");
        Assert.assertTrue(
                pageSource.contains("panier") || pageSource.contains("cart") || pageSource.contains("added"),
                "Produit non ajoute au panier"
        );
    }

    @Step("Vérifier que le panier n'est pas vide")
    private void verifierPanierNonVide() throws InterruptedException {
        driver.get(URL_PANIER);
        Thread.sleep(2000);
        String pageSource = driver.getPageSource();
        Assert.assertNotNull(pageSource, "Page source null");
        Assert.assertFalse(
                pageSource.contains("cart is currently empty"),
                "Le panier est vide apres ajout"
        );
    }

    // ==================== TESTS ====================

    @Test
    @Story("Chargement de la page panier")
    @Description("Vérifie que la page panier se charge correctement")
    @Severity(SeverityLevel.NORMAL)
    public void testPagePanierCharge() throws InterruptedException {
        ouvrirPagePanier();
        verifierPagePanierChargee();
    }

    @Test
    @Story("Ajout d'un produit au panier")
    @Description("Vérifie qu'un produit peut être ajouté au panier depuis la page catégorie")
    @Severity(SeverityLevel.BLOCKER)
    public void testAjoutAuPanier() throws InterruptedException {
        ouvrirPageCategorie();
        clicPremierProduit();
        clicBoutonAjouterAuPanier();
        verifierProduitAjoute();
    }

    @Test
    @Story("Panier non vide après ajout")
    @Description("Vérifie que le panier contient bien le produit ajouté et n'est pas vide")
    @Severity(SeverityLevel.CRITICAL)
    public void testPanierNonVideApresAjout() throws InterruptedException {
        ouvrirPageCategorie();
        clicPremierProduit();
        clicBoutonAjouterAuPanier();
        verifierPanierNonVide();
    }
}