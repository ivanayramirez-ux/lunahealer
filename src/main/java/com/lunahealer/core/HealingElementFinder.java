package com.lunahealer.core;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;

public class HealingElementFinder {

    private final WebDriver driver;
    private final LocatorStore store;
    private final HealingLogger logger;
    private final SmartFallback fallback;
    private final AiLocatorResolver aiResolver;

    public HealingElementFinder(WebDriver driver, LocatorStore store, HealingLogger logger) {
        this.driver = driver;
        this.store = store;
        this.logger = logger;
        this.fallback = new SmartFallback();
        this.aiResolver = new AiLocatorResolver(new MockAiClient());
    }

   
    public WebElement find(String key) {
        LocatorProfile profile = store.getProfile(key);

        if (profile == null) {
            String msg = "No locator profile found for key: " + key;
            logger.log(msg);
            throw new NoSuchElementException(msg);
        }

        // 1. Try primary locator
        try {
            WebElement element = driver.findElement(profile.getPrimary());
            String locatorStr = profile.getPrimary().toString();
            String pageUrl = safeGetUrl();
            String msg = "Key [" + key + "] found using PRIMARY locator: " + locatorStr;

            logger.record(key, "PRIMARY", locatorStr, "FOUND", msg, pageUrl);
            return element;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            logger.log("Key [" + key + "] PRIMARY locator failed: " + profile.getPrimary()
                    + " – will try alternatives.");
        }

        // 2. Try alternative locators
        List<By> alts = profile.getAlternatives();
        for (int i = 0; i < alts.size(); i++) {
            By alt = alts.get(i);
            try {
                WebElement element = driver.findElement(alt);
                String locatorStr = alt.toString();
                String pageUrl = safeGetUrl();
                String msg = "Key [" + key + "] healed using ALT #" + i + ": " + locatorStr;

                logger.record(key, "ALT#" + i, locatorStr, "HEALED", msg, pageUrl);
                return element;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                logger.log("Key [" + key + "] ALT #" + i + " failed: " + alt);
            }
        }

        // 3. Try SmartFallback 
        WebElement fallbackElement = fallback.tryFallback(key, profile, driver, logger);
        if (fallbackElement != null) {
            String locatorStr = "[SmartFallback]"; 
            String pageUrl = safeGetUrl();
            String msg = "Key [" + key + "] healed using SmartFallback.";

            logger.record(key, "FALLBACK", locatorStr, "HEALED", msg, pageUrl);
            return fallbackElement;
        }

        // 4. AI-assisted resolution 
        logger.log("Key [" + key + "] not found via primary, alternatives, or SmartFallback. Invoking AI resolver.");
        Optional<By> aiSuggestion = aiResolver.suggestLocator(key, profile, driver, logger);

        if (aiSuggestion.isPresent()) {
            By aiBy = aiSuggestion.get();
            try {
                WebElement element = driver.findElement(aiBy);
                String locatorStr = aiBy.toString();
                String pageUrl = safeGetUrl();
                String msg = "Key [" + key + "] healed using AI-suggested locator: " + locatorStr;

                logger.record(key, "AI", locatorStr, "HEALED", msg, pageUrl);
                return element;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                logger.log("AI-suggested locator FAILED for key [" + key + "]: " + aiBy
                        + " – " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }

        // 5. All strategies failed
        String msg = "LunaHealer: unable to locate element for key [" + key + "] "
                + "using primary, alternatives, SmartFallback, or AI.";
        String pageUrl = safeGetUrl();
        logger.record(key, "NONE", "", "FAILED", msg, pageUrl);
        logger.log(msg);
        throw new NoSuchElementException(msg);
    }

    private String safeGetUrl() {
        try {
            return driver.getCurrentUrl();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
