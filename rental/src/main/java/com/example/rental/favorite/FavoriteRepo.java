package com.example.rental.favorite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FavoriteRepo extends JpaRepository<Favorite, UUID> {

    // Метод для поиска избранных объявлений по tenant_uuid
    List<Favorite> findByTenantUuid(UUID tenantUuid);

    // Проверка, существует ли запись в таблице favorites
    boolean existsByTenantUuidAndListingUuid(UUID tenantUuid, UUID listingUuid);
}
