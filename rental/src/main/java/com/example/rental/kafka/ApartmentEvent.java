package com.example.rental.kafka;

import com.example.rental.kafka.Events;

import java.util.UUID;

public class ApartmentEvent {
    private final UUID userId;
    private final UUID apartmentId;
    private final Events event;

    public ApartmentEvent(UUID userId, UUID apartmentId, Events event) {
        this.userId = userId;
        this.apartmentId = apartmentId;
        this.event = event;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getApartmentId() {
        return apartmentId;
    }

    public Events getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return "ApartmentEvent{" +
                "userId=" + userId +
                ", apartmentId=" + apartmentId +
                ", event=" + event +
                '}';
    }
}