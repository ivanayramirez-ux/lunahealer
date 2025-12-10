package com.lunahealer.examples;

import com.lunahealer.api.LunaHealer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LunaHealerRegisterDemoTest {

    private WebDriver driver;
    private LunaHealer healer;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        driver.get("https://ivanayramirez-ux.github.io/lunabank-qa-webapp/register.html");

        healer = new LunaHealer(driver, "config/locators.json");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void canRegisterNewUserWithHealedLocators() {
        healer.find("register.firstName").sendKeys("Ivana");
        healer.find("register.lastName").sendKeys("Ramirez");
        healer.find("register.email").sendKeys("ivannyramirez@gmail.com");
        healer.find("register.confirmEmail").sendKeys("ivannyramirez@gmail.com");
        healer.find("register.password").sendKeys("Passw0rd!");
        healer.find("register.confirmPassword").sendKeys("Passw0rd!");

        healer.find("register.terms").click();

        healer.find("register.submit").click();

        WebElement banner = healer.find("register.successBanner");
        String text = banner.getText();
        System.out.println("Success banner text: " + text);

        Assert.assertTrue(
                text.contains("Account created successfully"),
                "Expected success banner, but got: " + text
        );
    }
}
