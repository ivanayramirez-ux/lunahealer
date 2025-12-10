package com.lunahealer.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HealingLogger {

    private final List<HealingEvent> events = new ArrayList<>();

    public void log(String message) {
        System.out.println("[LunaHealer] " + message);
    }

    
    public void record(String elementKey,
                       String strategy,
                       String locatorUsed,
                       String result,
                       String message,
                       String pageUrl) {

        HealingEvent event = new HealingEvent();
        event.setTimestamp(Instant.now().toString());
        event.setElementKey(elementKey);
        event.setStrategy(strategy);
        event.setLocatorUsed(locatorUsed);
        event.setResult(result);
        event.setPageUrl(pageUrl);
        event.setMessage(message);

        events.add(event);

     
        log("[" + result + "][" + strategy + "][" + elementKey + "] " + message);
    }

    public List<HealingEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

   
    public void writeJsonReport(String path) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(path);
            file.getParentFile().mkdirs(); 
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, events);
            log("Healing report written to: " + file.getAbsolutePath());
        } catch (IOException e) {
            log("Failed to write healing report to " + path + ": " + e.getMessage());
        }
    }
}
