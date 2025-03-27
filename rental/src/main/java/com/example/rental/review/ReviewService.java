package com.example.rental.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepo reviewRepo;

    // Создание нового отзыва
    public Review createReview(Review review) {
        return reviewRepo.save(review);
    }

    // Получение отзыва по UUID
    public Review getReview(UUID uuid) {
        return reviewRepo.findById(uuid).orElse(null);
    }

    // Получение всех отзывов
    public List<Review> getAllReviews() {
        return reviewRepo.findAll();
    }

    // Получение всех отзывов для конкретного объявления
    public List<Review> getReviewsByListing(UUID listingUuid) {
        return reviewRepo.findByListingUuid(listingUuid);
    }

    // Получение всех отзывов для конкретного арендатора
    public List<Review> getReviewsByTenant(UUID tenantUuid) {
        return reviewRepo.findByTenantUuid(tenantUuid);
    }

    // Обновление отзыва
    public Review updateReview(Review review) {
        if (reviewRepo.existsById(review.getUuid())) {
            return reviewRepo.save(review);
        } else {
            return null;
        }
    }

    // Удаление отзыва
    public void deleteReview(UUID uuid) {
        reviewRepo.deleteById(uuid);
    }
}