package Tests;

import Common.LoginSteps;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class Login {

    WebDriver driver;
    WebDriverWait wait;
    LoginSteps loginSteps;

    // ===== LOCATOR =====
    By txtEmail = By.id("login-form_email");
    By txtPassword = By.id("login-form_password");
    By btnLogin = By.xpath("//button[@type='submit']");
    By errorMsg = By.xpath("//div[contains(@class,'ant-form-item-explain-error')]");

    // icon show password (Ant Design)
    By iconShowPass = By.xpath("//span[contains(@class,'ant-input-password-icon')]");

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.manage().window().maximize();
        driver.get("http://161.248.4.185:8081/login");

        loginSteps = new LoginSteps(driver);
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    // ================= TEST =================

    // DN-1: mở trang login
    @Test
    public void DN1_Open_Login() {
        Assert.assertTrue(driver.getCurrentUrl().contains("login"));
    }

    // DN-2: login thành công
    @Test
    public void DN2_Login_Success() {
        loginSteps.login("admin@gmail.com", "123456");

        wait.until(ExpectedConditions.urlContains("admin"));

        Assert.assertTrue(driver.getCurrentUrl().contains("admin"));
    }

    // DN-3: sai username
    @Test
    public void DN3_Wrong_Username() {
        loginSteps.login("sai_user", "123456");

        Assert.assertTrue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg))
                        .getText().toLowerCase().contains("Bad credentials")
        );
    }

    // DN-4: sai password
    @Test
    public void DN4_Wrong_Password() {
        loginSteps.login("admin@gmail.com", "sai_pass");

        Assert.assertTrue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg))
                        .getText().toLowerCase().contains("Bad credentials")
        );
    }

    // DN-5: bỏ trống email
    @Test
    public void DN5_Empty_Email() {

        driver.findElement(txtPassword).sendKeys("123456");
        driver.findElement(btnLogin).click();

        Assert.assertTrue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg))
                        .getText().toLowerCase().contains("email")
        );
    }

    // DN-6: bỏ trống password
    @Test
    public void DN6_Empty_Password() {

        driver.findElement(txtEmail).sendKeys("admin@gmail.com");
        driver.findElement(btnLogin).click();

        Assert.assertTrue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg))
                        .getText().toLowerCase().contains("mật khẩu")
        );
    }

    // DN-7: bỏ trống tất cả
    @Test
    public void DN7_Empty_All() {

        driver.findElement(btnLogin).click();

        Assert.assertTrue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg))
                        .isDisplayed()
        );
    }

    // DN-8: email sai format
    @Test
    public void DN8_Invalid_Email() {

        driver.findElement(txtEmail).sendKeys("abc");

        // 👉 bắt buộc để trigger validate
        driver.findElement(txtPassword).click();

        driver.findElement(txtPassword).sendKeys("123456");
        driver.findElement(btnLogin).click();

        String actual = wait.until(
                ExpectedConditions.visibilityOfElementLocated(errorMsg)
        ).getText();

        System.out.println("Error: " + actual);

        Assert.assertTrue(
                actual.toLowerCase().contains("email"),
                "Sai message: " + actual
        );
    }

    // DN-9: tài khoản chưa tồn tại
    @Test
    public void DN9_User_Not_Exist() {

        loginSteps.login("test123@gmail.com", "123456");

        Assert.assertTrue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg))
                        .getText().toLowerCase().contains("không tồn tại")
        );
    }

    // DN-10: hiển thị mật khẩu
    @Test
    public void DN10_Show_Password() {

        WebElement password = driver.findElement(txtPassword);
        password.sendKeys("123456");

        driver.findElement(iconShowPass).click();

        String type = password.getAttribute("type");

        Assert.assertEquals(type, "text");
    }

    // DN-11: redirect sau login
    @Test
    public void DN11_Redirect_After_Login() {

        loginSteps.login("admin@gmail.com", "123456");

        wait.until(ExpectedConditions.urlContains("admin"));

        Assert.assertTrue(
                driver.getCurrentUrl().contains("admin"),
                "Không redirect đúng sau login"
        );
    }
}