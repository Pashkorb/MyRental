package com.example.rental.rentalListing;

import com.example.rental.property.Property;
import com.example.rental.filters.SearchFilter;
import com.example.rental.security.JwtTokenUtil;
import com.example.rental.users.UserPrincipal;
import com.example.rental.property.PropertyService;
import com.example.rental.filters.SearchFilterService;
import com.example.rental.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/rental/rentalListings")
public class RentalListingController {

    @Autowired
    UserService userService;

    @Autowired
    private RentalListingService rentalListingService;

    @Autowired
    private SearchFilterService searchFilterService;

    @Autowired
    FeingRecomendation feign;



    @Autowired
    private PropertyService propertyService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final Logger logger = LoggerFactory.getLogger(RentalListingController.class);


    // Создание нового объявления
    @PostMapping("/create")
    public ResponseEntity<RentalListing> createRentalListing(@RequestBody RentalListing rentalListing) {
        Property property = propertyService.createProperty(rentalListing.getProperty());
        rentalListing.setProperty(property);

        // Получаем пользователя из контекста безопасности
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID userId = userPrincipal.getUserId();
        property.setLandlord(userService.getUserNoDto(userId));

        RentalListing createdListing = rentalListingService.createRentalListing(rentalListing);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdListing);
    }

    // Получение всех объявлений
    @GetMapping("/all")
    public ResponseEntity<List<RentalListing>> getAllRentalListings() {
        List<RentalListing> rentalListings = rentalListingService.getAllRentalListings();
        return ResponseEntity.ok(rentalListings);
    }

    // Поиск по фильтрам
    @PostMapping("/search")
    public ResponseEntity<List<RentalListing>> searchListingsByFilter(@RequestBody SearchFilter searchFilter) {
        List<RentalListing> listings = rentalListingService.searchListingsByFilter(searchFilter);
        return ResponseEntity.ok(listings);
    }

    // Получение рекомендованных объявлений для авторизованного пользователя
    @GetMapping("/recommendations")
    public ResponseEntity<List<RentalListing>> getRecommendedListings() {
        List<RentalListing> recommendedListings = new ArrayList<>();
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userPrincipal != null) {
            UUID userId = userPrincipal.getUserId();

            // Асинхронно получаем рекомендации с тайм-аутом
            CompletableFuture<List<UUID>> recommendationFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return feign.getRecommendation(userId);
                } catch (Exception e) {
                    System.err.println("Ошибка при получении рекомендаций: " + e.getMessage());
                    return Collections.emptyList();
                }
            });

            try {
                List<UUID> recommIds = recommendationFuture.get(2, TimeUnit.SECONDS);
                if (recommIds != null && !recommIds.isEmpty()) {
                    recommendedListings = rentalListingService.findAllById(recommIds);
                }
            } catch (TimeoutException e) {
                System.err.println("Тайм-аут при получении рекомендаций");
            } catch (Exception e) {
                System.err.println("Ошибка при получении рекомендаций: " + e.getMessage());
            }
        }

        return ResponseEntity.ok(recommendedListings);
    }

    // Получение объявления по UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<RentalListing> getRentalListing(@PathVariable UUID uuid) {
        RentalListing rentalListing = rentalListingService.getRentalListing(uuid);
        if (rentalListing != null) {
            return ResponseEntity.ok(rentalListing);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Обновление объявления
    @PutMapping("/update/{uuid}")
    public ResponseEntity<RentalListing> updateRentalListing(@PathVariable UUID uuid, @RequestBody RentalListing rentalListing) {
        if (rentalListingService.getRentalListing(uuid) != null) {
            rentalListing.setUuid(uuid);
            RentalListing updatedListing = rentalListingService.updateRentalListing(rentalListing);
            return ResponseEntity.ok(updatedListing);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Удаление объявления
    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<Void> deleteRentalListing(@PathVariable UUID uuid) {
        if (rentalListingService.getRentalListing(uuid) != null) {
            rentalListingService.deleteRentalListing(uuid);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("{id}/addPhoto")
    public ResponseEntity<?> addPhoto(@PathVariable UUID id, @RequestParam("files")MultipartFile[] files, HttpServletRequest request){
        UserPrincipal user=(UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RentalListing listing=rentalListingService.getRentalListing(id);

        // Проверка, что объявление существует
        if (listing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Listing not found"));
        }

        // Проверка прав пользователя
        if (!user.getUserId().equals(listing.getProperty().getLandlord().getUuid())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "You do not have permission"));
        }


        List<String> url=rentalListingService.addPhoto(listing,files,user.getToken());


        return ResponseEntity.status(HttpStatus.OK).body(url);
    }

    @DeleteMapping("/photos/{id}/{url}")
    public ResponseEntity<?> deletePhoto(
            @PathVariable String url,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token) {

        UserPrincipal user=(UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RentalListing listing=rentalListingService.getRentalListing(id);

        // Проверка, что объявление существует
        if (listing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Listing not found"));
        }

        // Проверка прав пользователя
        if (!user.getUserId().equals(listing.getProperty().getLandlord().getUuid())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "You do not have permission"));
        }

        // Проверка, что фотография существует в списке
        if (!listing.getPhotoName().contains(url)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Photo not found"));
        }

        logger.info("Deleting photo with URL: {} for student with ID: {}", url, id);

        rentalListingService.removePhoto(listing, url,token);
        logger.info("Photo deleted successfully for student with ID: {}", id);
        return ResponseEntity.noContent().build();
    }


}
