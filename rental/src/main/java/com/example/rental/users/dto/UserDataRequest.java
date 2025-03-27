package com.example.rental.users.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
public class UserDataRequest {///используется для создания нового пользователя из сервиса безопасности

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

}