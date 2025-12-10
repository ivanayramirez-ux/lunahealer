package com.lunahealer.core;

import java.util.List;

import org.openqa.selenium.By;      // make sure this is EXACT

public class LocatorProfile {

    private final String key;
    private final String description;
    private final String semanticHint;
    private final By primary;
    private final List<By> alternatives;

    public LocatorProfile(String key,
                          String description,
                          String semanticHint,
                          By primary,
                          List<By> alternatives) {
        this.key = key;
        this.description = description;
        this.semanticHint = semanticHint;
        this.primary = primary;
        this.alternatives = alternatives;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public String getSemanticHint() {
        return semanticHint;
    }

    public By getPrimary() {
        return primary;
    }

    public List<By> getAlternatives() {
        return alternatives;
    }
}
