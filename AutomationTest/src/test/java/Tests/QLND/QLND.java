package Tests.QLND;

import Utils.ExcelUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class QLND {

    WebDriver driver;
    WebDriverWait wait;
    WebDriverWait shortWait;

    // =====================================================
    // SLEEP
    // =====================================================

    public void pause(int second) {
        try {
            Thread.sleep(second * 300L);
        } catch (Exception ignored) {
        }
    }

    // =====================================================
    // COMMON
    // =====================================================

    public void enter(By by, String text) {
        WebElement e = wait.until(
                ExpectedConditions.visibilityOfElementLocated(by)
        );
        e.click();
        pause(1);
        e.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        pause(1);
        e.sendKeys(Keys.DELETE);
        pause(1);
        if (text != null && !text.isEmpty()) {
            e.sendKeys(text);
        }
        pause(1);
    }

    public String pageText() {
        return driver.getPageSource().toLowerCase();
    }

    public int getRowCount() {
        List<WebElement> rows = driver.findElements(By.xpath("//tbody/tr"));
        if (rows.size() == 1 &&
                rows.get(0).getText().toLowerCase().contains("no data")) {
            return 0;
        }
        return rows.size();
    }

    // Đóng toast/notification popup (không phải modal form)
    public void closePopup() {
        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@aria-label='Close']")
            )).click();
            pause(1);
        } catch (Exception ignored) {
        }
    }

    // Chỉ đóng modal form (Thêm mới / Cập nhật) — XPath giới hạn trong .ant-modal
    public void closeModalIfOpen() {
        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class,'ant-modal')]//button[.//span[text()='Hủy' or text()='Huỷ']]")
            )).click();
            pause(2);

            // Chờ modal biến mất hoàn toàn
            shortWait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'ant-modal')]")
            ));
        } catch (Exception ignored) {
        }
    }

    // Mở form Thêm mới — đảm bảo modal cũ đã đóng, form mới đã hiện
    public void clickThemMoi() {

        // Bước 1: Nếu modal đang mở thì đóng trước
        closeModalIfOpen();
        pause(1);

        // Bước 2: Click nút Thêm mới
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Thêm mới']]")
        )).click();

        pause(1);

        // Bước 3: Chờ field EMAIL trong modal hiện ra (không phải field email ở search form)
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal')]//input[@id='email']")
        ));
    }

    // Mở form Edit
    public void clickEdit() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//span[contains(@class,'anticon-edit')])[1]")
        )).click();
        pause(2);
        // Chờ field name trong modal hiện ra
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal')]//input[@id='name']")
        ));
    }

    // =====================================================
    // SETUP
    // =====================================================

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));

        driver.manage().window().maximize();
        driver.get("http://localhost:5173/login");
        pause(2);

        enter(By.id("login-form_email"), "admin@gmail.com");
        enter(By.id("login-form_password"), "123456");

        driver.findElement(By.xpath("//button[@type='submit']")).click();
        pause(3);

        wait.until(ExpectedConditions.urlContains("/admin"));

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Người dùng')]")
        )).click();

        pause(2);

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//table")
        ));
    }

    // =====================================================
    // DATA PROVIDER
    // =====================================================

    @DataProvider(name = "Add")
    public Object[][] App() {
        return ExcelUtils.getData("Add");
    }

    @DataProvider(name = "UP")
    public Object[][] UP() {
        return ExcelUtils.getData("UP");
    }

    // =====================================================
    // COMMON ACTION
    // =====================================================

    public void selectDropdown(String id, String value) {

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@id='" + id + "']/ancestor::div[contains(@class,'ant-select')]")
        )).click();

        pause(1);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-select-item') and .//text()='" + value + "']")
        )).click();

        pause(1);
    }

    public void selectAddress(String city, String district, String ward) {

        // Click vào cascader trigger
        WebElement cascaderSelector = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-modal')]//div[contains(@class,'ant-cascader')]//div[contains(@class,'ant-select-selector')]")
        ));
        cascaderSelector.click();
        pause(2);

        // Chọn tỉnh/thành — dùng ant-cascader-menu-item thay vì ant-select-item
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-cascader-menus')]" +
                        "//ul[1]/li[contains(@class,'ant-cascader-menu-item') and contains(.,'" + city + "')]")
        )).click();
        pause(2);

        // Chọn quận/huyện — cột thứ 2
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-cascader-menus')]" +
                        "//ul[2]/li[contains(@class,'ant-cascader-menu-item') and contains(.,'" + district + "')]")
        )).click();
        pause(2);

        // Chọn phường/xã — cột thứ 3
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-cascader-menus')]" +
                        "//ul[3]/li[contains(@class,'ant-cascader-menu-item') and contains(.,'" + ward + "')]")
        )).click();
        pause(1);
    }
    public void resetSearch() {
        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//form//button[.//span[text()='Làm lại']]")
            )).click();
            pause(1);
        } catch (Exception ignored) {
        }
    }

    // =====================================================
    // ADD USER
    // =====================================================

    // Case 1: Thêm mới thành công
    @Test(dataProvider = "Add", priority = 1)
    public void AU_1_AddSuccess(
            String email, String password, String name,
            String age, String phone, String gender,
            String role, String city, String district, String ward
    ) {
        // clickThemMoi() đã xử lý: đóng modal cũ → click Thêm mới → chờ form hiện
        clickThemMoi();

        // Dùng XPath trong modal để tránh nhầm với input search ngoài trang
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='email']"), email);
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='password']"), password);
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='name']"), name);
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='age']"), age);
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='phone']"), phone);

        selectDropdown("gender", gender);
        selectDropdown("role", role);
        selectAddress(city, district, ward);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-modal')]//button[.//span[text()='Tạo mới']]")
        )).click();

        pause(2);

        Assert.assertTrue(pageText().contains("thành công"));
    }
// case 2: không nhập trường dữ liệu
    @Test(priority = 2)
    public void AU_2_EmptyAll() {
        clickThemMoi();

        driver.findElement(
                By.xpath("//div[contains(@class,'ant-modal')]//button[.//span[text()='Tạo mới']]")
        ).click();
        pause(2);

        Assert.assertTrue(
                pageText().contains("required") || pageText().contains("bắt buộc")
        );
    }
// case 3: Sai định dạng email

    @Test(priority = 3)
    public void AU_3_InvalidEmail() {
        clickThemMoi();

        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='email']"), "abc123");

        driver.findElement(
                By.xpath("//div[contains(@class,'ant-modal')]//button[.//span[text()='Tạo mới']]")
        ).click();
        pause(2);

        Assert.assertTrue(pageText().contains("email"));
    }

// case 4: trùng email
@DataProvider(name = "Duplicate")
public Object[][] duplicate() {
    return ExcelUtils.getData("Duplicate");
}

@Test(dataProvider = "Duplicate", priority = 4)
public void AU_4_DuplicateEmail(
        String email, String password, String name,
        String age, String phone, String gender,
        String role, String city, String district, String ward
) {
    clickThemMoi();

    enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='email']"), email);
    enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='password']"), password);
    enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='name']"), name);
    enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='age']"), age);
    enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='phone']"), phone);

    selectDropdown("gender", gender);
    selectDropdown("role", role);
    selectAddress(city, district, ward);

    driver.findElement(
            By.xpath("//div[contains(@class,'ant-modal')]//button[.//span[text()='Tạo mới']]")
    ).click();
    pause(4);

    Assert.assertTrue(
            pageText().contains("tồn tại") || pageText().contains("duplicate") ||
                    pageText().contains("already") || pageText().contains("existed")
    );
}
// case 5: Không nhập password
    @Test(priority = 5)
    public void AU_5_EmptyPassword() {
        clickThemMoi();

        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='email']"), "test@gmail.com");
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='name']"), "Lan");

        driver.findElement(
                By.xpath("//div[contains(@class,'ant-modal')]//button[.//span[text()='Tạo mới']]")
        ).click();
        pause(2);

        Assert.assertTrue(
                pageText().contains("password") || pageText().contains("mật khẩu")
        );
    }

    @Test(priority = 6)
    public void AU_6_InvalidAge() {
        clickThemMoi();

        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='email']"), "test@gmail.com");
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='password']"), "123456");
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='name']"), "Lan");
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='age']"), "abc");

        driver.findElement(
                By.xpath("//div[contains(@class,'ant-modal')]//button[.//span[text()='Tạo mới']]")
        ).click();
        pause(2);

        Assert.assertTrue(
                pageText().contains("tuổi") || pageText().contains("age")
        );
    }

    @Test(priority = 7)
    public void AU_7_InvalidPhone() {
        clickThemMoi();

        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='email']"), "test@gmail.com");
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='password']"), "123456");
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='name']"), "Lan");
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='phone']"), "abcxyz");

        driver.findElement(
                By.xpath("//div[contains(@class,'ant-modal')]//button[.//span[text()='Tạo mới']]")
        ).click();
        pause(2);

        Assert.assertTrue(
                pageText().contains("phone") || pageText().contains("sđt")
        );
    }

    @Test(priority = 8)
    public void AU_8_NoGender() {
        clickThemMoi();

        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='email']"), "test@gmail.com");
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='password']"), "123456");
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='name']"), "Lan");

        driver.findElement(
                By.xpath("//div[contains(@class,'ant-modal')]//button[.//span[text()='Tạo mới']]")
        ).click();
        pause(2);

        Assert.assertTrue(pageText().contains("giới tính"));
    }

    @Test(priority = 9)
    public void AU_9_NoRole() {
        clickThemMoi();

        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='email']"), "test@gmail.com");
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='password']"), "123456");
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='name']"), "Lan");

        selectDropdown("gender", "Nam");

        driver.findElement(
                By.xpath("//div[contains(@class,'ant-modal')]//button[.//span[text()='Tạo mới']]")
        ).click();
        pause(2);

        Assert.assertTrue(
                pageText().contains("vai trò") || pageText().contains("role")
        );
    }

    @Test(priority = 10)
    public void AU_10_CancelAdd() {
        clickThemMoi();

        driver.findElement(
                By.xpath("//div[contains(@class,'ant-modal')]//button[.//span[text()='Hủy'] or .//span[text()='Huỷ']]")
        ).click();
        pause(2);

        // Verify modal đã đóng
        shortWait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal')]")
        ));

        Assert.assertTrue(true);
    }

    // =====================================================
    // SEARCH
    // =====================================================

    public void search(String email, String name) {
        if (email != null) {
            enter(By.xpath("//form//input[@id='email']"), email);
        }
        if (name != null) {
            enter(By.xpath("//form//input[@id='name']"), name);
        }
        driver.findElement(
                By.xpath("//form//button[.//span[text()='Tìm kiếm']]")
        ).click();
        pause(2);
    }

    @DataProvider(name = "Search")
    public Object[][] searchData() {
        return ExcelUtils.getData("Search");
    }

    @Test(dataProvider = "Search", priority = 11)
    public void TS_SearchCases(String email, String name, String expectedResult) {
        search(
                email.isEmpty() ? null : email,
                name.isEmpty() ? null : name
        );

        switch (expectedResult) {
            case "found"    -> Assert.assertTrue(getRowCount() > 0,
                    "Phải có kết quả với email=" + email + " name=" + name);
            case "notfound" -> Assert.assertEquals(getRowCount(), 0,
                    "Phải không có kết quả với email=" + email + " name=" + name);
            case "any"      -> Assert.assertTrue(getRowCount() >= 0);
        }
    }


    // =====================================================
    // UPDATE
    // =====================================================



    @Test(dataProvider = "UP", priority = 19)
    public void TC_UpdateCases(
            String name, String age, String phone,
            String gender, String role,
            String city, String district, String ward,
            String expectedResult
    ) {
        clickEdit();

        // Nhập name
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='name']"), name);

        // Nhập age nếu có
        if (!age.isEmpty()) {
            enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='age']"), age);
        }

        // Nhập phone nếu có
        if (!phone.isEmpty()) {
            enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='phone']"), phone);
        }

        // Chọn gender — nếu trống thì clear
        if (!gender.isEmpty()) {
            selectDropdown("gender", gender);
        } else {
            try {
                WebElement clearBtn = driver.findElement(
                        By.xpath("//div[contains(@class,'ant-modal')]//input[@id='gender']" +
                                "/ancestor::div[contains(@class,'ant-select')]" +
                                "//span[contains(@class,'ant-select-clear')]")
                );
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clearBtn);
            } catch (Exception ignored) {}
        }

        // Chọn role — nếu trống thì clear
        if (!role.isEmpty()) {
            selectDropdown("role", role);
        } else {
            try {
                WebElement clearBtn = driver.findElement(
                        By.xpath("//div[contains(@class,'ant-modal')]//input[@id='role']" +
                                "/ancestor::div[contains(@class,'ant-select')]" +
                                "//span[contains(@class,'ant-select-clear')]")
                );
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clearBtn);
            } catch (Exception ignored) {}
        }

        // Chọn địa chỉ nếu có
        if (!city.isEmpty() && !district.isEmpty() && !ward.isEmpty()) {
            selectAddress(city, district, ward);
        }

        driver.findElement(
                By.xpath("//div[contains(@class,'ant-modal')]//button[.//span[text()='Cập nhật']]")
        ).click();
        pause(3);

        switch (expectedResult) {
            case "success"  -> Assert.assertTrue(pageText().contains("thành công"));
            case "name"     -> Assert.assertTrue(pageText().contains("tên"));
            case "age"      -> Assert.assertTrue(pageText().contains("tuổi"));
            case "phone"    -> Assert.assertTrue(
                    pageText().contains("điện thoại") || pageText().contains("phone"));
            case "gender"   -> Assert.assertTrue(pageText().contains("giới"));
            case "role"     -> Assert.assertTrue(pageText().contains("vai trò"));
        }
    }
    // DEMO: Cập nhật thông tin thành công (hardcode để demo)
    @Test(priority = 99)
    public void DEMO_UpdateSuccess() {
        // Dữ liệu demo — thay bằng dữ liệu thực có trong DB của bạn
        String name   = "Nguyễn Văn Demo";
        String age    = "25";
        String phone  = "0987654321";
        String gender = "Nam";
        String role   = "USER";

        // Bước 1: Tìm kiếm người dùng cần sửa
        search("Lan1234@gmail.com", null);
        pause(1);

        // Bước 2: Click icon edit (bút chì)
        clickEdit();

        // Bước 3: Nhập thông tin mới
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='name']"), name);
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='age']"), age);
        enter(By.xpath("//div[contains(@class,'ant-modal')]//input[@id='phone']"), phone);

        // Bước 4: Chọn gender và role
        selectDropdown("gender", gender);
        selectDropdown("role", role);

        // Bước 5: Bấm Cập nhật
        driver.findElement(
                By.xpath("//div[contains(@class,'ant-modal')]//button[.//span[text()='Cập nhật']]")
        ).click();
        pause(3);

        // Bước 6: Kiểm tra thông báo thành công
        Assert.assertTrue(
                pageText().contains("thành công"),
                "Không thấy thông báo cập nhật thành công!"
        );
    }
    // =====================================================
    // DELETE
    // =====================================================

    public void clickDeleteBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//span[contains(@class,'anticon-delete')])[1]")
        )).click();
        pause(2);
        // Chờ popconfirm hiện ra
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-popover') and not(contains(@class,'ant-popover-hidden'))]")
        ));
    }

    @Test(priority = 25)
    public void DU_1_DeleteSuccess() {
        clickDeleteBtn();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-popover') and not(contains(@class,'ant-popover-hidden'))]//button[.//span[text()='Xác nhận']]")
        )).click();
        pause(3);

        // Kiểm tra thông báo thành công thay vì đếm dòng
        Assert.assertTrue(
                pageText().contains("thành công"),
                "Không thấy thông báo xóa thành công!"
        );
    }
    @Test(priority = 26)
    public void DU_2_CancelDelete() {
        int before = getRowCount();

        clickDeleteBtn();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-popover') and not(contains(@class,'ant-popover-hidden'))]//button[.//span[text()='Hủy']]")
        )).click();
        pause(2);

        int after = getRowCount();
        Assert.assertEquals(after, before, "Số dòng không đổi khi hủy xóa");
    }

    @Test(priority = 27)
    public void DU_3_NoConfirmDelete() {
        int before = getRowCount();

        clickDeleteBtn();

        driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        pause(2);

        shortWait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-popover') and not(contains(@class,'ant-popover-hidden'))]")
        ));

        int after = getRowCount();
        Assert.assertEquals(after, before, "Số dòng không đổi khi nhấn ESC");
    }
    // =====================================================
    // AFTER
    // =====================================================

    @AfterMethod
    public void afterEach() {
        closePopup();       // đóng toast
        closeModalIfOpen(); // đóng modal form nếu còn mở
        resetSearch();
        pause(1);
    }

    @AfterClass
    public void tearDown() {
        pause(3);
        if (driver != null) {
            driver.quit();
        }
    }
}