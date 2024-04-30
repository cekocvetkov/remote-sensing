package org.zhvtsv.service.sentinel;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.net.http.HttpRequest.BodyPublishers.ofString;

@ApplicationScoped
public class SentinelAuth {
    @ConfigProperty(name = "sentinel-auth.url")
    private String url;
    @ConfigProperty(name = "sentinel-auth.client-id")
    private String clientId;
    @ConfigProperty(name = "sentinel-auth.client-secret")
    private String clientSecret;

    //TODO: Add refresh token functionality
    public String getAccessToken() {
        HttpClient httpClient = HttpClient.newBuilder().build();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "client_credentials");
        parameters.put("client_id", clientId);
        parameters.put("client_secret", clientSecret);

        String form = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .POST(ofString(form))
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new ServerErrorException("Request for Sentinel Processing API Data Access Token failed", Response.Status.BAD_GATEWAY);
        }

        JSONObject json = new JSONObject(response.body());
        String accessToken = json.getString("access_token");

        return accessToken;
    }
}