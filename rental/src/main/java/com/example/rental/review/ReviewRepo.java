package com.example.rental.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepo extends JpaRepository<Review, UUID> {

    // Метод для поиска отзывов по UUID объявления
    List<Review> findByListingUuid(UUID listingUuid);

    // Метод для поиска отзывов по UUID арендатора
    List<Review> findByTenantUuid(UUID tenantUuid);
}