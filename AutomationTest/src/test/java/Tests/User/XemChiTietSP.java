package Tests.User;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class XemChiTietSP {

    WebDriver driver;
    WebDriverWait wait;
    Actions actions;

    By productCard = By.cssSelector(".product-card-v2");
    By productName = By.cssSelector(".product-name-v2");
    By productPrice = By.cssSelector(".ant-typography-danger");
    By searchIcon = By.cssSelector(".anticon-search");

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        actions = new Actions(driver);

        driver.manage().window().maximize();
        driver.get("http://localhost:5173/");

        wait.until(ExpectedConditions.presenceOfElementLocated(productCard));
    }

    // ===== Lấy danh sách sản phẩm =====
    public List<WebElement> getProducts() {
        return driver.findElements(productCard);
    }

    // ===== CLICK SAFE (fix lỗi click) =====
    public void clickSafe(WebElement e) {
        try {
            e.click();
        } catch (Exception ex) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", e);
        }
    }

    // ================= TEST =================

    // CT-1: Click sản phẩm
    @Test
    public void CT1_Click_Product() {
        WebElement p = getProducts().get(0);
        clickSafe(p);

        Assert.assertTrue(driver.getCurrentUrl().contains("detail"));
    }


    // CT-4: Click ảnh
    @Test
    public void CT4_Click_Image() {
        WebElement img = driver.findElement(By.cssSelector(".product-card-v2 img"));

        clickSafe(img);

        Assert.assertTrue(driver.getCurrentUrl().contains("detail"));
    }

    // CT-5: Hover hiển thị icon
    @Test
    public void CT5_Hover_Show_Icon() {
        WebElement p = getProducts().get(0);

        actions.moveToElement(p).perform();

        WebElement icon = wait.until(ExpectedConditions.visibilityOfElementLocated(searchIcon));

        Assert.assertTrue(icon.isDisplayed());
    }

    // CT-6: Click icon search
    @Test
    public void CT6_Click_Search() {
        WebElement p = getProducts().get(0);

        actions.moveToElement(p).perform();

        WebElement icon = wait.until(ExpectedConditions.elementToBeClickable(searchIcon));

        clickSafe(icon);

        Assert.assertTrue(driver.getCurrentUrl().contains("detail"));
    }

    // CT-7: Back lại
    @Test
    public void CT7_Back_List() {
        clickSafe(getProducts().get(0));

        driver.navigate().back();

        Assert.assertTrue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(productCard)).isDisplayed()
        );
    }

    // CT-8: Scroll load
    @Test
    public void CT8_Scroll_Load() {

        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");

        wait.withTimeout(Duration.ofSeconds(3));

        Assert.assertTrue(getProducts().size() > 0);
    }

    // CT-9: Ảnh hiển thị trong detail
    @Test
    public void CT9_Image_Display() {

        clickSafe(getProducts().get(0));

        WebElement img = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("img")
        ));

        Assert.assertTrue(img.isDisplayed());
    }

    // CT-10: Click nhiều lần
    @Test
    public void CT10_Click_Multiple() {

        WebElement p = getProducts().get(0);

        clickSafe(p);
        driver.navigate().back();

        wait.until(ExpectedConditions.visibilityOfElementLocated(productCard));

        clickSafe(getProducts().get(0));

        Assert.assertTrue(driver.getCurrentUrl().contains("detail"));
    }

    // CT-11: Check URL
    @Test
    public void CT11_Check_URL() {

        clickSafe(getProducts().get(0));

        Assert.assertTrue(
                driver.getCurrentUrl().contains("detail"),
                "URL không chứa detail"
        );
    }

    // CT-12: Load trang detail
    @Test
    public void CT12_Load_Detail_Page() {

        clickSafe(getProducts().get(0));

        boolean loaded = wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("img"))
        )) != null;

        Assert.assertTrue(loaded, "Trang detail không load");
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}