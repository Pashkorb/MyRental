package com.example.Security.model.dto;

import com.example.Security.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;



public class UserDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String password;
    private String login;
    private String email;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserDTO(String password, String login, String email) {
        this.password = password;
        this.login = login;
        this.email = email;
    }
    public UserDTO() {

    }
}