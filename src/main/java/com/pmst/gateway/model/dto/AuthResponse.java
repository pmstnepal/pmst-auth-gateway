package com.pmst.gateway.model.dto;

public class AuthResponse {
    private String idToken;
    private String accessToken;
    private String refreshToken;
    private int expiresIn;
    private String tokenType;
    private UserDto user;

    public AuthResponse() {}

    public AuthResponse(String idToken, String accessToken, String refreshToken, int expiresIn, String tokenType, UserDto user) {
        this.idToken = idToken;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.user = user;
    }

    // Getters and Setters
    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public int getExpiresIn() { return expiresIn; }
    public void setExpiresIn(int expiresIn) { this.expiresIn = expiresIn; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }
}
