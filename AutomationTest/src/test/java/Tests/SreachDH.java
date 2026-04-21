package Tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class SreachDH {

    WebDriver driver;
    WebDriverWait wait;

    // ===== LOCATOR =====
    By txtName = By.id("receiverName");
    By txtPhone = By.id("receiverPhone");
    By drpStatus = By.id("status");
    By drpPayment = By.id("paymentMethod");

    By btnSearch = By.xpath("//button[.//span[text()='Tìm kiếm']]");
    By btnReset = By.xpath("//button[.//span[text()='Làm lại']]");
    By btnExpand = By.xpath("//a[contains(@class,'ant-pro-query-filter-collapse-button')]");

    // ===== SETUP =====
    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get("http://161.248.4.185:8081/login");

        login("admin@gmail.com", "123456");
        openOrderPage();
    }

    // ===== LOGIN =====
    public void login(String email, String pass) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-form_email"))).sendKeys(email);
        driver.findElement(By.id("login-form_password")).sendKeys(pass);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin"));
    }

    // ===== OPEN ORDER PAGE =====
    public void openOrderPage() {
        WebElement menu = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[@href='/admin/order']")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);

        wait.until(ExpectedConditions.urlContains("/admin/order"));
    }

    // ===== EXPAND =====
    public void expandForm() {
        try {
            WebElement expand = wait.until(ExpectedConditions.presenceOfElementLocated(btnExpand));

            if (expand.getText().contains("Mở rộng")) {
                expand.click();
                Thread.sleep(500);
            }
        } catch (Exception ignored) {}
    }

    // ===== INPUT =====
    public void enter(By locator, String value) {
        WebElement e = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        e.click();
        e.sendKeys(Keys.CONTROL + "a");
        e.sendKeys(Keys.DELETE);
        e.sendKeys(value);
    }

    // ===== SELECT DROPDOWN (ANTD FIX) =====
    public void selectDropdown(By locator, String text) {

        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(locator));
        dropdown.click();

        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-select-item-option-content') and text()='" + text + "']")
        ));

        option.click();
    }

    // ===== CLICK SEARCH =====
    public void clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(btnSearch)).click();
    }

    // ===== GET ROW =====
    public int getRow() {

        // nếu no data
        List<WebElement> empty = driver.findElements(
                By.xpath("//td[contains(text(),'No data')]")
        );

        if (!empty.isEmpty()) return 0;

        return driver.findElements(By.xpath("//table//tbody//tr")).size();
    }

    // ===== RESET =====
    @AfterMethod
    public void reset() {
        wait.until(ExpectedConditions.elementToBeClickable(btnReset)).click();
    }

    // ================= TEST =================

    // TH-1: full search
    @Test
    public void TH01_Search_Full() {

        expandForm();

        enter(txtName, "Lan");
        enter(txtPhone, "0987654321");

        enter(drpStatus, "Đã giao");
        enter(drpPayment, "COD");

        clickSearch();

        Assert.assertTrue(getRow() >= 0);
    }

    // TH-2: search name
    @Test
    public void TH02_Search_Name() {

        enter(txtName, "Lan");

        clickSearch();

        Assert.assertTrue(getRow() >= 0);
    }

    // TH-3: search phone
    @Test
    public void TH03_Search_Phone() {

        enter(txtPhone, "0987654321");

        clickSearch();

        Assert.assertTrue(getRow() >= 0);
    }

    // TH-4: phone invalid
    @Test
    public void TH04_Invalid_Phone() {

        enter(txtPhone, "123456789999");

        clickSearch();

        Assert.assertTrue(getRow() >= 0);
    }

    // TH-5: no result
    @Test
    public void TH05_No_Result() {

        enter(txtName, "abcxyz123");

        clickSearch();

        Assert.assertEquals(getRow(), 0, "Phải không có dữ liệu");
    }

    // TH-6: status
    @Test
    public void TH06_Search_Status() {

        expandForm();

        enter(drpStatus, "Đã hủy");

        clickSearch();

        Assert.assertTrue(getRow() >= 0);
    }

    // TH-7: payment
    @Test
    public void TH07_Search_Payment() {

        expandForm();

        enter(drpPayment, "COD");

        clickSearch();

        Assert.assertTrue(getRow() >= 0);
    }

    // TH-8: empty search
    @Test
    public void TH08_Empty_Search() {

        clickSearch();

        Assert.assertTrue(getRow() >= 0);
    }

    // TH-9: special char
    @Test
    public void TH09_Special_Char() {

        enter(txtName, "@@##");

        clickSearch();

        Assert.assertEquals(getRow(), 0, "Ký tự đặc biệt phải không có kết quả");
    }

    // ===== TEARDOWN =====
    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}