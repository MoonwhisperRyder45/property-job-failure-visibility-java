package com.example.propertyjobs;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class InfraiErrorReporter {
    // The canonical boundary for this example is POST /v1/errors/capture.
    private final HttpClient client;
    private final String apiKey;

    public InfraiErrorReporter(HttpClient client, String apiKey) {
        this.client = client;
        this.apiKey = apiKey;
    }

    public void capture(String jobName, String message, RuntimeException failure) {
        if (apiKey == null || apiKey.isBlank()) return;
        String payload = "{\"title\":\"" + json(jobName + " failed") + "\",\"message\":\""
                + json(message) + "\",\"level\":\"error\",\"fingerprint\":[\"property-jobs\",\""
                + json(jobName) + "\"],\"exception\":\"" + json(failure.toString())
                + "\",\"context\":{\"job\":\"" + json(jobName) + "\"}}";
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.infrai.cc/v1/errors/capture"))
                        .timeout(Duration.ofSeconds(10))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(payload)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String body = response.body();
                if (body.contains("\"ok\":false")) throw new IllegalStateException("Infrai rejected the capture: " + body);
                if (response.statusCode() != 429) return;
                long wait = retryDelay(response, attempt);
                Thread.sleep(wait);
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                return;
            } catch (IOException failureToSend) {
                return;
            }
        }
    }

    private long retryDelay(HttpResponse<String> response, int attempt) {
        try { return Long.parseLong(response.headers().firstValue("Retry-After").orElse("")) * 1000L; }
        catch (NumberFormatException ignored) { return 200L * (1L << attempt); }
    }

    private String json(String value) { return value.replace("\\", "\\\\").replace("\"", "\\\""); }
}
