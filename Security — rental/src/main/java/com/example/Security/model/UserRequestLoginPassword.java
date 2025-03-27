package com.example.Security.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;



public class UserRequestLoginPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String password;
    private String identifier;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public UserRequestLoginPassword(String password, String identifier) {
        this.password = password;
        this.identifier = identifier;
    }

}