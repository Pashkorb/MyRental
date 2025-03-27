package com.example.rental.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String TOPIC = "apartment-event";

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    @Autowired
    @Qualifier("kafkaTemplateView")
    private KafkaTemplate<String, String> kafkaTemplateView;
    @Autowired
    @Qualifier("kafkaTemplateFavorite")
    private KafkaTemplate<String, String> kafkaTemplateFavorite;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendApartmentView(UUID userId, UUID apartmentId) {
        sendViewEvent(new ApartmentEvent(userId, apartmentId, Events.view));
    }

    public void sendApartmentFavorite(UUID userId, UUID apartmentId) {
        sendFavoriteEvent(new ApartmentEvent(userId, apartmentId, Events.addFavorite));
    }

    public void removeApartmentFavorite(UUID userId, UUID apartmentId) {
        sendFavoriteEvent(new ApartmentEvent(userId, apartmentId, Events.remFavorite));
    }

    private void sendViewEvent(ApartmentEvent event) {
        executorService.submit(() -> {
            try {
                String message = objectMapper.writeValueAsString(event);
                kafkaTemplateView.send(TOPIC, message)
                        .thenAccept(result -> {
                            if (result != null) {
                                logger.info("Sent event: {}", message);
                            }
                        })
                        .exceptionally(ex -> {
                            logger.error("Failed to send event: {}", event.toString(), ex);
                            return null;
                        });
            } catch (JsonProcessingException e) {
                logger.error("Failed to serialize event: {}", event.toString(), e);
            }
        });
    }

    private void sendFavoriteEvent(ApartmentEvent event) {
        executorService.submit(() -> {
            try {
                String message = objectMapper.writeValueAsString(event);
                kafkaTemplateFavorite.send(TOPIC, message).get();
                logger.info("Sent event: {}", message);
            } catch (Exception e) {
                logger.error("Failed to send event: {}", event, e);
            }

        });
    }

}