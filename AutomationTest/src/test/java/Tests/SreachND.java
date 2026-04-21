package Tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class SreachND {

    WebDriver driver;
    WebDriverWait wait;

    // ===== COMMON INPUT =====
    public void enter(By by, String text) {
        WebElement e = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        e.clear();
        e.sendKeys(text);
    }

    // ===== SEARCH =====
    public void search(String email, String name) {

        By emailInput = By.xpath("//form//input[@id='email']");
        By nameInput = By.xpath("//form//input[@id='name']");

        if (email != null) {
            enter(emailInput, email);
        }

        if (name != null) {
            enter(nameInput, name);
        }

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//form//button[.//span[text()='Tìm kiếm']]")
        )).click();
    }

    // ===== GET RESULT COUNT =====
    public int getResultCount() {

        List<WebElement> rows = driver.findElements(By.xpath("//tbody/tr"));

        if (rows.size() == 1 &&
                rows.get(0).getText().toLowerCase().contains("no data")) {
            return 0;
        }

        return rows.size();
    }

    // ===== RESET =====
    public void reset() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//form//button[.//span[text()='Làm lại']]")
            )).click();
        } catch (Exception ignored) {}
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

        // vào menu user
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Người dùng')]")
        )).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table")));
    }

    // ================= TEST CASE =================

    // TS-1: Email hợp lệ
    @Test(priority = 1)
    public void TS_1_SearchByEmail() {
        search("admin@gmail.com", null);
        Assert.assertTrue(getResultCount() > 0);
    }

    // TS-2: Tên hợp lệ
    @Test(priority = 2)
    public void TS_2_SearchByName() {
        search(null, "admin");
        Assert.assertTrue(getResultCount() > 0);
    }

    // TS-3: Không có kết quả
    @Test(priority = 3)
    public void TS_3_NoResult() {
        search("abcxyz@gmail.com", null);
        Assert.assertEquals(getResultCount(), 0);
    }

    // TS-4: Email sai + Name đúng
    @Test(priority = 4)
    public void TS_4_EmailWrong_NameCorrect() {
        search("wrong@gmail.com", "admin");
        Assert.assertEquals(getResultCount(), 0);
    }

    // TS-5: Email đúng + Name sai
    @Test(priority = 5)
    public void TS_5_EmailCorrect_NameWrong() {
        search("admin@gmail.com", "khongtontai");
        Assert.assertEquals(getResultCount(), 0);
    }

    // TS-6: Email + Name đều đúng
    @Test(priority = 6)
    public void TS_6_BothCorrect() {
        search("admin@gmail.com", "admin");

        int count = getResultCount();
        Assert.assertTrue(count > 0);
    }

    // TS-7: ký tự đặc biệt
    @Test(priority = 7)
    public void TS_7_SpecialCharacter() {
        search("@@@###", null);
        Assert.assertEquals(getResultCount(), 0);
    }

    // TS-8: không nhập gì
    @Test(priority = 8)
    public void TS_8_EmptySearch() {
        search(null, null);

        int count = getResultCount();
        Assert.assertTrue(count >= 0);
    }

    // ===== CLEAN =====
    @AfterMethod
    public void afterTest() {
        reset();
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}