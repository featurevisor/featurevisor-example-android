package com.featurevisor.example.android

import com.featurevisor.sdk.DatafileContent
import com.featurevisor.sdk.Featurevisor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeaturevisorTest {
    @Test
    fun evaluatesFeaturesAndGlobalVariables() {
        val json = requireNotNull(
            javaClass.classLoader?.getResource("featurevisor-sdk-v3.json"),
        ).readText()
        val context = mapOf<String, Any>(
            "userId" to "customer-123",
            "country" to "nl",
            "locale" to "nl-NL",
            "accountPlan" to "pro",
        )
        val f = Featurevisor.createFeaturevisor(
            Featurevisor.FeaturevisorOptions()
                .datafile(DatafileContent.fromJson(json))
                .context(context),
        )

        try {
            assertTrue(f.isEnabled("commerce_platform"))
            assertEquals("express", f.getVariation("checkout_experience"))
            assertEquals(25, f.getVariableInteger("checkout_experience", "max_items"))
            assertEquals(
                listOf("card", "wallet"),
                f.getVariableArray("checkout_experience", "payment_methods"),
            )
            assertEquals(
                "https://api.eu.example.com",
                f.getVariableObject<Map<String, Any>>("serviceEndpoints")["baseUrl"],
            )
            assertEquals("support-nl@example.com", f.getVariableString("supportContact"))
        } finally {
            f.close()
        }
    }
}
