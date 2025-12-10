package com.lunahealer.examples;

import com.lunahealer.api.LunaHealer;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LunaHealerLoginDemoTest {

	private WebDriver driver;
	private LunaHealer healer;

	@BeforeMethod
	public void setUp() {

		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://ivanayramirez-ux.github.io/lunabank-qa-webapp/login.html");

		healer = new LunaHealer(driver, "config/locators.json");
	}

	@AfterMethod
	public void tearDown() {

		try {
			if (healer != null) {
				healer.getLogger().writeJsonReport("target/lunahealer-healing-report.json");

			}
		} finally {

			if (driver != null) {
				driver.quit();
			}
		}
	}

	@Test
	public void loginWithLunaHealerLocators() {
		
		healer.find("login.email").sendKeys("test@lunabank.com");
		healer.find("login.password").sendKeys("EasyPass123");
		healer.find("login.submit").click(); 
	}
}
