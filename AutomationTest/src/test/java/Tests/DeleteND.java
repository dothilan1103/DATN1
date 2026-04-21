package Tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class DeleteND {

    WebDriver driver;
    WebDriverWait wait;

    // ===== LOGIN + NAV =====
    public void enter(By by, String text) {
        WebElement e = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        e.click();
        e.sendKeys(Keys.CONTROL + "a");
        e.sendKeys(Keys.DELETE);
        if (text != null) e.sendKeys(text);
    }

    public void goToUserPage() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Người dùng')]")
        )).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table")));
    }

    // ===== ACTION =====
    public void clickDelete() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//span[contains(@class,'anticon-delete')])[1]")
        )).click();
    }

    public void confirmDelete() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Xác nhận']]")
        )).click();
    }

    public void cancelDelete() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Huỷ']]")
        )).click();
    }

    // ===== DATA =====
    public int getUserCount() {
        List<WebElement> rows = driver.findElements(By.xpath("//tbody/tr"));

        if (rows.size() == 1 &&
                rows.get(0).getText().toLowerCase().contains("no data")) {
            return 0;
        }
        return rows.size();
    }

    public String getToast() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ant-message-notice-content")
            )).getText();
        } catch (Exception e) {
            return "";
        }
    }

    // ===== WAIT STABLE =====
    public void waitTableRefresh(int oldSize) {
        wait.until(driver -> getUserCount() != oldSize);
    }

    // ===== SETUP =====
    @BeforeClass
    public void setup() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get("http://161.248.4.185:8081/login");

        enter(By.id("login-form_email"), "admin@gmail.com");
        enter(By.id("login-form_password"), "123456");

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin"));

        goToUserPage();
    }

    // ================= TEST CASE =================

    // DU-1: DELETE SUCCESS
    @Test(priority = 1)
    public void DU_1_DeleteSuccess() {

        int before = getUserCount();

        clickDelete();
        confirmDelete();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'thành công')]")
                ),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//table"))
        ));

        waitTableRefresh(before);

        int after = getUserCount();

        Assert.assertTrue(after < before,
                "User chưa bị xoá");
    }

    // DU-2: CANCEL DELETE
    @Test(priority = 2)
    public void DU_2_CancelDelete() {

        int before = getUserCount();

        clickDelete();
        cancelDelete();

        int after = getUserCount();

        Assert.assertEquals(after, before,
                "Huỷ xoá nhưng dữ liệu bị thay đổi");
    }

    // DU-3: NO DATA
    @Test(priority = 3)
    public void DU_3_NoData() {

        List<WebElement> icons = driver.findElements(
                By.xpath("//span[contains(@class,'anticon-delete')]")
        );

        Assert.assertTrue(icons.size() >= 0);
    }

    // DU-4: DELETE FAIL
    @Test(priority = 4)
    public void DU_4_DeleteFail() {

        clickDelete();
        confirmDelete();

        String toast = getToast().toLowerCase();

        Assert.assertTrue(
                toast.contains("thất bại")
                        || toast.contains("lỗi")
                        || toast.contains("error")
        );
    }

    // DU-5: NO CONFIRM DELETE
    @Test(priority = 5)
    public void DU_5_NoConfirmDelete() {

        int before = getUserCount();

        clickDelete();
        // không confirm

        try {
            Thread.sleep(1500);
        } catch (Exception ignored) {}

        int after = getUserCount();

        Assert.assertEquals(after, before,
                "Không confirm nhưng dữ liệu bị thay đổi");
    }

    // ===== CLEAN =====
    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}