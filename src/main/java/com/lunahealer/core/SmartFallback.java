package com.lunahealer.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SmartFallback {

   
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "the", "a", "an", "and", "or", "on", "in", "of", "for", "to",
            "button", "link", "page", "screen", "dashboard"
    ));

   
    public WebElement tryFallback(String key,
                                  LocatorProfile profile,
                                  WebDriver driver,
                                  HealingLogger logger) {

        String hint = profile.getSemanticHint();
        if (hint == null || hint.trim().isEmpty()) {
            logger.log("SmartFallback: no semanticHint for key [" + key + "], skipping.");
            return null;
        }

        
        String[] rawTokens = hint.toLowerCase().split("\\W+");
        List<String> tokens = Arrays.stream(rawTokens)
                .filter(t -> t.length() > 2)                
                .filter(t -> !STOP_WORDS.contains(t))       
                .distinct()
                .collect(Collectors.toList());

        if (tokens.isEmpty()) {
            logger.log("SmartFallback: no useful tokens for key [" + key + "] from hint '" + hint + "'.");
            return null;
        }

        logger.log("SmartFallback: tokens for key [" + key + "] from hint '" + hint + "': " + tokens);

       
        List<WebElement> candidates = driver.findElements(
                By.cssSelector("button, a, [role='button'], input[type='button'], input[type='submit']")
        );

        if (candidates.isEmpty()) {
            logger.log("SmartFallback: no clickable candidates found in DOM for key [" + key + "].");
            return null;
        }

        WebElement best = null;
        int bestScore = 0;

       
        for (WebElement el : candidates) {
            StringBuilder sb = new StringBuilder();

            try {
                String text = el.getText();
                if (text != null) {
                    sb.append(' ').append(text);
                }
            } catch (Exception ignored) {
            }

           
            String[] attrs = {
                    "id", "name", "class", "aria-label", "value",
                    "data-test", "data-qa", "data-testid"
            };

            for (String attr : attrs) {
                try {
                    String v = el.getAttribute(attr);
                    if (v != null) {
                        sb.append(' ').append(v);
                    }
                } catch (Exception ignored) {
                }
            }

            String haystack = sb.toString().toLowerCase();
            int score = 0;

            for (String token : tokens) {
                if (!token.isEmpty() && haystack.contains(token)) {
                    score++;
                }
            }

            if (score > bestScore) {
                bestScore = score;
                best = el;
            }
        }

        if (best != null && bestScore > 0) {
            logger.log("SmartFallback: key [" + key + "] healed with score "
                    + bestScore + " using semanticHint '" + hint + "'.");
            return best;
        }

        logger.log("SmartFallback: no suitable candidate for key [" + key + "] using semanticHint '" + hint + "'.");
        return null;
    }
}
