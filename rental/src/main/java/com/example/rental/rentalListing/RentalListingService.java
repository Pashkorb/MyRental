package com.example.rental.rentalListing;

import com.example.rental.file.UploadFileResponse;
import com.example.rental.file.FileServiceClient;
import com.example.rental.model.PriceHistory;
import com.example.rental.filters.SearchFilter;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable; // Правильный импорт
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RentalListingService {

    @Autowired
    PriceHistoryService priceHistoryService;
    @Autowired
    FileServiceClient fileServiceClient;
    @Autowired
    private RentalListingRepo rentalListingRepo;
    private static final Logger logger = LoggerFactory.getLogger(RentalListingService.class);


    @Transactional
    public void removePhoto(RentalListing listing, String url, String token) {
        fileServiceClient.deleteFile(url,token);
        listing.getPhotoUrl().remove(url);
        rentalListingRepo.save(listing);
    }

    @Transactional
    public List<String> addPhoto(RentalListing listing, MultipartFile[] files,String token) {
        List<String> urls = fileServiceClient.uploadMultipleFiles(files,token).stream()
                .map(UploadFileResponse::getFileDownloadUri)
                .toList();
        listing.getPhotoUrl().addAll(urls);
        rentalListingRepo.save(listing);
        return urls;
    }

    public RentalListing createRentalListing(RentalListing rentalListing) {
        rentalListing.setPublicationDate(new Date()); // Устанавливаем текущую дату
        rentalListing.setStatus(0); // Устанавливаем статус по умолчанию
        return rentalListingRepo.save(rentalListing);
    }

    // Получение объявления по UUID
    @Cacheable(value = "rentalListing",key="#uuid")
    public RentalListing getRentalListing(UUID uuid) {
        return rentalListingRepo.findById(uuid).orElse(null);
    }

    // Получение всех объявлений
    public List<RentalListing> getAllRentalListings() {
        return rentalListingRepo.findByStatus(0); // 0 - статус "акивно"
    }

    // Обновление объявления
    @Transactional
    @CachePut(value = "rentalListings", key = "#rentalListing.uuid")
    public RentalListing updateRentalListing(RentalListing rentalListing) {
        // Находим существующий RentalListing
        Optional<RentalListing> optional = rentalListingRepo.findById(rentalListing.getUuid());
        if (optional.isPresent()) {
            RentalListing existingListing = optional.get();

            // Обновляем поля существующего RentalListing
            existingListing.setTitle(rentalListing.getTitle());
            existingListing.setDescription(rentalListing.getDescription());
            existingListing.setRentalCost(rentalListing.getRentalCost());
            existingListing.setPublicationDate(rentalListing.getPublicationDate());
            existingListing.setStatus(rentalListing.getStatus());
            existingListing.setRentalConditions(rentalListing.getRentalConditions());
            existingListing.setCorporateConditions(rentalListing.getCorporateConditions());

            // Создаем новую запись в истории цен
            PriceHistory newPriceHistory = new PriceHistory();
            newPriceHistory.setRentalListing(existingListing);  // Устанавливаем связь
            newPriceHistory.setNewPrice(existingListing.getRentalCost());  // Текущая цена

            // Добавляем новую запись в историю цен
            existingListing.getPriceHistory().add(newPriceHistory);

            // Сохраняем обновленный RentalListing (каскадно сохранит PriceHistory)
            return rentalListingRepo.save(existingListing);
        } else {
            return null;  // Если RentalListing не найден
        }
    }

    // Удаление объявления
    @CacheEvict(value = "rentalListings", key = "#uuid")
    public void deleteRentalListing(UUID uuid) {
        rentalListingRepo.deleteById(uuid);
    }
    // Поиск объявлений с учетом фильтров

    public List<RentalListing> searchListingsByFilter(SearchFilter searchFilter) {
        Specification<RentalListing> spec = RentalListingSpecification.filterBySearchFilter(searchFilter);
        return rentalListingRepo.findAll(spec);
    }

    public List<RentalListing> getListingsByUser(UUID userId) {
        return rentalListingRepo.findByPropertyLandlordUuid(userId);
    }

    public List<RentalListing> findAllById(List<UUID> recommId) {
        return rentalListingRepo.findAllById(recommId);
    }

    private String extractFileName(String url) {
        logger.debug("Extracting file name from URL: {}", url);
        return url.substring(url.lastIndexOf('/') + 1);
    }
}