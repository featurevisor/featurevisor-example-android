package com.featurevisor.example.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.featurevisor.sdk.DatafileContent;
import com.featurevisor.sdk.Featurevisor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MainActivity extends Activity {
    private static final String DATAFILE_URL =
        "https://featurevisor-example-cloudflare.pages.dev/production/featurevisor-mobile.json";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Featurevisor f;
    private View loadingView;
    private View errorView;
    private View resultsView;
    private TextView errorMessage;
    private TextView flagValue;
    private TextView variationValue;
    private TextView variableValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingView = findViewById(R.id.loading_view);
        errorView = findViewById(R.id.error_view);
        resultsView = findViewById(R.id.results_view);
        errorMessage = findViewById(R.id.error_message);
        flagValue = findViewById(R.id.flag_value);
        variationValue = findViewById(R.id.variation_value);
        variableValue = findViewById(R.id.variable_value);

        findViewById(R.id.retry_button).setOnClickListener(view -> loadDatafile());

        loadDatafile();
    }

    private void loadDatafile() {
        showLoading();

        executor.execute(() -> {
            try {
                String datafileJson = fetchDatafile();
                DatafileContent datafile = DatafileContent.fromJson(datafileJson);
                Featurevisor loadedF = Featurevisor.createFeaturevisor(
                    new Featurevisor.FeaturevisorOptions().datafile(datafile)
                );

                Map<String, Object> context = new HashMap<>();
                context.put("userId", "mobile-user");
                context.put("country", "nl");

                boolean enabled = loadedF.isEnabled("mobile_experience", context);
                String variation = loadedF.getVariation("mobile_experience", context);
                String welcomeMessage = loadedF.getVariableString(
                    "mobile_experience",
                    "welcome_message",
                    context
                );

                runOnUiThread(() -> showResults(
                    loadedF,
                    enabled,
                    variation,
                    welcomeMessage
                ));
            } catch (Exception exception) {
                runOnUiThread(() -> showError(exception.getMessage()));
            }
        });
    }

    private String fetchDatafile() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(DATAFILE_URL).openConnection();
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(10_000);
        connection.setRequestProperty("Accept", "application/json");

        try {
            int statusCode = connection.getResponseCode();

            if (statusCode < 200 || statusCode >= 300) {
                throw new IOException("Datafile request failed with HTTP " + statusCode + ".");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream(),
                StandardCharsets.UTF_8
            ))) {
                StringBuilder content = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }

                return content.toString();
            }
        } finally {
            connection.disconnect();
        }
    }

    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        resultsView.setVisibility(View.GONE);
    }

    private void showResults(
        Featurevisor loadedF,
        boolean enabled,
        String variation,
        String welcomeMessage
    ) {
        if (isFinishing() || isDestroyed()) {
            loadedF.close();
            return;
        }

        if (f != null) {
            f.close();
        }

        f = loadedF;
        flagValue.setText(enabled ? R.string.enabled : R.string.disabled);
        variationValue.setText(valueOrFallback(variation));
        variableValue.setText(valueOrFallback(welcomeMessage));
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        resultsView.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        resultsView.setVisibility(View.GONE);
        errorMessage.setText(message != null ? message : getString(R.string.unknown_error));
    }

    private String valueOrFallback(String value) {
        return value != null ? value : getString(R.string.no_value);
    }

    @Override
    protected void onDestroy() {
        executor.shutdownNow();

        if (f != null) {
            f.close();
        }

        super.onDestroy();
    }
}
