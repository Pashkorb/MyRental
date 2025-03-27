package com.example.Ratings_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    // Добавление оценки
    public void addRating(UUID ratedUserId, UUID ratingUserId, int ratingValue) {

        if (ratingValue < 1 || ratingValue > 5) {
            throw new IllegalArgumentException("Оценка должна быть от 1 до 5");
        }

        if (ratingRepository.existsByRatedUserIdAndRatingUserId(ratedUserId, ratingUserId)) {
            throw new IllegalArgumentException("Пользователь уже поставил оценку");
        }

        Rating rating = new Rating();
        rating.setRatedUserId(ratedUserId);
        rating.setRatingUserId(ratingUserId);
        rating.setRatingValue(ratingValue);
        rating.setCreatedAt(LocalDateTime.now());

        ratingRepository.save(rating);
    }

    // Получение рейтинга пользователя
    public double getRating(UUID ratedUserId) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        List<Rating> recentRatings = ratingRepository.findRecentRatings(ratedUserId, sixMonthsAgo);

        if (recentRatings.isEmpty()) {
            return 0.0;
        }

        double sum = recentRatings.stream().mapToInt(Rating::getRatingValue).sum();
        return sum / recentRatings.size();
    }
}