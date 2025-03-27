package com.example.Security.model.enums;

public enum Role {
    COACH(0),
    STUDENT(1);

    private final int value;

    Role(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // Метод для преобразования числа в enum
    public static Role fromValue(int value) {
        for (Role role : Role.values()) {
            if (role.getValue() == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role value: " + value);
    }
}
