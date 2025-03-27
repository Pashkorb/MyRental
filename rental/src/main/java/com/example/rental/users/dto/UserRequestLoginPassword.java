package com.example.rental.users.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public class UserRequestLoginPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String password;
    private String login;

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


}