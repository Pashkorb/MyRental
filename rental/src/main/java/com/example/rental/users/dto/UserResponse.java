package com.example.rental.users.dto;


import java.util.Date;
import java.util.UUID;

public class UserResponse {
    private UUID uuid;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private Date registrationDate;

    public UserResponse(UUID uuid, String name, String surname, String email, String phone) {
        this.uuid = uuid;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
}
