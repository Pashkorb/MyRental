package com.example.rental.favorite;

import com.example.rental.rentalListing.RentalListing;
import com.example.rental.users.UserPrincipal;
import com.example.rental.kafka.KafkaProducerService;
import com.example.rental.rentalListing.RentalListingService;
import com.example.rental.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rental/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private RentalListingService rentalListingService;

    @Autowired
    private UserService userService;

    // Получение списка избранных объявлений
    @GetMapping
    public ResponseEntity<List<RentalListing>> getFavorites() {
        // Получаем текущего пользователя
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID tenantUuid = userPrincipal.getUserId();

        // Получаем избранные объявления для текущего пользователя
        List<Favorite> favorites = favoriteService.getFavoritesByTenant(tenantUuid);

        // Получаем список объявлений из избранных
        List<RentalListing> favoriteListings = favorites.stream()
                .map(Favorite::getListing)
                .collect(Collectors.toList());

        return ResponseEntity.ok(favoriteListings);
    }

    // Добавление объявления в избранное
    @PostMapping("/add/{listingUuid}")
    public ResponseEntity<String> addToFavorites(@PathVariable UUID listingUuid) {
        // Получаем текущего пользователя
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID tenantUuid = userPrincipal.getUserId();

        // Получаем объявление
        RentalListing listing = rentalListingService.getRentalListing(listingUuid);
        if (listing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Listing not found");
        }

        // Проверяем, что объявление не принадлежит текущему пользователю
        if (listing.getProperty().getLandlord().getUuid().equals(tenantUuid)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You cannot add your own listing to favorites");
        }

        // Создаем новое избранное объявление
        Favorite favorite = new Favorite();
        favorite.setTenant(userService.getUserNoDto(tenantUuid));
        favorite.setListing(listing);
        favorite.setAddDate(new Date());

        // Сохраняем избранное объявление
        favoriteService.createFavorite(favorite);

        // Отправляем событие в Kafka
        kafkaProducerService.sendApartmentFavorite(listingUuid, tenantUuid);

        return ResponseEntity.ok("Listing added to favorites");
    }

    // Удаление объявления из избранного
    @DeleteMapping("/remove/{listingUuid}")
    public ResponseEntity<String> removeFromFavorites(@PathVariable UUID listingUuid) {
        // Получаем текущего пользователя
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID tenantUuid = userPrincipal.getUserId();

        // Получаем избранное объявление
        List<Favorite> favorites = favoriteService.getFavoritesByTenant(tenantUuid);
        Favorite favoriteToRemove = favorites.stream()
                .filter(favorite -> favorite.getListing().getUuid().equals(listingUuid))
                .findFirst()
                .orElse(null);

        if (favoriteToRemove != null) {
            // Удаляем избранное объявление
            favoriteService.deleteFavorite(favoriteToRemove.getUuid());
            // Отправляем событие в Kafka
            kafkaProducerService.removeApartmentFavorite(tenantUuid, listingUuid);
            return ResponseEntity.ok("Listing removed from favorites");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Favorite not found");
        }
    }
}