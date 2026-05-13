package Tests.User;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class XemSPTheoDM {

    WebDriver driver;
    Actions actions;
    WebDriverWait wait;

    String baseUrl = "http://localhost:5173/";

    By menuDM = By.xpath("//span[contains(text(),'Danh mục sản phẩm')]");
    By productList = By.xpath("//div[contains(@class,'product')]");
    By emptyText = By.xpath("//*[contains(text(),'Không có sản phẩm')]");

    // ================= SETUP =================

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        actions = new Actions(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.manage().window().maximize();
    }

    // Mỗi test quay lại trang chủ để tránh lỗi run all
    @BeforeMethod
    public void beforeEachTest() {
        driver.get(baseUrl);
    }

    // ================= HÀM CHỌN DANH MỤC =================

    public void selectCategory(String categoryName) {

        WebElement menu = wait.until(
                ExpectedConditions.visibilityOfElementLocated(menuDM)
        );

        actions.moveToElement(menu)
                .pause(Duration.ofSeconds(1))
                .perform();

        By categoryXpath = By.xpath("//li//a[normalize-space()='" + categoryName + "']");

        WebElement category = wait.until(
                ExpectedConditions.elementToBeClickable(categoryXpath)
        );

        try {
            category.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", category);
        }

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(productList),
                ExpectedConditions.presenceOfElementLocated(emptyText)
        ));
    }

    // ================= HÀM CHECK CHUNG =================

    public void checkCategory(String name) {

        selectCategory(name);

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productList));

        List<WebElement> products = driver.findElements(productList);

        Assert.assertTrue(
                products.size() > 0,
                "Không có sản phẩm trong " + name
        );
    }

    // ================= TEST CASE =================

    @Test
    public void XS1_Display_Menu() {
        Assert.assertTrue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(menuDM))
                        .isDisplayed()
        );
    }

    @Test public void XS5_SetBo() { checkCategory("Set Bộ"); }

    @Test public void XS6_Ao() { checkCategory("Áo"); }

    @Test public void XS7_AoPolo() { checkCategory("Áo Polo"); }

    @Test public void XS8_AoSoMi() { checkCategory("Áo Sơ mi"); }

    @Test public void XS9_QuanJean() { checkCategory("Quần Jean"); }

    @Test public void XS10_QuanKaki() { checkCategory("Quần Kaki"); }

    @Test
    public void XS11_Empty_Category() {

        selectCategory("Danh mục test");

        boolean hasEmpty = wait.until(
                ExpectedConditions.visibilityOfElementLocated(emptyText)
        ).isDisplayed();

        Assert.assertTrue(hasEmpty);
    }

    @Test
    public void XS12_Load_Product() {

        selectCategory("Áo");

        List<WebElement> products = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(productList)
        );

        Assert.assertTrue(products.size() > 0);
    }

    @Test
    public void XS13_Click_Multiple() {

        selectCategory("Áo");
        driver.get(baseUrl);

        selectCategory("Đầm");
        driver.get(baseUrl);

        selectCategory("Quần");

        Assert.assertTrue(driver.getCurrentUrl().contains("category"));
    }

    @Test
    public void XS14_Hover_Menu() {

        WebElement menu = wait.until(
                ExpectedConditions.visibilityOfElementLocated(menuDM)
        );

        actions.moveToElement(menu).perform();

        WebElement subMenu = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//li//a")
                )
        );

        Assert.assertTrue(subMenu.isDisplayed());
    }

    @Test
    public void XS15_Click_By_JS() {

        WebElement menu = wait.until(
                ExpectedConditions.visibilityOfElementLocated(menuDM)
        );

        actions.moveToElement(menu).perform();

        WebElement category = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//li//a[normalize-space()='Áo']")
                )
        );

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", category);

        List<WebElement> products = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(productList)
        );

        Assert.assertTrue(products.size() > 0);
    }

    // ================= CLOSE =================

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}