package com.example.Ratings_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {
    @Autowired
    RatingService ratingService;

    @PostMapping
    public ResponseEntity<String> addRating(@RequestParam UUID ratingUserId, @RequestParam int ratingValue) {
        UUID ratedUserId= (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ratingService.addRating(ratedUserId, ratingUserId, ratingValue);
        return ResponseEntity.ok("Оценка добавлена");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Double> getRating(@PathVariable UUID userId) {
        double rating = ratingService.getRating(userId);
        return ResponseEntity.ok(rating);
    }

}
