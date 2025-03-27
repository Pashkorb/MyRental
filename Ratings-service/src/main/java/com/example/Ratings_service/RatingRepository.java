package com.example.Ratings_service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
    // Проверка, ставил ли пользователь оценку ранее
    boolean existsByRatedUserIdAndRatingUserId(UUID ratedUserId, UUID ratingUserId);

    // Получение оценок за последние 6 месяцев
    @Query("SELECT r FROM Rating r WHERE r.ratedUserId = :ratedUserId AND r.createdAt >= :sixMonthsAgo")
    List<Rating> findRecentRatings(@Param("ratedUserId") UUID ratedUserId, @Param("sixMonthsAgo") LocalDateTime sixMonthsAgo);
}