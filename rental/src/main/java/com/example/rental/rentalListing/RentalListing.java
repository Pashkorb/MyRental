package com.example.rental.rentalListing;

import com.example.rental.model.PriceHistory;
import com.example.rental.property.Property;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rental_listings")
public class RentalListing implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uuid")
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "property_uuid")
    private Property property;

    @Column(name = "title")
    private String title;

    @ElementCollection
    private List<String> photoUrl =new ArrayList<>();

    @Column(name = "description")
    private String description;

    @Column(name = "rental_cost")
    private double rentalCost;

    @Column(name = "publication_date")
    private Date publicationDate;

    @Column(name = "status")
    private int status;

    @Column(name = "rental_conditions")
    private String rentalConditions;

    @Column(name = "corporate_conditions")
    private String corporateConditions;

    @OneToMany(mappedBy = "rentalListing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceHistory> priceHistory = new ArrayList<>();

    public List<String> getPhotoName() {
        return photoUrl;
    }

    public void setPhotoName(List<String> photoName) {
        this.photoUrl = photoName;
    }

    public List<PriceHistory> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(List<PriceHistory> priceHistory) {
        this.priceHistory = priceHistory;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRentalCost() {
        return rentalCost;
    }

    public void setRentalCost(double rentalCost) {
        this.rentalCost = rentalCost;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRentalConditions() {
        return rentalConditions;
    }

    public void setRentalConditions(String rentalConditions) {
        this.rentalConditions = rentalConditions;
    }

    public String getCorporateConditions() {
        return corporateConditions;
    }

    public void setCorporateConditions(String corporateConditions) {
        this.corporateConditions = corporateConditions;
    }

    public List<String> getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(List<String> photoUrl) {
        this.photoUrl = photoUrl;
    }
}