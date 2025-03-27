package com.example.rental.rentalListing;

import com.example.rental.rentalListing.RentalListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RentalListingRepo extends JpaRepository<RentalListing, UUID>, JpaSpecificationExecutor<RentalListing> {
    List<RentalListing> findByPropertyLandlordUuid(UUID userId);
    List<RentalListing> findByStatus(int status);
}