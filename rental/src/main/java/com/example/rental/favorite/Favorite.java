package com.example.rental.favorite;

import com.example.rental.rentalListing.RentalListing;
import com.example.rental.users.User;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "favorites")
public class Favorite {

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

    @Column(name = "add_date")
    private Date addDate;

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

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }
// Getters and setters
}