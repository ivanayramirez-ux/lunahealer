package com.lunahealer.core;

import org.openqa.selenium.By;
import java.util.Optional;

public interface AiClient {

    Optional<By> suggestLocator(String elementKey, String intent, String pageHtml);
}
