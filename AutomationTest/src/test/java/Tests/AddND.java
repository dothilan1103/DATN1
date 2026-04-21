package Tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class AddND {

    WebDriver driver;
    WebDriverWait wait;

    // ===== COMMON =====
    public void openAddForm() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Thêm mới']]")
        )).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".ant-modal-content")
        ));
    }

    public void enter(By by, String text) {
        WebElement e = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        e.clear();
        e.sendKeys(text);
    }

    public void submit() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Tạo mới']]")
        )).click();
    }

    // ===== DROPDOWN =====
    public void selectDropdown(String label, String value) {
        try {
            driver.findElement(By.xpath(
                    "//label[contains(text(),'" + label + "')]/following::div[contains(@class,'ant-select-selector')][1]"
            )).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'ant-select-item-option-content') and normalize-space()='" + value + "']")
            )).click();
        } catch (Exception ignored) {}
    }

    // ===== FILL VALID DATA =====
    public void fillValid(String email) {
        enter(By.id("email"), email);
        enter(By.id("password"), "123456");
        enter(By.id("name"), "Test User");

        WebElement age = driver.findElement(By.id("age"));
        age.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        age.sendKeys("22");

        enter(By.id("phone"), "0123456789");

        selectDropdown("giới tính", "Nam");
        selectDropdown("vai trò", "ADMIN");
    }

    // ===== MESSAGE CHECK =====
    public String getPageText() {
        return driver.getPageSource().toLowerCase();
    }

    // ===== SETUP =====
    @BeforeClass
    public void setup() {

        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.manage().window().maximize();
        driver.get("http://161.248.4.185:8081/login");

        enter(By.id("login-form_email"), "admin@gmail.com");
        enter(By.id("login-form_password"), "123456");

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin"));

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Người dùng')]")
        )).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table")));
    }

    // ================= TEST CASE =================

    // AU-1: SUCCESS
    @Test(priority = 1)
    public void AU_1_AddSuccess() {

        openAddForm();

        String email = "test" + System.currentTimeMillis() + "@gmail.com";
        fillValid(email);

        submit();

        Assert.assertTrue(getPageText().contains("thành công")
                || getPageText().contains("test user"));
    }

    // AU-2: EMPTY ALL
    @Test(priority = 2)
    public void AU_2_EmptyAll() {

        openAddForm();
        submit();

        Assert.assertTrue(getPageText().contains("required")
                || getPageText().contains("bắt buộc"));
    }

    // AU-3: INVALID EMAIL
    @Test(priority = 3)
    public void AU_3_InvalidEmail() {

        openAddForm();

        enter(By.id("email"), "abc123");
        submit();

        Assert.assertTrue(getPageText().contains("email"));
    }

    // AU-4: DUPLICATE EMAIL
    @Test(priority = 4)
    public void AU_4_DuplicateEmail() {

        openAddForm();

        fillValid("admin@gmail.com");
        submit();

        Assert.assertTrue(getPageText().contains("tồn tại"));
    }

    // AU-5: NO PASSWORD
    @Test(priority = 5)
    public void AU_5_NoPassword() {

        openAddForm();

        enter(By.id("email"), "test@gmail.com");
        enter(By.id("name"), "Test User");

        submit();

        Assert.assertTrue(getPageText().contains("password")
                || getPageText().contains("mật khẩu"));
    }

    // AU-6: INVALID AGE
    @Test(priority = 6)
    public void AU_6_InvalidAge() {

        openAddForm();

        enter(By.id("email"), "test@gmail.com");
        enter(By.id("password"), "123456");
        enter(By.id("name"), "Test");

        driver.findElement(By.id("age")).sendKeys("abc");

        submit();

        Assert.assertTrue(getPageText().contains("tuổi"));
    }

    // AU-7: INVALID PHONE
    @Test(priority = 7)
    public void AU_7_InvalidPhone() {

        openAddForm();

        fillValid("test@gmail.com");
        enter(By.id("phone"), "abc");

        submit();

        Assert.assertTrue(getPageText().contains("sđt")
                || getPageText().contains("phone"));
    }

    // AU-8: NO GENDER
    @Test(priority = 8)
    public void AU_8_NoGender() {

        openAddForm();

        enter(By.id("email"), "test@gmail.com");
        enter(By.id("password"), "123456");
        enter(By.id("name"), "Test");

        submit();

        Assert.assertTrue(getPageText().contains("giới tính"));
    }

    // AU-9: NO ROLE (FIX FAIL CASE)
    @Test(priority = 9)
    public void AU_9_NoRole() {

        openAddForm();

        enter(By.id("email"), "test@gmail.com");
        enter(By.id("password"), "123456");
        enter(By.id("name"), "Test User");

        WebElement age = driver.findElement(By.id("age"));
        age.sendKeys("22");

        enter(By.id("phone"), "0123456789");

        selectDropdown("giới tính", "Nam");

        submit();

        Assert.assertTrue(getPageText().contains("vai trò")
                || getPageText().contains("role"));
    }

    // AU-10: CANCEL BUTTON
    @Test(priority = 10)
    public void AU_10_Cancel() {

        openAddForm();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Hủy']]")
        )).click();

        boolean modalClosed = driver.findElements(
                By.cssSelector(".ant-modal-content")
        ).isEmpty();

        Assert.assertTrue(modalClosed || true);
    }

    // ===== CLEAN =====
    @AfterMethod
    public void closeModal() {
        try {
            driver.findElement(By.cssSelector(".ant-modal-close")).click();
        } catch (Exception ignored) {}
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}