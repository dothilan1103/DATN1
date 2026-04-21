package Tests;

import Common.LoginSteps;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class Logout {

    WebDriver driver;
    WebDriverWait wait;
    LoginSteps loginSteps;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://161.248.4.185:8081/login");

        loginSteps = new LoginSteps(driver);

        // 👉 Login trước
        loginSteps.login("admin@gmail.com", "123456");

        // 👉 Chờ vào trang admin
        wait.until(ExpectedConditions.urlContains("admin"));
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    // ===== LOCATOR =====
    By userIcon = By.xpath("//div[contains(@class,'ant-dropdown-trigger')]");
    By logoutBtn = By.xpath("//span[contains(text(),'Đăng xuất')]");

    // ===== TEST 1: Logout thành công =====
    @Test
    public void testLogoutSuccess() {

        // Click icon user
        WebElement icon = wait.until(ExpectedConditions.elementToBeClickable(userIcon));
        icon.click();

        // Click logout
        WebElement logout = wait.until(ExpectedConditions.elementToBeClickable(logoutBtn));
        logout.click();

        // Verify quay về login
        wait.until(ExpectedConditions.urlContains("login"));

        Assert.assertTrue(driver.getCurrentUrl().contains("login"),
                "Không logout về trang login");
    }

    // ===== TEST 2: Logout xong không vào lại admin được =====
    @Test
    public void testCannotAccessAdminAfterLogout() {

        driver.findElement(userIcon).click();
        driver.findElement(logoutBtn).click();

        wait.until(ExpectedConditions.urlContains("login"));

        // Thử truy cập lại admin
        driver.get("http://161.248.4.185:8081/admin");

        // Phải bị đá về login
        Assert.assertTrue(driver.getCurrentUrl().contains("login"),
                "Vẫn vào được admin sau khi logout → lỗi session");
    }

    // ===== TEST 3: Dropdown không mở =====
    @Test
    public void testUserDropdownDisplay() {

        WebElement icon = wait.until(ExpectedConditions.visibilityOfElementLocated(userIcon));
        icon.click();

        WebElement logout = wait.until(ExpectedConditions.visibilityOfElementLocated(logoutBtn));

        Assert.assertTrue(logout.isDisplayed(), "Dropdown không hiển thị");
    }

    // ===== TEST 4: Click bằng JS nếu bị lỗi =====
    @Test
    public void testLogoutByJSClick() {

        WebElement icon = wait.until(ExpectedConditions.presenceOfElementLocated(userIcon));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", icon);

        WebElement logout = wait.until(ExpectedConditions.presenceOfElementLocated(logoutBtn));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", logout);

        wait.until(ExpectedConditions.urlContains("login"));

        Assert.assertTrue(driver.getCurrentUrl().contains("login"));
    }
}