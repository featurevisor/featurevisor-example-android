package com.featurevisor.example.android

import com.featurevisor.sdk.DatafileContent
import com.featurevisor.sdk.Featurevisor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeaturevisorTest {
    @Test
    fun evaluatesMobileExperience() {
        val json = requireNotNull(
            javaClass.classLoader?.getResource("featurevisor-mobile.json"),
        ).readText()
        val f = Featurevisor.createFeaturevisor(
            Featurevisor.FeaturevisorOptions().datafile(
                DatafileContent.fromJson(json),
            ),
        )
        val context = mapOf<String, Any>(
            "userId" to "mobile-user",
            "country" to "nl",
        )

        try {
            assertTrue(f.isEnabled("mobile_experience", context))
            assertEquals(
                "treatment",
                f.getVariation("mobile_experience", context),
            )
            assertEquals(
                "Welkom",
                f.getVariableString(
                    "mobile_experience",
                    "welcome_message",
                    context,
                ),
            )
        } finally {
            f.close()
        }
    }
}
