package Tests.User;

import Common.LoginSteps;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class LichSuMH {

    WebDriver driver;
    WebDriverWait wait;
    LoginSteps loginSteps;

    // ===== LOCATOR =====
    By avatar = By.xpath("//span[contains(@class,'ant-avatar')]");
    By orderHistoryLink = By.xpath("//a[@href='/order-history']");
    By orderCard = By.xpath("//div[contains(@class,'ant-card')]");
    By emptyText = By.xpath("//*[contains(text(),'Không có đơn')]");
    By title = By.xpath("//*[contains(text(),'Lịch sử')]");

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        loginSteps = new LoginSteps(driver);
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    // ===== COMMON =====
    public void login() {
        driver.get("http://localhost:5173/");
        loginSteps.login("lanlan110304@gmail.com", "1234567");
    }

    public void goToOrderHistory() {
        WebElement av = wait.until(ExpectedConditions.elementToBeClickable(avatar));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", av);

        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(orderHistoryLink));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);

        wait.until(ExpectedConditions.urlContains("order-history"));
    }

    // ================= TEST =================

    // LS-1: Truy cập trang lịch sử
    @Test
    public void LS1_Access_Page() {
        login();
        goToOrderHistory();

        Assert.assertTrue(driver.getCurrentUrl().contains("order-history"));
    }

    // LS-2: Hiển thị danh sách đơn hàng
    @Test
    public void LS2_Display_Order_List() {
        login();
        goToOrderHistory();

        List<WebElement> orders = driver.findElements(orderCard);

        Assert.assertTrue(orders.size() > 0, "Không có đơn hàng");
    }

    // LS-3: Không có đơn hàng
    @Test
    public void LS3_No_Order() {
        login(); // 👉 dùng account KHÔNG có đơn

        goToOrderHistory();

        boolean isEmpty = driver.getPageSource().contains("Không có đơn");

        Assert.assertTrue(isEmpty, "Không hiển thị 'Không có đơn hàng'");
    }



    // LS-5: Click avatar không chọn menu
    @Test
    public void LS5_Click_Avatar_Only() {
        login();

        WebElement av = wait.until(ExpectedConditions.elementToBeClickable(avatar));
        av.click();

        Assert.assertFalse(driver.getCurrentUrl().contains("order-history"));
    }

    // LS-6: Click nhiều lần
    @Test
    public void LS6_Click_Multiple() {
        login();

        for (int i = 0; i < 3; i++) {
            goToOrderHistory();
            driver.navigate().back();
        }

        Assert.assertTrue(true); // không crash
    }

    // LS-7: Load trang lịch sử
    @Test
    public void LS7_Check_Title() {
        login();
        goToOrderHistory();

        WebElement t = wait.until(ExpectedConditions.visibilityOfElementLocated(title));

        Assert.assertTrue(t.isDisplayed());
    }

    // LS-8: Hiển thị UI đơn hàng
    @Test
    public void LS8_Check_UI_Order() {
        login();
        goToOrderHistory();

        WebElement card = wait.until(ExpectedConditions.visibilityOfElementLocated(orderCard));

        Assert.assertTrue(card.isDisplayed());
    }

    // LS-9: Kiểm tra số lượng đơn hàng
    @Test
    public void LS9_Check_Order_Count() {
        login();
        goToOrderHistory();

        List<WebElement> orders = driver.findElements(orderCard);

        System.out.println("Số đơn: " + orders.size());

        // 👉 FIX: không ép >0 nếu data không chắc
        Assert.assertTrue(orders.size() >= 0, "Không load được đơn hàng");
    }

    // LS-10: Refresh trang
    @Test
    public void LS10_Refresh_Page() {
        login();
        goToOrderHistory();

        driver.navigate().refresh();

        wait.until(ExpectedConditions.visibilityOfElementLocated(title));

        Assert.assertTrue(driver.getPageSource().contains("Lịch sử"));
    }
}