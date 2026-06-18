package Tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

@Epic("Drest.tn E-Commerce")
@Feature("Recherche de produits")
public class SearchTest extends BaseTest {

    @Test
    @Story("Recherche avec mot-clé valide")
    @Description("Vérifie qu'une recherche avec le mot 'adidas' retourne des résultats et redirige correctement")
    @Severity(SeverityLevel.CRITICAL)
    public void testRechercheValide() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement searchInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("dgwt-wcas-search-input-1")
                )
        );
        searchInput.click();
        searchInput.sendKeys("adidas");
        Thread.sleep(1500);
        searchInput.sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.urlContains("adidas"));
        String url = driver.getCurrentUrl();
        Assert.assertNotNull(url, "URL null");
        Assert.assertTrue(url.contains("adidas"), "URL incorrecte : " + url);
    }

    @Test
    @Story("Recherche avec mot-clé inexistant")
    @Description("Vérifie qu'une recherche avec un mot inexistant affiche un message indiquant l'absence de résultats")
    @Severity(SeverityLevel.NORMAL)
    public void testRechercheVideSansResultat() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement searchInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("dgwt-wcas-search-input-1")
                )
        );
        searchInput.click();
        searchInput.sendKeys("xxxxxxxxxinexistant");
        Thread.sleep(1500);
        searchInput.sendKeys(Keys.ENTER);

        Thread.sleep(3000);
        String pageSource = driver.getPageSource();
        Assert.assertNotNull(pageSource, "Page source null");
        Assert.assertTrue(
                pageSource.contains("Aucun") ||
                        pageSource.contains("aucun") ||
                        pageSource.contains("0 result") ||
                        pageSource.contains("no result"),
                "Aucun message de resultat vide detecte"
        );
    }
}