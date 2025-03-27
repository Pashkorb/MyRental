package com.example.rental.users;
import com.example.rental.users.dto.UserFulRequest;
import com.example.rental.users.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    // Создание нового пользователя
    public void createUser(User user) {
        if (userRepo.existsByEmail(user.getEmail())) {
            throw new ConflictException("User with this email already exists");
        }
        // Сохраняем пользователя в базе данных
        userRepo.save(user);
    }

    public UserResponse getUser(UUID userId) {
        User user = userRepo.findById(userId).orElse(null);
        return user != null ? UserMapper.toDto(user) : null;
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepo.findAll();
        return UserMapper.toDtoList(users);
    }



    public void deleteUser(UUID userId) {
        userRepo.deleteById(userId);
    }

    public User getUserNoDto(UUID userId) {
        User user = userRepo.findById(userId).orElse(null);
        return user ;
    }

    public UserResponse updateUser(UserFulRequest user, UUID userId) {
        // Пытаемся обновить. Если пользователя нет, вернёт 0.
        int updatedRows = userRepo.updateUserFields(
                userId,
                user.name(),
                user.surname(),
                user.email(),
                user.phone()
        );

        if (updatedRows == 0) {
            throw new UserNotFoundException(userId); // Кастомное исключение
        }

        // Возвращаем обновлённые данные
        return new UserResponse(userId, user.name(), user.surname(), user.email(), user.phone());
    }
}