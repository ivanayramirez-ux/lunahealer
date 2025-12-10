package com.lunahealer.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;


public class AiLocatorResolver {

    private final AiClient aiClient;

    public AiLocatorResolver(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    public Optional<By> suggestLocator(String key,
                                       LocatorProfile profile,
                                       WebDriver driver,
                                       HealingLogger logger) {

        String intent = buildIntent(profile);
        String dom = safeGetDom(driver);

        logger.log("AIResolver: requesting suggestion for key [" + key + "] with intent '" + intent + "'.");

      
        Optional<By> aiSuggestion = aiClient.suggestLocator(key, intent, dom);

        if (aiSuggestion.isEmpty()) {
            logger.log("AIResolver: no suggestion returned for key [" + key + "].");
            return Optional.empty();
        }

        By candidate = aiSuggestion.get();

       
        List<WebElement> matches;
        try {
            matches = driver.findElements(candidate);
        } catch (Exception e) {
            logger.log("AIResolver: error validating AI locator for key [" + key + "]: "
                    + e.getClass().getSimpleName() + " – " + e.getMessage());
            return Optional.empty();
        }

        if (matches.size() == 1) {
            logger.log("AIResolver: suggestion '" + candidate
                    + "' matched exactly 1 element for key [" + key + "].");
            return Optional.of(candidate);
        } else {
            logger.log("AIResolver: suggestion '" + candidate
                    + "' matched " + matches.size()
                    + " elements for key [" + key + "], not auto-healing.");
            return Optional.empty();
        }
    }

    private String buildIntent(LocatorProfile profile) {
        
        String hint = profile.getSemanticHint();
        String desc = profile.getDescription();

        if (hint != null && !hint.isEmpty() && desc != null && !desc.isEmpty()) {
            return hint + " – " + desc;
        } else if (hint != null && !hint.isEmpty()) {
            return hint;
        } else if (desc != null && !desc.isEmpty()) {
            return desc;
        }
        return "Unknown element";
    }

    private String safeGetDom(WebDriver driver) {
        try {
            return driver.getPageSource();
        } catch (Exception e) {
            return "";
        }
    }
}
