package com.sap.cic.pdp.oauthtoken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature; // For ignoring unknown fields
import org.springframework.stereotype.Component;

@Component
public class OAuth2TokenClient {

    private static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";

    public String getAccessToken(String clientId, String clientSecret, String authorizationServerUrl, String grantType) throws IOException {
        String authHeader = createAuthorizationHeader(clientId, clientSecret);

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("grant_type", grantType);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : requestParams.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(param.getValue(), "UTF-8"));
        }

        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        URL url = new URL(authorizationServerUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", authHeader);
        conn.setRequestProperty("Content-Type", DEFAULT_CONTENT_TYPE);
        conn.setDoOutput(true);
        conn.setDoInput(true);

        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postDataBytes);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
            }

            // Parse the JSON response and extract access token
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Ignore unknown fields
            Map<String, Object> responseMap = mapper.readValue(response.toString(), Map.class);

            return (String) responseMap.get("access_token");
        } else {
            System.err.println("Error getting token: " + responseCode + " - " + conn.getResponseMessage());
            return null;
        }
    }

    private String createAuthorizationHeader(String clientId, String clientSecret) {
        // Base64 encode the client ID and client secret concatenated with a colon (:)
        String credentials = clientId + ":" + clientSecret;
        byte[] encodedCredentials = java.util.Base64.getEncoder().encodeToString(credentials.getBytes()).getBytes();
        return "Basic " + new String(encodedCredentials);
    }
}
