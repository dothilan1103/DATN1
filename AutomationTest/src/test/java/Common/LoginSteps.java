package Common;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginSteps {

    WebDriver driver;

    public LoginSteps(WebDriver driver) {
        this.driver = driver;
    }

    public void login(String email, String password) {
        driver.findElement(By.id("login-form_email")).clear();
        driver.findElement(By.id("login-form_email")).sendKeys(email);

        driver.findElement(By.id("login-form_password")).clear();
        driver.findElement(By.id("login-form_password")).sendKeys(password);

        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }
}