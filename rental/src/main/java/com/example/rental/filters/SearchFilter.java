package com.example.rental.filters;

import com.example.rental.users.User;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "search_filters")
public class SearchFilter {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uuid")
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "tenant_uuid")
    private User tenant;

    @Column(name = "minimum_price")
    private double minimumPrice;

    @Column(name = "maximum_price")
    private double maximumPrice;

    @Column(name = "minimum_area")
    private double minimumArea;

    @Column(name = "maximum_area")
    private double maximumArea;

    @Column(name = "number_of_rooms")
    private int numberOfRooms;

    @Column(name = "amenities")
    private String amenities;

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

    public double getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(double minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    public double getMaximumPrice() {
        return maximumPrice;
    }

    public void setMaximumPrice(double maximumPrice) {
        this.maximumPrice = maximumPrice;
    }

    public double getMinimumArea() {
        return minimumArea;
    }

    public void setMinimumArea(double minimumArea) {
        this.minimumArea = minimumArea;
    }

    public double getMaximumArea() {
        return maximumArea;
    }

    public void setMaximumArea(double maximumArea) {
        this.maximumArea = maximumArea;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }
// Getters and setters
}