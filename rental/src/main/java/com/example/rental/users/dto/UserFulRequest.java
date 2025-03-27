package com.example.rental.users.dto;

import jakarta.persistence.Column;

public record UserFulRequest(

        String name,

        String surname,

        String email,

        String phone

) {
}
