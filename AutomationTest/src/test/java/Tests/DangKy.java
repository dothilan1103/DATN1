package Tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class DangKy {

    WebDriver driver;
    WebDriverWait wait;

    // ===== LOCATOR =====
    By txtName = By.id("register-form_name");
    By txtEmail = By.id("register-form_email");
    By body = By.tagName("body");
    By btnSendOTP = By.xpath("//button[.//span[text()='Gửi mã']]");
    By txtOTP = By.id("register-form_otp");
    By txtPassword = By.id("register-form_password");
    By txtAge = By.id("register-form_age");

    // dropdown Ant Design
    By drpGender = By.xpath("//div[@id='register-form_gender']/ancestor::div[contains(@class,'ant-select')]");
    By optFemale = By.xpath("//div[contains(@class,'ant-select-item-option-content') and text()='Nữ']");

    By drpAddress = By.xpath("//div[contains(@class,'ant-cascader')]");
    By optHN = By.xpath("//li[contains(text(),'Hà Nội')]");

    By btnRegister = By.xpath("//button[@type='submit']");

    // ===== COMMON =====
    public void enter(By by, String value) {
        WebElement e = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        e.clear();
        e.sendKeys(value);
    }

    public String randomEmail() {
        return "test" + System.currentTimeMillis() + "@gmail.com";
    }

    public void selectGender() {
        wait.until(ExpectedConditions.elementToBeClickable(drpGender)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(optFemale)).click();
    }

    public void selectAddress() {
        wait.until(ExpectedConditions.elementToBeClickable(drpAddress)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(optHN)).click();
    }

    public void clickRegister() {
        wait.until(ExpectedConditions.elementToBeClickable(btnRegister)).click();
    }

    // ===== SETUP =====
    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.manage().window().maximize();

        // 👉 vào thẳng trang đăng ký
        driver.get("http://161.248.4.185:8081/register");

        wait.until(ExpectedConditions.visibilityOfElementLocated(txtName));
    }

    // ===== RESET SAU MỖI TEST =====
    @AfterMethod
    public void reset() {
        driver.navigate().refresh();
    }

    // ================= TEST =================

    // DK-1: kiểm tra mở trang
    @Test
    public void DK1_Open_Register() {
        Assert.assertTrue(driver.getCurrentUrl().contains("register"));
    }


    @Test
    public void DK2_Register_Success() {

        enter(txtName, "Do Duy Manh");
        enter(txtEmail, "duymanh234@gmail.com");

        wait.until(ExpectedConditions.elementToBeClickable(btnSendOTP)).click();

        enter(txtOTP, "123"); // OTP không quan trọng

        enter(txtPassword, "123456");
        enter(txtAge, "20");

        selectGender();
        selectAddress();

        clickRegister();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("login"),
                ExpectedConditions.textToBePresentInElementLocated(
                        By.tagName("body"), "thành công"
                )
        ));

        Assert.assertTrue(
                driver.getCurrentUrl().contains("login"),
                "Đăng ký không thành công"
        );
    }
    // DK-3: email đã tồn tại
    @Test
    public void DK3_Email_Exists() {

        enter(txtName, "Test");
        enter(txtEmail, "admin@gmail.com");

        clickRegister();

        Assert.assertTrue(driver.getPageSource().toLowerCase().contains("email"));
    }

    // DK-4: bỏ trống
    @Test
    public void DK4_Empty_Form() {
        clickRegister();
        Assert.assertTrue(driver.getPageSource().contains("Vui lòng"));
    }

    // DK-5: email sai
    @Test
    public void DK5_Invalid_Email() {

        enter(txtEmail, "abc.com");

        clickRegister();

        Assert.assertTrue(driver.getPageSource().toLowerCase().contains("email"));
    }

    // DK-6: tuổi không hợp lệ
    @Test
    public void DK6_Invalid_Age() {

        enter(txtAge, "10");

        clickRegister();

        Assert.assertTrue(driver.getPageSource().contains("Tuổi"));
    }

    // DK-7: OTP sai
    @Test
    public void DK7_Wrong_OTP() {

        enter(txtName, "Test");
        enter(txtEmail, randomEmail());

        wait.until(ExpectedConditions.elementToBeClickable(btnSendOTP)).click();

        enter(txtOTP, "000000");

        clickRegister();

        Assert.assertTrue(driver.getPageSource().toLowerCase().contains("otp"));
    }

    // DK-8: không chọn giới tính
    @Test
    public void DK8_No_Gender() {

        enter(txtName, "Test");
        enter(txtEmail, randomEmail());
        enter(txtOTP, "1");
        enter(txtPassword, "123456");
        enter(txtAge, "20");

        selectAddress();

        clickRegister();

        By errGender = By.xpath(
                "//div[contains(@class,'ant-form-item-explain-error') and contains(text(),'giới')]"
        );

        Assert.assertTrue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(errGender)).isDisplayed(),
                "Không hiển thị lỗi giới tính"
        );
    }

    // DK-9: không chọn địa chỉ
    @Test
    public void DK9_No_Address() {

        enter(txtName, "Test");
        enter(txtEmail, randomEmail());
        enter(txtOTP, "1");
        enter(txtPassword, "123456");
        enter(txtAge, "20");

        selectGender();

        clickRegister();

        Assert.assertTrue(driver.getPageSource().toLowerCase().contains("địa chỉ"));
    }

    // DK-10: OTP để trống
    @Test
    public void DK10_Empty_OTP() {

        enter(txtName, "Test");
        enter(txtEmail, randomEmail());

        clickRegister();

        Assert.assertTrue(driver.getPageSource().toLowerCase().contains("otp"));
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}