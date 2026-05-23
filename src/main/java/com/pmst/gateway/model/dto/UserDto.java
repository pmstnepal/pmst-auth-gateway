package com.pmst.gateway.model.dto;

import java.util.UUID;

public class UserDto {
    private String id;
    private String email;
    private String username;
    private String displayName;
    private String role;
    private String status;

    public UserDto() {}

    public UserDto(String id, String email, String username, String displayName, String role, String status) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.displayName = displayName;
        this.role = role;
        this.status = status;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
