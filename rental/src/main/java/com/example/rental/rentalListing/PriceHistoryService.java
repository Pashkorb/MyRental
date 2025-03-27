package com.example.rental.rentalListing;

import com.example.rental.model.PriceHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PriceHistoryService {

    @Autowired
    private PriceHistoryRepo priceHistoryRepo;

    // Создание новой записи в истории цен
    public PriceHistory createPriceHistory(PriceHistory priceHistory) {
        return priceHistoryRepo.save(priceHistory);
    }

    // Получение записи истории цен по UUID
    public PriceHistory getPriceHistory(UUID uuid) {
        return priceHistoryRepo.findById(uuid).orElse(null);
    }

    // Получение всех записей истории цен
    public List<PriceHistory> getAllPriceHistories() {
        return priceHistoryRepo.findAll();
    }

    // Получение всей истории изменений цен для конкретного объявления
    public List<PriceHistory> getPriceHistoryByListing(UUID listingUuid) {
        return priceHistoryRepo.findByRentalListingUuid(listingUuid);
    }

    // Обновление записи истории цен
    public PriceHistory updatePriceHistory(PriceHistory priceHistory) {
        if (priceHistoryRepo.existsById(priceHistory.getUuid())) {
            return priceHistoryRepo.save(priceHistory);
        } else {
            return null;
        }
    }

    // Удаление записи истории цен
    public void deletePriceHistory(UUID uuid) {
        priceHistoryRepo.deleteById(uuid);
    }
}