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
@Feature("Tests End-to-End")
public class E2ETest extends BaseTest {

    private static final String URL_LOGIN     = "https://www.drest.tn/mon-compte/";
    private static final String URL_CATEGORIE = "https://www.drest.tn/product-category/femme/vetements/";
    private static final String URL_PANIER    = "https://www.drest.tn/cart/";
    private static final String EMAIL_VALIDE  = "benrhouma.samar44@gmail.com";
    private static final String MDP_VALIDE    = "@Samar123";

    @Test
    @Story("Parcours achat complet")
    @Description("E2E OK - Connexion → Navigation → Ajout panier → Checkout")
    @Severity(SeverityLevel.BLOCKER)
    public void testParcourAchatComplet() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        // ETAPE 1 - Connexion
        driver.get(URL_LOGIN);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
        Thread.sleep(1000);
        driver.findElement(By.id("username")).sendKeys(EMAIL_VALIDE);
        driver.findElement(By.id("password")).sendKeys(MDP_VALIDE);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", driver.findElement(By.name("login"))
        );
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".woocommerce-form-login")
        ));
        Assert.assertTrue(driver.getCurrentUrl().contains("mon-compte"),
                "Etape 1 - Connexion echouee");

        // ETAPE 2 - Navigation vers categorie Femme
        driver.get(URL_CATEGORIE);
        wait.until(ExpectedConditions.urlContains("product-category"));
        Assert.assertTrue(driver.getCurrentUrl().contains("femme"),
                "Etape 2 - Navigation categorie echouee");

        // ETAPE 3 - Clic sur un produit
        WebElement produit = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("li.product a.woocommerce-loop-product__link")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", produit);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", produit);
        Thread.sleep(8000);

        WebElement titre = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1.product_title"))
        );
        Assert.assertTrue(titre.isDisplayed(), "Etape 3 - Page produit non chargee");

        // ETAPE 4 - Ajout au panier
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
                "Etape 4 - Ajout au panier echoue"
        );

        // ETAPE 5 - Aller au panier
        driver.get(URL_PANIER);
        Thread.sleep(3000);
        Assert.assertFalse(
                driver.getPageSource().contains("cart is currently empty"),
                "Etape 5 - Panier vide apres ajout"
        );

        // ETAPE 6 - Aller au checkout
        WebElement boutonCheckout = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".checkout-button, a.checkout-button, .wc-proceed-to-checkout a")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", boutonCheckout);
        Thread.sleep(5000);

        Assert.assertTrue(
                driver.getCurrentUrl().contains("checkout") || driver.getCurrentUrl().contains("commande"),
                "Etape 6 - Page checkout non chargee"
        );

        // ETAPE 7 - Verifier formulaire checkout
        Assert.assertTrue(
                driver.getPageSource().contains("billing") ||
                        driver.getPageSource().contains("facturation") ||
                        driver.getPageSource().contains("livraison"),
                "Etape 7 - Formulaire checkout non present"
        );
    }

    @Test
    @Story("Parcours connexion invalide")
    @Description("E2E KO - Tentative connexion avec mauvais identifiants")
    @Severity(SeverityLevel.CRITICAL)
    public void testParcourConnexionInvalide() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // ETAPE 1 - Ouvrir page login
        driver.get(URL_LOGIN);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
        Thread.sleep(1000);

        // ETAPE 2 - Saisir mauvais identifiants
        driver.findElement(By.id("username")).sendKeys("faux@inexistant.tn");
        driver.findElement(By.id("password")).sendKeys("mauvaismdp123");
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", driver.findElement(By.name("login"))
        );

        // ETAPE 3 - Verifier message erreur
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")
        ));
        Thread.sleep(1000);

        String pageSource = driver.getPageSource();
        Assert.assertTrue(
                pageSource.contains("woocommerce-error") ||
                        pageSource.contains("Erreur") ||
                        pageSource.contains("erreur") ||
                        pageSource.contains("incorrect"),
                "E2E KO - Message erreur connexion non affiche"
        );

        // ETAPE 4 - Verifier qu'on est toujours sur la page login
        Assert.assertTrue(
                driver.getCurrentUrl().contains("mon-compte"),
                "E2E KO - Redirection incorrecte apres echec connexion"
        );
    }

    @Test
    @Story("Parcours navigation et recherche")
    @Description("E2E - Navigation menu → Recherche produit → Consultation fiche produit")
    @Severity(SeverityLevel.NORMAL)
    public void testParcourNavigationRecherche() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // ETAPE 1 - Verifier menu principal
        WebElement menuFemme = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a.menu-item[href*='femme']")
                )
        );
        Assert.assertTrue(menuFemme.isDisplayed(), "Etape 1 - Menu Femme non visible");

        // ETAPE 2 - Naviguer vers Femme
        menuFemme.click();
        wait.until(ExpectedConditions.urlContains("femme"));
        Assert.assertTrue(driver.getCurrentUrl().contains("femme"),
                "Etape 2 - Navigation Femme echouee");

        // ETAPE 3 - Rechercher un produit
        driver.get("https://www.drest.tn");
        Thread.sleep(2000);
        WebElement searchInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("dgwt-wcas-search-input-1")
                )
        );
        searchInput.click();
        searchInput.sendKeys("robe");
        Thread.sleep(1500);
        searchInput.sendKeys(org.openqa.selenium.Keys.ENTER);

        wait.until(ExpectedConditions.urlContains("robe"));
        Assert.assertTrue(driver.getCurrentUrl().contains("robe"),
                "Etape 3 - Recherche echouee");

        // ETAPE 4 - Cliquer sur un produit
        WebElement produit = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("li.product a.woocommerce-loop-product__link")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", produit);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", produit);
        Thread.sleep(6000);

        // ETAPE 5 - Verifier fiche produit
        WebElement titreProduit = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1.product_title"))
        );
        Assert.assertTrue(titreProduit.isDisplayed(), "Etape 5 - Titre produit non visible");
        Assert.assertFalse(titreProduit.getText().isEmpty(), "Etape 5 - Titre produit vide");
    }
}