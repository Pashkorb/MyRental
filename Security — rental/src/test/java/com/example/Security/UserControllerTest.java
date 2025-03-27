package com.example.Security;

import com.example.Security.controller.UserController;
import com.example.Security.fieng.RentalFiegn;
import com.example.Security.model.*;
import com.example.Security.model.dto.UserDTO;
import com.example.Security.model.enums.Role;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import com.example.Security.repo.RefreshTokenRepository;
import com.example.Security.repo.UserRepo;
import com.example.Security.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserControllerTest {
    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepo userRepository;

    @MockBean
    private EmailService emailService;

    @MockBean // Мокируем RedisService
    private RedisService redisService;

    @MockBean
    private RentalFiegn rentalFiegn;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        when(rentalFiegn.createUser(anyString(),any())).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(null));

    }

    @Test
    public void testRegister() {
        UserDTO userDTO = new UserDTO("password", "testUser", "test@example.com");

        ResponseEntity<?> response = userController.register(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Проверяем, что токен присутствует и не пустой
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody.get("token"));
        assertEquals(jwtService.extractUserName(responseBody.get("token")),userDTO.getLogin());
        assertFalse(responseBody.get("token").isEmpty());
    }

    @Test
    public void testLogin() {
        // Сначала регистрируем пользователя
        UserDTO userDTO = new UserDTO("password", "testUser", "test@example.com");

        userController.register(userDTO);

        // Теперь пытаемся войти
        UserRequestLoginPassword loginRequest = new UserRequestLoginPassword("password", "testUser");
        ResponseEntity<?> response = userController.login(loginRequest);
        Map<String, String> responseBody = (Map<String, String>) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwtService.extractUserName(responseBody.get("token")),userDTO.getLogin());

        // Теперь пытаемся войти по логину
        UserRequestLoginPassword loginRequest2 = new UserRequestLoginPassword("password", "test@example.com");
        ResponseEntity<?> response2 = userController.login(loginRequest2);
        Map<String, String> responseBody2 = (Map<String, String>) response2.getBody();

        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(jwtService.extractUserName(responseBody2.get("token")),userDTO.getLogin());
    }

    @Test
    public void testRefreshToken() {
        // Регистрируем пользователя
        UserDTO userDTO = new UserDTO("password", "testUser", "test@example.com");

        ResponseEntity<?> response=userController.register(userDTO);
        Map<String,String> body=(Map<String,String>)response.getBody();
        String token=body.get("token");

        // Обновляем токен
        ResponseEntity<?> response2 = userController.refreshToken("Bearer " + token);

        assertEquals(HttpStatus.OK, response2.getStatusCode(),"ожидается 200");
        Map<String,String> body2=(Map<String, String>) response2.getBody();
        assertEquals(jwtService.extractUserName(body2.get("accessToken")),jwtService.extractUserName(token));
    }

    @Test
    public void testLogout() {
        // Регистрируем пользователя
        UserDTO userDTO = new UserDTO("password", "testUser", "test@example.com");

        ResponseEntity<?> response =userController.register(userDTO);
        Map<String,String> body=(Map<String,String>)response.getBody();
        String accessToken=body.get("token");

        Users user= userRepository.findByLogin("testUser").get();

        // Выход из системы
        ResponseEntity<?> response2 = userController.logout("Bearer " + accessToken);

        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assert(refreshTokenRepository.findByUser(user).isEmpty());
    }

    @Test
    public void testPasswordResetFlow() {
        // Регистрация пользователя (реальная, без моков)
        UserDTO userDTO = new UserDTO("oldPassword", "testUser", "test@example.com");
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyInt());
        ResponseEntity<?> registerResponse = userController.register(userDTO);
        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());

        // 1. Тестирование /forgot-password
        // Мокируем сохранение кода в Redis
        doNothing().when(redisService).saveResetCode(anyString(), anyInt(), anyLong());

        ResponseEntity<?> forgotResponse = userController.forgotPassword("test@example.com");
        assertEquals(HttpStatus.OK, forgotResponse.getStatusCode());

        // Захватываем код, который был отправлен в Redis
        ArgumentCaptor<Integer> codeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(redisService).saveResetCode(eq("test@example.com"), codeCaptor.capture(), eq(15L));
        Integer resetCode = codeCaptor.getValue();

        // 2. Тестирование /reset-password
        // Мокируем проверку кода
        when(redisService.getResetCode("test@example.com")).thenReturn(resetCode);
        PasswordResetRequest request = new PasswordResetRequest(
                resetCode,
                "newSecurePassword123",
                "test@example.com"
        );

        ResponseEntity<?> resetResponse = userController.resetPassword(request);
        assertEquals(HttpStatus.OK, resetResponse.getStatusCode());

        // 3. Проверяем, что пароль действительно изменился
        // Пытаемся войти с новым паролем (реальный вызов)
        UserRequestLoginPassword newLoginRequest = new UserRequestLoginPassword(
                "newSecurePassword123",
                "testUser"
        );
        ResponseEntity<?> newLoginResponse = userController.login(newLoginRequest);
        assertEquals(HttpStatus.OK, newLoginResponse.getStatusCode());

        // 4. Проверяем, что старый пароль больше не работает
        UserRequestLoginPassword oldLoginRequest = new UserRequestLoginPassword(
                "oldPassword",
                "testUser"
        );
        ResponseEntity<?> oldLoginResponse = userController.login(oldLoginRequest);
        assertEquals(HttpStatus.UNAUTHORIZED, oldLoginResponse.getStatusCode());
    }

}