package com.example.rental.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rental/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Создание нового отзыва
    @PostMapping("/create")
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        Review createdReview = reviewService.createReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    // Получение отзыва по UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<Review> getReview(@PathVariable UUID uuid) {
        Review review = reviewService.getReview(uuid);
        if (review != null) {
            return ResponseEntity.ok(review);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Получение всех отзывов
    @GetMapping("/all")
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    // Получение всех отзывов для конкретного объявления
    @GetMapping("/listing/{listingUuid}")
    public ResponseEntity<List<Review>> getReviewsByListing(@PathVariable UUID listingUuid) {
        List<Review> reviews = reviewService.getReviewsByListing(listingUuid);
        return ResponseEntity.ok(reviews);
    }

    // Получение всех отзывов для конкретного арендатора
    @GetMapping("/tenant/{tenantUuid}")
    public ResponseEntity<List<Review>> getReviewsByTenant(@PathVariable UUID tenantUuid) {
        List<Review> reviews = reviewService.getReviewsByTenant(tenantUuid);
        return ResponseEntity.ok(reviews);
    }

    // Обновление отзыва
    @PutMapping("/update/{uuid}")
    public ResponseEntity<Review> updateReview(@PathVariable UUID uuid, @RequestBody Review review) {
        if (reviewService.getReview(uuid) != null) {
            review.setUuid(uuid);
            Review updatedReview = reviewService.updateReview(review);
            return ResponseEntity.ok(updatedReview);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Удаление отзыва
    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID uuid) {
        if (reviewService.getReview(uuid) != null) {
            reviewService.deleteReview(uuid);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}