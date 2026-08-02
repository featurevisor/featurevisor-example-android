package com.featurevisor.example.android;

import com.featurevisor.sdk.DatafileContent;
import com.featurevisor.sdk.Featurevisor;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class FeaturevisorTest {
    @Test
    public void evaluatesMobileExperience() throws Exception {
        String json = Files.readString(
            Path.of("src/test/resources/featurevisor-mobile.json")
        );
        Featurevisor f = Featurevisor.createFeaturevisor(
            new Featurevisor.FeaturevisorOptions().datafile(
                DatafileContent.fromJson(json)
            )
        );
        Map<String, Object> context = new HashMap<>();
        context.put("userId", "mobile-user");
        context.put("country", "nl");

        try {
            assertTrue(f.isEnabled("mobile_experience", context));
            assertEquals(
                "treatment",
                f.getVariation("mobile_experience", context)
            );
            assertEquals(
                "Welkom",
                f.getVariableString(
                    "mobile_experience",
                    "welcome_message",
                    context
                )
            );
        } finally {
            f.close();
        }
    }
}
