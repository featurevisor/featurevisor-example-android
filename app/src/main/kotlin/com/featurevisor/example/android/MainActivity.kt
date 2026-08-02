package com.featurevisor.example.android

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.featurevisor.sdk.DatafileContent
import com.featurevisor.sdk.Featurevisor
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private val executor = Executors.newSingleThreadExecutor()

    private var f: Featurevisor? = null
    private lateinit var loadingView: View
    private lateinit var errorView: View
    private lateinit var resultsView: View
    private lateinit var errorMessage: TextView
    private lateinit var flagValue: TextView
    private lateinit var variationValue: TextView
    private lateinit var variableValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadingView = findViewById(R.id.loading_view)
        errorView = findViewById(R.id.error_view)
        resultsView = findViewById(R.id.results_view)
        errorMessage = findViewById(R.id.error_message)
        flagValue = findViewById(R.id.flag_value)
        variationValue = findViewById(R.id.variation_value)
        variableValue = findViewById(R.id.variable_value)

        findViewById<View>(R.id.retry_button).setOnClickListener { loadDatafile() }

        loadDatafile()
    }

    private fun loadDatafile() {
        showLoading()

        executor.execute {
            try {
                val datafile = DatafileContent.fromJson(fetchDatafile())
                val loadedF = Featurevisor.createFeaturevisor(
                    Featurevisor.FeaturevisorOptions().datafile(datafile),
                )
                val context = mapOf<String, Any>(
                    "userId" to "mobile-user",
                    "country" to "nl",
                )

                val enabled = loadedF.isEnabled("mobile_experience", context)
                val variation = loadedF.getVariation("mobile_experience", context)
                val welcomeMessage = loadedF.getVariableString(
                    "mobile_experience",
                    "welcome_message",
                    context,
                )

                runOnUiThread {
                    showResults(loadedF, enabled, variation, welcomeMessage)
                }
            } catch (exception: Exception) {
                runOnUiThread { showError(exception.message) }
            }
        }
    }

    private fun fetchDatafile(): String {
        val connection = URL(DATAFILE_URL).openConnection() as HttpURLConnection
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        connection.setRequestProperty("Accept", "application/json")

        return try {
            val statusCode = connection.responseCode

            if (statusCode !in 200..299) {
                error("Datafile request failed with HTTP $statusCode.")
            }

            connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun showLoading() {
        loadingView.visibility = View.VISIBLE
        errorView.visibility = View.GONE
        resultsView.visibility = View.GONE
    }

    private fun showResults(
        loadedF: Featurevisor,
        enabled: Boolean,
        variation: String?,
        welcomeMessage: String?,
    ) {
        if (isFinishing || isDestroyed) {
            loadedF.close()
            return
        }

        f?.close()
        f = loadedF

        flagValue.setText(if (enabled) R.string.enabled else R.string.disabled)
        variationValue.text = variation ?: getString(R.string.no_value)
        variableValue.text = welcomeMessage ?: getString(R.string.no_value)
        loadingView.visibility = View.GONE
        errorView.visibility = View.GONE
        resultsView.visibility = View.VISIBLE
    }

    private fun showError(message: String?) {
        if (isFinishing || isDestroyed) {
            return
        }

        loadingView.visibility = View.GONE
        errorView.visibility = View.VISIBLE
        resultsView.visibility = View.GONE
        errorMessage.text = message ?: getString(R.string.unknown_error)
    }

    override fun onDestroy() {
        executor.shutdownNow()
        f?.close()
        super.onDestroy()
    }

    private companion object {
        const val DATAFILE_URL =
            "https://featurevisor-example-cloudflare.pages.dev/production/featurevisor-mobile.json"
    }
}
