package com.example.Security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String email, int token) {

        // Формируем текст письма
        String emailText = "Ваш секретный код - " + token;

        // Отправляем письмо
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("myRentalsuppot@yandex.ru");
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText(emailText);
        mailSender.send(message);
    }
}