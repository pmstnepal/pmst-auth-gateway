package com.pmst.gateway.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    private String displayName;

    public RegisterRequest() {}

    public RegisterRequest(String email, String password, String username, String displayName) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.displayName = displayName;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}
