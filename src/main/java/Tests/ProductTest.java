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
@Feature("Catalogue Produits")
public class ProductTest extends BaseTest {

    private static final String URL_CATEGORIE = "https://www.drest.tn/product-category/femme/vetements/";

    // ==================== STEPS ====================

    @Step("Ouvrir la page catégorie vêtements femme")
    private void ouvrirPageCategorie() {
        driver.get(URL_CATEGORIE);
    }

    @Step("Vérifier que la page catégorie est chargée")
    private void verifierPageCategorieChargee() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.urlContains("product-category"));
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("product-category"), "URL incorrecte : " + url);
    }

    @Step("Vérifier que des produits sont affichés")
    private void verifierProduitsPresents() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement premierProduit = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("li.product")
                )
        );
        Assert.assertTrue(premierProduit.isDisplayed(), "Aucun produit visible");
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
        Thread.sleep(8000);
    }

    @Step("Vérifier que le titre du produit est visible et non vide")
    private void verifierTitreProduit() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement titre = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("h1.product_title")
                )
        );
        Assert.assertTrue(titre.isDisplayed(), "Titre du produit non visible");
        Assert.assertFalse(titre.getText().isEmpty(), "Titre du produit vide");
    }

    @Step("Vérifier que le prix du produit est visible et non vide")
    private void verifierPrixProduit() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement prix = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("p.price")
                )
        );
        Assert.assertTrue(prix.isDisplayed(), "Prix non visible");
        Assert.assertFalse(prix.getText().isEmpty(), "Prix vide");
    }

    // ==================== TESTS ====================

    @Test
    @Story("Chargement de la page catégorie")
    @Description("Vérifie que la page catégorie vêtements femme se charge correctement")
    @Severity(SeverityLevel.NORMAL)
    public void testPageCategorieCharge() {
        ouvrirPageCategorie();
        verifierPageCategorieChargee();
    }

    @Test
    @Story("Affichage des produits")
    @Description("Vérifie que les produits sont visibles dans la page catégorie")
    @Severity(SeverityLevel.CRITICAL)
    public void testProduitsPresents() {
        ouvrirPageCategorie();
        verifierProduitsPresents();
    }

    @Test
    @Story("Titre du produit")
    @Description("Vérifie que le titre du produit est visible et non vide sur la page détail")
    @Severity(SeverityLevel.CRITICAL)
    public void testClicProduitEtTitre() throws InterruptedException {
        ouvrirPageCategorie();
        clicPremierProduit();
        verifierTitreProduit();
    }

    @Test
    @Story("Prix du produit")
    @Description("Vérifie que le prix du produit est visible et non vide sur la page détail")
    @Severity(SeverityLevel.CRITICAL)
    public void testPrixProduitPresent() throws InterruptedException {
        ouvrirPageCategorie();
        clicPremierProduit();
        verifierPrixProduit();
    }
}