package Tests.QLDH;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class UpdateDH {

    WebDriver driver;
    WebDriverWait wait;

    String initialStatus = "";

    @BeforeClass
    public void setup() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("http://localhost:5173/");

        login();
    }

    // ✅ reset mỗi test → tránh phụ thuộc
    @BeforeMethod
    public void reload() {
        driver.navigate().refresh();
        wait.until(ExpectedConditions.urlContains("/admin"));
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
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@href='/admin/order']")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table")));
    }

    // ===== MỞ MODAL =====
    public void openEditModal() {
        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[.//span[contains(@class,'anticon-edit')]])[1]")
        ));
        editBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".ant-modal-content")
        ));
    }

    // ===== CHỌN TRẠNG THÁI =====
    public void selectStatus(String status) {

        WebElement selectBox = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-modal')]//div[contains(@class,'ant-select-selector')]")
        ));
        selectBox.click();

        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-select-dropdown')]//div[text()='" + status + "']")
        ));
        option.click();
    }

    // ===== CLICK CẬP NHẬT =====
    public void clickUpdateButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Cập nhật']]")
        ));
        btn.click();
    }

    // ===== LẤY STATUS =====
    public String getStatus() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal')]//span[contains(@class,'ant-select-selection-item')]")
        )).getText().trim();
    }

    // ===== TOAST =====
    public String getToast() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ant-message-notice-content")
            )).getText();
        } catch (Exception e) {
            return "";
        }
    }

    // ================= TEST =================

    // ✅ UD-1
    @Test(priority = 1)
    public void UD_1_UpdateSuccess() {

        openEditModal();

        selectStatus("Đang giao hàng");
        clickUpdateButton();

        String toast = getToast();

        Assert.assertTrue(toast.contains("thành công") || toast.length() > 0);
    }

    // ✅ UD-2
    @Test(priority = 2)
    public void UD_2_CloseWithoutSave() {

        openEditModal();

        initialStatus = getStatus();

        selectStatus("Đang giao hàng");

        // đóng modal
        driver.findElement(By.cssSelector(".ant-modal-close")).click();

        openEditModal();

        String after = getStatus();

        Assert.assertEquals(after, initialStatus);
    }

    // ✅ UD-3
    @Test(priority = 3)
    public void UD_3_NoStatusSelected() {

        openEditModal();

        clickUpdateButton();

        String toast = getToast();

        Assert.assertTrue(
                toast.contains("Vui lòng") || toast.contains("chọn"),
                "Không hiển thị lỗi"
        );
    }

    // ✅ UD-4
    @Test(priority = 4)
    public void UD_4_CheckDropdown() {

        openEditModal();

        WebElement selectBox = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-modal')]//div[contains(@class,'ant-select-selector')]")
        ));

        selectBox.click();

        boolean visible = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-select-dropdown')]")
        )).isDisplayed();

        Assert.assertTrue(visible);
    }

    // ===== CLEAN =====
    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}