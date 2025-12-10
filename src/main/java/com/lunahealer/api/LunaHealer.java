package com.lunahealer.api;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.lunahealer.core.HealingElementFinder;
import com.lunahealer.core.HealingLogger;
import com.lunahealer.core.LocatorStore;

public class LunaHealer {

    private final HealingElementFinder finder;
    private final HealingLogger logger;

    public LunaHealer(WebDriver driver, String locatorConfigPath) {
        LocatorStore store = new LocatorStore(locatorConfigPath);
        this.logger = new HealingLogger();
        this.finder = new HealingElementFinder(driver, store, logger);
    }

    public WebElement find(String key) {
        return finder.find(key);
    }
    public HealingLogger getLogger() {
    	return logger;
    }
    
}
