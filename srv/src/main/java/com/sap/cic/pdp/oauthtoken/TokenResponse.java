package com.sap.cic.pdp.oauthtoken;

public class TokenResponse {
    // Define fields based on your authorization server's response structure (e.g., access_token, expires_in)
    private String accessToken;
    private long expiresIn;

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }


}
