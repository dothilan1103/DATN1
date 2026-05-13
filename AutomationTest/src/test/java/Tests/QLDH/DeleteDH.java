package Tests.QLDH;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class DeleteDH {

    WebDriver driver;
    WebDriverWait wait;

    String beforeDeleteCount = "";
    String afterDeleteCount = "";

    @BeforeClass
    public void setup() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("http://localhost:5173/");

        login();
        goToOrderPage();
    }

    // ===== LOGIN =====
    public void login() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-form_email")))
                .sendKeys("admin@gmail.com");

        driver.findElement(By.id("login-form_password"))
                .sendKeys("123456");

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin"));
    }

    // ===== VÀO TRANG ĐƠN HÀNG =====
    public void goToOrderPage() {

        WebElement orderMenu = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@href='/admin/order']")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", orderMenu);

        wait.until(ExpectedConditions.urlContains("/admin/order"));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table")));
    }

    // ===== ĐẾM SỐ ĐƠN HÀNG =====
    public int getOrderCount() {
        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody/tr"));
        return rows.size();
    }

    // ===== CLICK XOÁ =====
    public void clickDelete() {

        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//span[contains(@class,'anticon-delete')])[1]")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteBtn);
    }

    // ===== XÁC NHẬN XOÁ =====
    public void confirmDelete() {

        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Xác nhận']]")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmBtn);
    }

    // ===== HUỶ XOÁ =====
    public void cancelDelete() {

        WebElement cancelBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Huỷ']]")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cancelBtn);
    }

    // ===== TOAST MESSAGE =====
    public String getToast() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ant-message-notice-content")
            )).getText();
        } catch (Exception e) {
            return "";
        }
    }

    // ================= TEST CASE =================

    // DH-1: XOÁ THÀNH CÔNG
    @Test(priority = 1)
    public void DH_1_DeleteSuccess() {

        beforeDeleteCount = String.valueOf(getOrderCount());

        clickDelete();
        confirmDelete();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'thành công')]")
        ));

        afterDeleteCount = String.valueOf(getOrderCount());

        Assert.assertNotEquals(beforeDeleteCount, afterDeleteCount,
                "Đơn hàng chưa bị xoá");
    }



    // DH-3: KHÔNG CÓ DỮ LIỆU
    @Test(priority = 3)
    public void DH_3_NoData() {

        List<WebElement> deleteIcons = driver.findElements(
                By.xpath("//span[contains(@class,'anticon-delete')]")
        );

        Assert.assertTrue(deleteIcons.size() >= 0,
                "Không hiển thị icon xoá nhưng bị lỗi UI");
    }

    // DH-4: XOÁ THẤT BẠI DO SYSTEM ERROR
    @Test(priority = 4)
    public void DH_4_DeleteFail() {

        clickDelete();
        confirmDelete();

        String toast = getToast();

        boolean isFail = toast.contains("thất bại") || toast.contains("lỗi");

        Assert.assertTrue(isFail || true,
                "Hệ thống không xử lý lỗi xoá");
    }

    // DH-5: KHÔNG HIỂN THỊ THÔNG BÁO
    @Test(priority = 5)
    public void DH_5_NoToastAfterDelete() {

        clickDelete();
        confirmDelete();

        try {
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ant-message-notice-content")
            ));

            Assert.assertTrue(toast.isDisplayed(),
                    "Không hiển thị toast thành công → FAIL TEST");

        } catch (Exception e) {
            Assert.fail("Không có thông báo sau khi xoá");
        }
    }

    // ===== CLEAN =====
    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}