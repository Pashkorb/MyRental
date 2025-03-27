package com.example.rental.rentalListing;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient("Static")
public interface FeingRecomendation {
    @GetMapping("/Recomendation")
    public List<UUID> getRecommendation(@RequestHeader UUID userId);

}
