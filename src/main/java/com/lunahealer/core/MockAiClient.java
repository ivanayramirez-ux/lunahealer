package com.lunahealer.core;

import org.openqa.selenium.By;
import java.util.Optional;


public class MockAiClient implements AiClient {

    @Override
    public Optional<By> suggestLocator(String elementKey, String intent, String pageHtml) {

      
        if ("login.submit".equals(elementKey)) {
            return Optional.of(By.cssSelector("button[type='submit']"));
        }

      
        if ("register.submit".equals(elementKey)) {
            return Optional.of(By.cssSelector("button[type='submit']"));
        }

        
        return Optional.empty();
    }
}
