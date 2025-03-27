package com.example.rental.property;

import com.example.rental.users.User;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "properties")
public class Property implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "address")
    private String address;

    @Column(name = "property_type")
    private String propertyType;

    @Column(name = "total_area")
    private double totalArea;

    @Column(name = "number_of_rooms")
    private int numberOfRooms;

    @Column(name = "floor")
    private int floor;

    @Column(name = "amenities")
    private String amenities;

    @ManyToOne
    @JoinColumn(name = "landlord_uuid")
    private User landlord;

    public Property() {
    }

    public Property(String address, String propertyType, double totalArea, int numberOfRooms, int floor, String amenities, User landlord) {
        this.address = address;
        this.propertyType = propertyType;
        this.totalArea = totalArea;
        this.numberOfRooms = numberOfRooms;
        this.floor = floor;
        this.amenities = amenities;
        this.landlord = landlord;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public double getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(double totalArea) {
        this.totalArea = totalArea;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public User getLandlord() {
        return landlord;
    }

    public void setLandlord(User landlord) {
        this.landlord = landlord;
    }
}