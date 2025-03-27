package com.example.rental.favorite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepo favoriteRepo;

    // Создание нового избранного объявления
    public Favorite createFavorite(Favorite favorite) {
        return favoriteRepo.save(favorite);
    }

    // Получение избранного объявления по UUID
    public Favorite getFavorite(UUID uuid) {
        return favoriteRepo.findById(uuid).orElse(null);
    }

    // Получение всех избранных объявлений
    public List<Favorite> getAllFavorites() {
        return favoriteRepo.findAll();
    }

    // Получение всех избранных объявлений для конкретного арендатора
    public List<Favorite> getFavoritesByTenant(UUID tenantUuid) {
        return favoriteRepo.findByTenantUuid(tenantUuid);
    }

    // Обновление избранного объявления
    public Favorite updateFavorite(Favorite favorite) {
        if (favoriteRepo.existsById(favorite.getUuid())) {
            return favoriteRepo.save(favorite);
        } else {
            return null;
        }
    }
    // Проверка, находится ли объявление в избранном
    public boolean isFavorite(UUID tenantUuid, UUID listingUuid) {
        return favoriteRepo.existsByTenantUuidAndListingUuid(tenantUuid, listingUuid);
    }
    // Удаление избранного объявления
    public void deleteFavorite(UUID uuid) {
        favoriteRepo.deleteById(uuid);
    }
}