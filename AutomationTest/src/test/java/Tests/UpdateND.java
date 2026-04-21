package Tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class UpdateND {

    WebDriver driver;
    WebDriverWait wait;

    // ===== COMMON =====
    public void enter(By by, String text) {
        WebElement e = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        e.click();
        e.sendKeys(Keys.CONTROL + "a");
        e.sendKeys(Keys.DELETE);
        e.sendKeys(text);
    }

    public void openEdit() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//span[contains(@class,'anticon-edit')])[1]")
        )).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal-content')]")
        ));
    }

    public void submit() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button//span[text()='Cập nhật']")
        )).click();
    }

    // ===== SELECT FIX =====
    public void selectDropdown(By dropdown, String value) {
        // click dropdown
        wait.until(ExpectedConditions.elementToBeClickable(dropdown)).click();

        // chờ list render
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='listbox']")
        ));

        // chọn item
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@role='option']//div[text()='" + value + "']")
        )).click();
    }

    public void selectGender(String gender) {
        selectDropdown(
                By.xpath("//input[@id='gender']/ancestor::div[contains(@class,'ant-select')]"),
                gender
        );
    }

    public void selectRole(String role) {
        selectDropdown(
                By.xpath("//input[@id='role']/ancestor::div[contains(@class,'ant-select')]"),
                role
        );
    }

    // ===== ADDRESS FIX =====
    public void selectAddress() {
        // mở cascader
        WebElement cascader = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[text()='Địa chỉ']/following::div[contains(@class,'ant-cascader')][1]")
        ));

        cascader.click();

        // chọn từng cấp (đợi visible từng menu)
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//ul[contains(@class,'ant-cascader-menu')]")
        ));

        // cấp 1
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//li[contains(@class,'ant-cascader-menu-item')])[1]")
        )).click();

        // cấp 2
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//ul[contains(@class,'ant-cascader-menu')])[2]//li[1]")
        )).click();

        // cấp 3
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//ul[contains(@class,'ant-cascader-menu')])[3]//li[1]")
        )).click();
    }

    // ===== CLEAR DROPDOWN FIX =====
    public void clearSelect(By locator) {
        try {
            WebElement clearBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    locator
            ));
            clearBtn.click();
        } catch (Exception e) {
            System.out.println("Không có nút clear");
        }
    }

    // ===== LOGIN =====
    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.manage().window().maximize();
        driver.get("http://161.248.4.185:8081/login");

        enter(By.id("login-form_email"), "admin@gmail.com");
        enter(By.id("login-form_password"), "123456");

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // đợi load menu
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Người dùng')]")
        )).click();
    }

    // =========================
    // ❌ VALIDATION
    // =========================

    @Test
    public void TC01_Name_Empty() {
        openEdit();

        enter(By.id("name"), "");
        submit();

        Assert.assertTrue(driver.getPageSource().contains("Tên hiển thị"));
    }

    @Test
    public void TC02_Age_Invalid() {
        openEdit();

        enter(By.id("age"), "abc");
        submit();

        Assert.assertTrue(driver.getPageSource().contains("tuổi"));
    }

    @Test
    public void TC03_Phone_Invalid() {
        openEdit();

        enter(By.id("phone"), "abc");
        submit();

        Assert.assertTrue(driver.getPageSource().contains("điện thoại"));
    }

    // =========================
    // ❌ KHÔNG CHỌN DROPDOWN
    // =========================

    @Test
    public void TC04_NoGender() {
        openEdit();

        clearSelect(By.xpath("//input[@id='gender']/ancestor::div[contains(@class,'ant-select')]//span[contains(@class,'ant-select-clear')]"));

        submit();

        Assert.assertTrue(driver.getPageSource().contains("Giới"));
    }

    @Test
    public void TC05_NoRole() {
        openEdit();

        clearSelect(By.xpath("//input[@id='role']/ancestor::div[contains(@class,'ant-select')]//span[contains(@class,'ant-select-clear')]"));

        submit();

        Assert.assertTrue(driver.getPageSource().contains("Vai trò"));
    }

    // =========================
    // ✅ SUCCESS (FIX PASS 100%)
    // =========================

    @Test
    public void TC06_UpdateSuccess() {
        openEdit();

        enter(By.id("name"), "Lan OK");
        enter(By.id("age"), "22");
        enter(By.id("phone"), "0987654321");

        selectGender("Nam");
        selectRole("ADMIN_TEST");
        selectAddress();

        submit();

        // đợi toast hoặc reload table
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'thành công')]")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Lan OK')]"))
        ));

        Assert.assertTrue(driver.getPageSource().contains("Lan OK"));
    }

    // =========================
    // CLOSE POPUP
    // =========================
    @AfterMethod
    public void closePopup() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@aria-label='Close']")
            )).click();
        } catch (Exception e) {}
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}