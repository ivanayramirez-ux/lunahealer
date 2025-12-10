package com.lunahealer.examples;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.lunahealer.api.LunaHealer;

public class LunaBankEndToEndHealingTest {

    private WebDriver driver;
    private LunaHealer healer;

  
    private static final String BASE_URL = "https://ivanayramirez-ux.github.io/lunabank-qa-webapp";

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        healer = new LunaHealer(driver, "config/locators.json");
        log("===== Starting LunaBank E2E healing scenario =====");
    }

    @AfterMethod
    public void tearDown() {
        log("===== Ending LunaBank E2E healing scenario =====");
        if (driver != null) {
            driver.quit();
        }
    }

    private void log(String msg) {
        System.out.println("[LunaE2E] " + msg);
        Reporter.log("[LunaE2E] " + msg, true);
    }

    private double parseMoney(String text) {
        // e.g. "$18,829.89" -> 18829.89
        String normalized = text.replace("$", "").replace(",", "").trim();
        return Double.parseDouble(normalized);
    }

    @Test
    public void endToEnd_flow_uses_all_healing_layers_successfully() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));

        // 1. Index -> Login
        log("Step 1: Open index page and navigate to Login using normal primary locator.");
        driver.get(BASE_URL + "/index.html");

        healer.find("index.signIn").click();
        wait.until(ExpectedConditions.urlContains("login.html"));
        log("Navigated to login.html successfully.");

        // 2. Login (AI healing on login.submit)
        log("Step 2: Perform login using VALID credentials.");
        healer.find("login.email").sendKeys("test@lunabank.com");
        healer.find("login.password").sendKeys("EasyPass123");

        log("Intentionally broken login.submit primary + alts + bad semanticHint -> expect AI healing.");
        healer.find("login.submit").click();

        wait.until(ExpectedConditions.urlContains("dashboard.html"));
        log("Login succeeded and redirected to dashboard.html (AI healing layer used for login.submit).");

        // 3. Capture balances on dashboard
        log("Step 3: Capture initial balances for Everyday Checking and High-Yield Savings.");
        WebElement checkingBalanceCell = healer.find("dashboard.checkingBalance");
        WebElement savingsBalanceCell  = healer.find("dashboard.savingsBalance");

        double initialChecking = parseMoney(checkingBalanceCell.getText());
        double initialSavings  = parseMoney(savingsBalanceCell.getText());

        log("Initial Everyday Checking balance: " + initialChecking);
        log("Initial High-Yield Savings balance: " + initialSavings);

        // 4. Dashboard -> Transfer (SmartFallback)
        log("Step 4: Navigate to Transfer page from dashboard.");
        log("dashboard.transferBtn has a broken primary ID but good semanticHint -> expect SmartFallback.");
        healer.find("dashboard.transferBtn").click();

        wait.until(ExpectedConditions.urlContains("transfer.html"));
        log("Arrived on transfer.html via SmartFallback healing for dashboard.transferBtn.");

        // 5. Perform transfer: Savings -> Checking (ALT healing on submit)
        log("Step 5: Configure transfer from High-Yield Savings to Everyday Checking.");

        Select fromSelect = new Select(healer.find("transfer.fromAccount"));
        fromSelect.selectByValue("savings");

        Select toSelect = new Select(healer.find("transfer.toAccount"));
        toSelect.selectByValue("checking");

        String amountToTransfer = "250.00";
        WebElement amountInput = healer.find("transfer.amount");
        amountInput.clear();
        amountInput.sendKeys(amountToTransfer);
        log("Transfer amount set to " + amountToTransfer);

        log("transfer.submit has a broken primary but valid CSS alternative -> expect ALT healing.");
        healer.find("transfer.submit").click();

        String transferBannerText = healer.find("transfer.banner").getText();
        log("Transfer banner text: " + transferBannerText);

        assertTrue(
            transferBannerText.toLowerCase().contains("transfer scheduled"),
            "Expected success transfer banner to mention 'Transfer scheduled', but got: " + transferBannerText
        );
        log("Transfer confirmation banner validated.");

        log("Navigate back to dashboard from transfer page.");
        log("transfer.backToDashboard has a broken primary but valid CSS alternative -> expect ALT healing.");
        healer.find("transfer.backToDashboard").click();
        wait.until(ExpectedConditions.urlContains("dashboard.html"));
        log("Back on dashboard.html after a completed transfer.");

        // 6. Verify balances reflect transfer
        log("Step 6: Verify balances reflect the transfer (Checking up, Savings down).");

        double newChecking = parseMoney(healer.find("dashboard.checkingBalance").getText());
        double newSavings  = parseMoney(healer.find("dashboard.savingsBalance").getText());
        double transferAmount = Double.parseDouble(amountToTransfer);

        log("New Everyday Checking balance: " + newChecking);
        log("New High-Yield Savings balance: " + newSavings);

        assertEquals(
            newChecking,
            initialChecking + transferAmount,
            0.01,
            String.format(
                "Checking balance should increase by %.2f (from %.2f to %.2f), but is %.2f",
                transferAmount, initialChecking, initialChecking + transferAmount, newChecking
            )
        );

        assertEquals(
            newSavings,
            initialSavings - transferAmount,
            0.01,
            String.format(
                "Savings balance should decrease by %.2f (from %.2f to %.2f), but is %.2f",
                transferAmount, initialSavings, initialSavings - transferAmount, newSavings
            )
        );

        log("Account balances updated correctly after self-healed transfer.");

        // 7. Profile page – multiple healers
        log("Step 7: Navigate to Profile page and update notification + contact info.");
        log("dashboard.profileLink has a broken primary CSS but valid alternative -> expect ALT healing.");
        healer.find("dashboard.profileLink").click();
        wait.until(ExpectedConditions.urlContains("profile.html"));
        log("Arrived on profile.html.");

        log("Toggle marketing notifications (broken primary, ALT locator for checkbox -> expect ALT healing).");
        WebElement marketingToggle = healer.find("profile.marketingToggle");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", marketingToggle);

        log("Edit contact info: phone number (profile.contactEditBtn has broken primary -> expect SmartFallback by button text).");
        healer.find("profile.contactEditBtn").click();

        WebElement phone = healer.find("profile.phoneInput");
        phone.clear();
        phone.sendKeys("+1 (555) 555-5555");

        log("Saving contact info (profile.contactSaveBtn has broken primary -> expect SmartFallback by button text).");
        healer.find("profile.contactSaveBtn").click();

        String profileMsg = healer.find("profile.contactMessage").getText();
        log("Profile save message: " + profileMsg);

        assertTrue(
            profileMsg.toLowerCase().contains("saved") || profileMsg.toLowerCase().contains("updated"),
            "Profile save message should indicate success (saved/updated), but got: " + profileMsg
        );
        log("Profile settings successfully updated.");

        log("Return from profile to dashboard (profile.backToDashboard has broken primary -> expect SmartFallback using link text).");
        healer.find("profile.backToDashboard").click();
        wait.until(ExpectedConditions.urlContains("dashboard.html"));
        log("Back on dashboard.html from profile.");

        // 8. Security page – MFA toggle + alerts
        log("Step 8: Navigate to Security page and change MFA + alerts preferences.");
        healer.find("dashboard.securityLink").click();
        wait.until(ExpectedConditions.urlContains("security.html"));
        log("Arrived on security.html.");

        log("Toggle MFA setting (security.mfaToggle has broken primary CSS, valid alternative -> expect ALT healing).");
        WebElement mfaToggle = healer.find("security.mfaToggle");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", mfaToggle);

        log("Set login alerts to 'Email only'.");
        healer.find("security.alertsEmailOnly").click();

        healer.find("security.saveBtn").click();

        String secMsg = healer.find("security.message").getText();
        log("Security save message: " + secMsg);

        assertTrue(
            secMsg.toLowerCase().contains("saved") || secMsg.toLowerCase().contains("updated"),
            "Security save message should indicate success (saved/updated), but got: " + secMsg
        );
        log("Security settings successfully updated.");

        log("Return from security to dashboard.");
        healer.find("security.backToDashboard").click();
        wait.until(ExpectedConditions.urlContains("dashboard.html"));
        log("Back on dashboard.html from security.");

        // 9. RockyInvest partner experience – card ALT + SmartFallback in modal
        log("Step 9: Open RockyInvest partner experience (modal + new tab).");
        log("dashboard.rockyCard has broken primary id, valid alternative -> expect ALT healing.");
        healer.find("dashboard.rockyCard").click();

        log("Rocky modal opened; rocky.modalContinue has broken primary CSS, no alternatives -> expect SmartFallback by 'Continue' text.");
        healer.find("rocky.modalContinue").click();

        String originalWindow = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalWindow)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        wait.until(ExpectedConditions.urlContains("rocky-down.html"));
        log("Switched to RockyInvest tab (rocky-down.html).");

        String rockyHeading = healer.find("rocky.heading").getText();
        log("RockyInvest heading text: " + rockyHeading);

        assertTrue(
            rockyHeading.toLowerCase().contains("interstellar link temporarily offline"),
            "RockyInvest page should indicate offline interstellar link, but heading was: " + rockyHeading
        );
        log("RockyInvest offline state verified.");

        // 10. Verify healing report file exists
        log("Step 10: Verify LunaHealer healing report JSON was generated.");
        File report = new File("target/lunahealer-healing-report.json");
        assertTrue(
            report.exists(),
            "Expected healing report at target/lunahealer-healing-report.json to exist after E2E run."
        );
        log("Healing report file found at target/lunahealer-healing-report.json.");
    }
}