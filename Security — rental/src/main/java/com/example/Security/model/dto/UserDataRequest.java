package com.example.Security.model.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;



public class UserDataRequest {

    private UUID uuid;
    private String email;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserDataRequest(UUID uuid, String email) {
        this.uuid = uuid;
        this.email = email;
    }
}