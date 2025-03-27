package com.example.rental.rentalListing;

import com.example.rental.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PriceHistoryRepo extends JpaRepository<PriceHistory, UUID> {
    List<PriceHistory> findByRentalListingUuid(UUID rentalListingUuid);

    // Метод для поиска истории изменений цен по UUID объявления
//    List<PriceHistory> findByuuid(UUID listingUuid);
}