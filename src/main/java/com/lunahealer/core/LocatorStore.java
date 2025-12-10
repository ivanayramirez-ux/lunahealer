package com.lunahealer.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LocatorStore {

    private final Map<String, LocatorProfile> profiles = new HashMap<>();

    public LocatorStore(String jsonPath) {
        loadFromJson(jsonPath);
    }

    public LocatorProfile getProfile(String key) {
        return profiles.get(key);
    }

    private void loadFromJson(String jsonPath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(jsonPath);

            Map<String, LocatorProfileConfig> raw =
                    mapper.readValue(file, new TypeReference<Map<String, LocatorProfileConfig>>() {});

            for (Map.Entry<String, LocatorProfileConfig> entry : raw.entrySet()) {
                String key = entry.getKey();
                LocatorProfileConfig cfg = entry.getValue();
                LocatorProfile profile = toLocatorProfile(key, cfg);
                profiles.put(key, profile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load locator config from " + jsonPath, e);
        }
    }

    private LocatorProfile toLocatorProfile(String key, LocatorProfileConfig cfg) {
        By primaryBy = toBy(cfg.primary);
        List<By> altList = new ArrayList<>();

        if (cfg.alternatives != null) {
            for (LocatorDefinition def : cfg.alternatives) {
                altList.add(toBy(def));
            }
        }

        return new LocatorProfile(
                key,
                cfg.description,
                cfg.semanticHint,
                primaryBy,
                altList
        );
    }

    private By toBy(LocatorDefinition def) {
        if (def == null) {
            return null;
        }
        String type = def.type.toLowerCase();
        String value = def.value;

        switch (type) {
            case "id":
                return By.id(value);
            case "name":
                return By.name(value);
            case "css":
            case "cssselector":
                return By.cssSelector(value);
            case "xpath":
                return By.xpath(value);
            case "classname":
            case "class_name":
                return By.className(value);
            case "tag":
            case "tagname":
                return By.tagName(value);
            default:
                throw new IllegalArgumentException("Unknown locator type: " + def.type);
        }
    }
}
