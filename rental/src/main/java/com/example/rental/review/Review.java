package com.example.rental.review;

import com.example.rental.rentalListing.RentalListing;
import com.example.rental.users.User;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uuid")
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "tenant_uuid")
    private User tenant;

    @ManyToOne
    @JoinColumn(name = "listing_uuid")
    private RentalListing listing;

    @Column(name = "rating")
    private int rating;

    @Column(name = "review_text")
    private String reviewText;

    @Column(name = "review_date")
    private Date reviewDate;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public User getTenant() {
        return tenant;
    }

    public void setTenant(User tenant) {
        this.tenant = tenant;
    }

    public RentalListing getListing() {
        return listing;
    }

    public void setListing(RentalListing listing) {
        this.listing = listing;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }
// Getters and setters
}