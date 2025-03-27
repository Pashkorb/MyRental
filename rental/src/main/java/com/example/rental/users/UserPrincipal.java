package com.example.rental.users;

import java.util.UUID;

public class UserPrincipal {
    private final UUID userId;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserPrincipal(UUID userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

}