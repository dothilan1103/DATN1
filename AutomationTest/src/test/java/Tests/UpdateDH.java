package Tests;

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

        driver.get("http://161.248.4.185:8081/login");

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
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@href='/admin/order']")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);

        wait.until(ExpectedConditions.urlContains("/admin/order"));
    }

    // ===== MỞ MODAL =====
    public void openEditModal() {
        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//span[contains(@class,'anticon-edit')])[1]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editBtn);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".ant-modal-content")
        ));
    }

    // ===== CHỌN TRẠNG THÁI =====
    public void selectStatus(String status) {

        WebElement selectBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal')]//div[contains(@class,'ant-select')]")
        ));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new MouseEvent('mousedown', {bubbles: true}))",
                selectBox
        );

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-select-dropdown')]")
        ));

        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-select-item-option-content') and normalize-space()='" + status + "']")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
    }

    // ===== CLICK NÚT CẬP NHẬT =====
    public void clickUpdateButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Cập nhật')]")
        ));
        btn.click();
    }

    // ===== LẤY STATUS =====
    public String getStatus() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal')]//span[contains(@class,'ant-select-selection-item')]")
        )).getText().trim();
    }

    // ===== LẤY TOAST MESSAGE =====
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

    // UD-1
    @Test(priority = 1)
    public void UD_1_UpdateSuccess() {

        openEditModal();

        selectStatus("Đang giao hàng");
        clickUpdateButton();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath("//div[contains(@class,'ant-modal')]//span[contains(@class,'ant-select-selection-item')]"),
                "Đang giao hàng"
        ));

        String actual = getStatus();
        Assert.assertEquals(actual, "Đang giao hàng");
    }

    // UD-2
    @Test(priority = 2)
    public void UD_2_CloseWithoutSave() {

        openEditModal();

        initialStatus = getStatus();

        selectStatus("Đang giao hàng");

        // đóng modal (không lưu)
        driver.findElement(By.cssSelector(".ant-modal-close")).click();

        goToOrderPage();
        openEditModal();

        String after = getStatus();
        Assert.assertEquals(after, initialStatus);
    }

    // UD-3
    @Test(priority = 3)
    public void UD_3_NoStatusSelected() {

        openEditModal();

        clickUpdateButton();

        String toast = getToast();

        Assert.assertTrue(toast.contains("Vui lòng") || toast.contains("chọn trạng thái"));
    }

    // UD-4
    @Test(priority = 4)
    public void UD_4_CheckDropdown() {

        openEditModal();

        WebElement selectBox = driver.findElement(
                By.xpath("//div[contains(@class,'ant-modal')]//div[contains(@class,'ant-select')]")
        );

        selectBox.click();

        boolean visible = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-select-dropdown')]")
        )).isDisplayed();

        Assert.assertTrue(visible);
    }

    // UD-5
    @Test(priority = 5)
    public void UD_5_SelectByKeyboard() {

        openEditModal();

        WebElement selectBox = driver.findElement(
                By.xpath("//div[contains(@class,'ant-modal')]//div[contains(@class,'ant-select')]")
        );

        selectBox.click();
        selectBox.sendKeys(Keys.ARROW_DOWN);
        selectBox.sendKeys(Keys.ENTER);

        Assert.assertTrue(getStatus().length() > 0);
    }

    // UD-6
    @Test(priority = 6)
    public void UD_6_UpdateMultipleTimes() {

        openEditModal();

        selectStatus("Đang giao hàng");
        clickUpdateButton();

        driver.navigate().refresh();
        goToOrderPage();
        openEditModal();

        selectStatus("Đã giao hàng");
        clickUpdateButton();

        String finalStatus = getStatus();

        Assert.assertEquals(finalStatus, "Đã giao hàng");
    }

    // UD-7
    @Test(priority = 7)
    public void UD_7_CheckNoSaveButton() {

        openEditModal();

        boolean hasUpdateBtn = driver.findElements(
                By.xpath("//button[contains(.,'Cập nhật')]")
        ).size() > 0;

        Assert.assertTrue(hasUpdateBtn, "Không có nút cập nhật nhưng hệ thống vẫn chạy");
    }

    // ===== CLEAN =====
    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}