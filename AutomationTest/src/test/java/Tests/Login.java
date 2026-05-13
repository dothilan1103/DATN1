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
        driver.get("http://localhost:5173/login");

        loginSteps = new LoginSteps(driver);
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    // ================= TEST =================


    // DN-1: login thành công
    @Test
    public void DN2_Login_Success() {
        loginSteps.login("admin@gmail.com", "123456");

        wait.until(ExpectedConditions.urlContains("admin"));

        Assert.assertTrue(driver.getCurrentUrl().contains("admin"));
    }

    // DN-2: sai username
    @Test
    public void DN3_Wrong_Username() {

        loginSteps.login("sai_user@gmail.com", "123456");

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("login"),
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(text(),'Bad credentials')]"))
        ));

        String pageSource = driver.getPageSource().toLowerCase();

        Assert.assertTrue(
                pageSource.contains("bad credentials")
                        || pageSource.contains("không tồn tại")
                        || pageSource.contains("sai tài khoản"),
                "Không hiển thị lỗi đăng nhập sai username"
        );
    }

    // DN-3: sai password
    @Test
    public void DN4_Wrong_Password() {

        loginSteps.login("admin@gmail.com", "sai_pass");

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("login"),
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(text(),'Bad credentials')]"))
        ));

        String pageSource = driver.getPageSource().toLowerCase();

        Assert.assertTrue(
                pageSource.contains("bad credentials")
                        || pageSource.contains("sai mật khẩu"),
                "Không hiển thị lỗi sai password"
        );
    }

    // DN-4: bỏ trống email
    @Test
    public void DN5_Empty_Email() {

        driver.findElement(txtPassword).sendKeys("123456");
        driver.findElement(btnLogin).click();

        Assert.assertTrue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg))
                        .getText().toLowerCase().contains("email")
        );
    }

    // DN-5: bỏ trống password
    @Test
    public void DN6_Empty_Password() {

        driver.findElement(txtEmail).sendKeys("admin@gmail.com");
        driver.findElement(btnLogin).click();

        Assert.assertTrue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg))
                        .getText().toLowerCase().contains("mật khẩu")
        );
    }

    // DN-6: bỏ trống tất cả
    @Test
    public void DN7_Empty_All() {

        driver.findElement(btnLogin).click();

        Assert.assertTrue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg))
                        .isDisplayed()
        );
    }


    // DN-7: tài khoản chưa tồn tại
    @Test
    public void DN9_User_Not_Exist() {

        loginSteps.login("test123890@gmail.com", "123456");

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("login"),
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(text(),'không tồn tại')]"))
        ));

        String pageSource = driver.getPageSource().toLowerCase();

        Assert.assertTrue(
                pageSource.contains("không tồn tại")
                        || pageSource.contains("bad credentials")
                        || pageSource.contains("user not found"),
                "Không hiển thị lỗi tài khoản không tồn tại"
        );
    }

    // DN-8: hiển thị mật khẩu
    @Test
    public void DN8_Show_Password() {

        WebElement password = driver.findElement(txtPassword);
        password.sendKeys("123456");

        driver.findElement(iconShowPass).click();

        String type = password.getAttribute("type");

        Assert.assertEquals(type, "text");
    }
}

