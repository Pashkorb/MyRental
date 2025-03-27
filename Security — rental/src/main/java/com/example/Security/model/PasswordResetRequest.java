package com.example.Security.model;

public record PasswordResetRequest (
     Integer token,
     String newPassword,
     String email
){}