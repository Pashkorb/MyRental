package Static.Static.Models;

import Static.Static.enums.Events;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

public class ApartmentEvent {
    private final UUID userId;
    private final UUID apartmentId;
    private final Events event;

    @JsonCreator
    public ApartmentEvent(
            @JsonProperty("userId") UUID userId,
            @JsonProperty("apartmentId") UUID apartmentId,
            @JsonProperty("event") Events event) {
        this.userId = userId;
        this.apartmentId = apartmentId;
        this.event = event;
    }

    // Геттеры
    public UUID getUserId() {
        return userId;
    }

    public UUID getApartmentId() {
        return apartmentId;
    }

    public Events getEvent() {
        return event;
    }
}