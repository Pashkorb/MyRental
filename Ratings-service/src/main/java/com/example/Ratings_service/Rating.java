package com.example.Ratings_service;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID ratedUserId;  // ID пользователя, которому поставили оценку
    private UUID ratingUserId; // ID пользователя, который поставил оценку
    private int ratingValue;   // Значение оценки
    @CurrentTimestamp
    private LocalDateTime createdAt; // Дата и время оценки

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRatedUserId() {
        return ratedUserId;
    }

    public void setRatedUserId(UUID ratedUserId) {
        this.ratedUserId = ratedUserId;
    }

    public UUID getRatingUserId() {
        return ratingUserId;
    }

    public void setRatingUserId(UUID ratingUserId) {
        this.ratingUserId = ratingUserId;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}