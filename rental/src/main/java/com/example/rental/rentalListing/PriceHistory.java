package com.example.rental.model;

import com.example.rental.rentalListing.RentalListing;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "price_history")
public class PriceHistory {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uuid")
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_listing_uuid")  // Ссылается на колонку в БД
    private RentalListing rentalListing;  // Правильная ссылка на сущность

    @Column(name = "change_date")
    @CreationTimestamp
    private Date changeDate;

    @Column(name = "old_price")
    private double oldPrice;

    @Column(name = "new_price")
    private double newPrice;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public RentalListing getRentalListing() {
        return rentalListing;
    }

    public void setRentalListing(RentalListing rentalListing) {
        this.rentalListing = rentalListing;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }

    public PriceHistory(RentalListing rentalListing, Date changeDate, double oldPrice, double newPrice) {
        this.rentalListing = rentalListing;
        this.changeDate = changeDate;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }

    public PriceHistory() {
    }
}