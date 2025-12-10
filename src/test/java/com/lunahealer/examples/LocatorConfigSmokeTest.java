package com.lunahealer.examples;

import com.lunahealer.core.LocatorProfile;
import com.lunahealer.core.LocatorStore;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LocatorConfigSmokeTest {

    @Test
    public void canLoadProfilesFromJson() {
        LocatorStore store = new LocatorStore("config/locators.json");

        LocatorProfile email = store.getProfile("login.email");
        LocatorProfile submit = store.getProfile("login.submit");

        Assert.assertNotNull(email, "login.email should be present in JSON config");
        Assert.assertNotNull(submit, "login.submit should be present in JSON config");

        System.out.println("JSON primary email locator: " + email.getPrimary());
        System.out.println("JSON submit locator: " + submit.getPrimary());
        System.out.println("JSON email alt count: " + email.getAlternatives().size());
    }
}
