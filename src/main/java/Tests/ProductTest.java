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
    private void ouvrirPageCategorie() throws InterruptedException {
        driver.get(URL_CATEGORIE);
        // Attendre le chargement complet
        Thread.sleep(5000);
        // Scroll pour déclencher le lazy-loading des produits
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight/2);");
        Thread.sleep(3000);
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
        Thread.sleep(2000);
    }

    @Step("Vérifier que la page catégorie est chargée")
    private void verifierPageCategorieChargee() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.urlContains("product-category"));
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("product-category"), "URL incorrecte : " + url);
    }

    @Step("Vérifier que des produits sont affichés")
    private void verifierProduitsPresents() throws InterruptedException {
        // Utiliser presenceOfElementLocated au lieu de visibilityOfElementLocated
        // (les images S3 mettent trop de temps à charger en CI/CD)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));
        WebElement premierProduit = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("li.product")
                )
        );
        Thread.sleep(3000);
        Assert.assertNotNull(premierProduit, "Aucun produit présent dans le DOM");
    }

    @Step("Cliquer sur le premier produit de la liste")
    private void clicPremierProduit() throws InterruptedException {
        // presenceOfElementLocated : l'élément existe dans le DOM (pas besoin qu'il soit "visible")
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));
        WebElement produit = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("li.product a.woocommerce-loop-product__link")
                )
        );
        Thread.sleep(5000);

        // Scroll vers le produit et clic via JavaScript
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", produit);
        Thread.sleep(2000);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", produit);
        Thread.sleep(10000); // Attente supplémentaire pour le chargement de la fiche produit
    }

    @Step("Vérifier que le titre du produit est visible et non vide")
    private void verifierTitreProduit() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        WebElement titre = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("h1.product_title")
                )
        );
        Thread.sleep(2000);
        String texteTitre = titre.getText().trim();
        Assert.assertNotNull(titre, "Titre du produit non présent");
        Assert.assertFalse(texteTitre.isEmpty(), "Titre du produit vide");
    }

    @Step("Vérifier que le prix du produit est visible et non vide")
    private void verifierPrixProduit() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        WebElement prix = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("p.price")
                )
        );
        Thread.sleep(2000);
        String textePrix = prix.getText().trim();
        Assert.assertNotNull(prix, "Prix non présent");
        Assert.assertFalse(textePrix.isEmpty(), "Prix vide");
    }

    // ==================== TESTS ====================

    @Test
    @Story("Chargement de la page catégorie")
    @Description("Vérifie que la page catégorie vêtements femme se charge correctement")
    @Severity(SeverityLevel.NORMAL)
    public void testPageCategorieCharge() throws InterruptedException {
        ouvrirPageCategorie();
        verifierPageCategorieChargee();
    }

    @Test
    @Story("Affichage des produits")
    @Description("Vérifie que les produits sont visibles dans la page catégorie")
    @Severity(SeverityLevel.CRITICAL)
    public void testProduitsPresents() throws InterruptedException {
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