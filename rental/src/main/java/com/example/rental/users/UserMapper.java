package com.example.rental.users;

import com.example.rental.users.dto.UserFulRequest;
import com.example.rental.users.dto.UserResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserMapper {
    // Метод для преобразования User в UserResponse
    public static UserResponse toDto(User user) {
        return new UserResponse(
                user.getUuid(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPhone()
        );
    }
    // Метод для преобразования списка User в список UserResponse
    public static List<UserResponse> toDtoList(List<User> users) {
        return users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public static User getEntity(UserFulRequest user, UUID userId) {
        return new User(userId,user.name(),user.surname(),user.email(),user.phone());

    }


}