package Tests;

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

    By menuDM = By.xpath("//span[contains(text(),'Danh mục sản phẩm')]");
    By productList = By.xpath("//div[contains(@class,'product')]");
    By emptyText = By.xpath("//*[contains(text(),'Không có sản phẩm')]");

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        actions = new Actions(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.manage().window().maximize();
        driver.get("http://161.248.4.185:8081/");
    }

    // ===== HÀM CHỌN DANH MỤC =====
    public void selectCategory(String categoryName) {

        WebElement menu = wait.until(ExpectedConditions.visibilityOfElementLocated(menuDM));
        actions.moveToElement(menu).pause(Duration.ofSeconds(1)).perform();

        WebElement category = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li//a[normalize-space()='" + categoryName + "']")
        ));

        try {
            category.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", category);
        }

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(productList),
                ExpectedConditions.presenceOfElementLocated(emptyText)
        ));
    }

    // ================= TEST =================

    // XS-1: Hiển thị menu danh mục
    @Test
    public void XS1_Display_Menu() {
        Assert.assertTrue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(menuDM)).isDisplayed()
        );
    }

    // XS-2 → XS-10: các danh mục
    @Test public void XS2_AoKhoac() { checkCategory("Áo khoác"); }
    @Test public void XS3_Dam() { checkCategory("Đầm"); }
    @Test public void XS4_Quan() { checkCategory("Quần"); }
    @Test public void XS5_SetBo() { checkCategory("Set Bộ"); }
    @Test public void XS6_Ao() { checkCategory("Áo"); }
    @Test public void XS7_AoPolo() { checkCategory("Áo Polo"); }
    @Test public void XS8_AoSoMi() { checkCategory("Áo Sơ mi"); }
    @Test public void XS9_QuanJean() { checkCategory("Quần Jean"); }
    @Test public void XS10_QuanKaki() { checkCategory("Quần Kaki"); }

    // Hàm check chung
    public void checkCategory(String name) {
        selectCategory(name);

        List<WebElement> products = driver.findElements(productList);

        Assert.assertTrue(
                products.size() > 0,
                "Không có sản phẩm trong " + name
        );
    }

    // XS-11: danh mục không có sản phẩm
    @Test
    public void XS11_Empty_Category() {

        selectCategory("Danh mục test"); // 👉 sửa nếu có danh mục rỗng thật

        boolean hasEmpty = driver.getPageSource().contains("Không có sản phẩm");

        Assert.assertTrue(hasEmpty, "Không hiển thị 'Không có sản phẩm'");
    }

    // XS-12: load sản phẩm
    @Test
    public void XS12_Load_Product() {

        selectCategory("Áo");

        List<WebElement> products = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(productList)
        );

        Assert.assertTrue(products.size() > 0);
    }

    // XS-13: click nhiều danh mục nhanh
    @Test
    public void XS13_Click_Multiple() {

        selectCategory("Áo");
        selectCategory("Đầm");
        selectCategory("Quần");

        Assert.assertTrue(driver.getCurrentUrl().contains("category"));
    }

    // XS-14: hover menu
    @Test
    public void XS14_Hover_Menu() {

        WebElement menu = wait.until(ExpectedConditions.visibilityOfElementLocated(menuDM));

        actions.moveToElement(menu).perform();

        WebElement subMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//li//a")
        ));

        Assert.assertTrue(subMenu.isDisplayed());
    }

    // XS-15: click bằng JS
    @Test
    public void XS15_Click_By_JS() {

        WebElement menu = wait.until(ExpectedConditions.visibilityOfElementLocated(menuDM));
        actions.moveToElement(menu).perform();

        WebElement category = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//li//a[normalize-space()='Áo']")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", category);

        List<WebElement> products = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(productList)
        );

        Assert.assertTrue(products.size() > 0);
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}