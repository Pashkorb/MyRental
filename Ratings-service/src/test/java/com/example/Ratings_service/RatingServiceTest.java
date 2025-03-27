package com.example.Ratings_service;

import com.example.Ratings_service.Rating;
import com.example.Ratings_service.RatingRepository;
import com.example.Ratings_service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Подключаем Mockito
public class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository; // Мокируем репозиторий

    @InjectMocks
    private RatingService ratingService; // Внедряем моки в сервис

    private UUID user1;
    private UUID user2;

    @BeforeEach
    void setUp() {
        user1 = UUID.randomUUID();
        user2 = UUID.randomUUID();
    }

    @Test
    void addRating_shouldSaveRating_whenUserHasNotRatedBefore() {
        // Arrange
        when(ratingRepository.existsByRatedUserIdAndRatingUserId(user1, user2)).thenReturn(false);

        // Act
        ratingService.addRating(user1, user2, 5);

        // Assert
        verify(ratingRepository, times(1)).save(any(Rating.class)); // Проверяем, что save был вызван
    }

    @Test
    void addRating_shouldThrowException_whenUserHasAlreadyRated() {
        // Arrange
        when(ratingRepository.existsByRatedUserIdAndRatingUserId(user1, user2)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ratingService.addRating(user1, user2, 5)
        );
        assertEquals("Пользователь уже поставил оценку", exception.getMessage());
    }

    @Test
    void addRating_shouldThrowException_whenRatingValueIsInvalid() {
        // Arrange
        when(ratingRepository.existsByRatedUserIdAndRatingUserId(user1, user2)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                ratingService.addRating(user1, user2, 0) // Оценка меньше 1
        );
        assertThrows(IllegalArgumentException.class, () ->
                ratingService.addRating(user1, user2, 6) // Оценка больше 5
        );
    }

    @Test
    void getRating_shouldReturnZero_whenNoRatingsExist() {
        // Arrange
        when(ratingRepository.findRecentRatings(eq(user1), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        double rating = ratingService.getRating(user1);

        // Assert
        assertEquals(0.0, rating);
    }

    @Test
    void getRating_shouldCalculateAverage_whenRatingsExist() {
        // Arrange
        Rating rating1 = new Rating();
        rating1.setRatingValue(4);
        Rating rating2 = new Rating();
        rating2.setRatingValue(5);

        when(ratingRepository.findRecentRatings(eq(user1), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(rating1, rating2));

        // Act
        double rating = ratingService.getRating(user1);

        // Assert
        assertEquals(4.5, rating); // (4 + 5) / 2 = 4.5
    }

    @Test
    void getRating_shouldOnlyConsiderRecentRatings() {
        // Arrange
        Rating recentRating = new Rating();
        recentRating.setRatingValue(5);
        recentRating.setCreatedAt(LocalDateTime.now().minusMonths(3)); // Оценка за последние 6 месяцев

        Rating oldRating = new Rating();
        oldRating.setRatingValue(1);
        oldRating.setCreatedAt(LocalDateTime.now().minusMonths(7)); // Оценка старше 6 месяцев

        when(ratingRepository.findRecentRatings(eq(user1), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(recentRating));

        // Act
        double rating = ratingService.getRating(user1);

        // Assert
        assertEquals(5.0, rating); // Учитывается только recentRating
    }
}