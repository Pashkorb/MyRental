package Static.Static.Service;

import Static.Static.Models.Favorite;
import Static.Static.Models.Views;
import Static.Static.Repository.FavoriteRepo;
import Static.Static.Repository.ViewRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private FavoriteRepo favoriteRepo;

    @Autowired
    private ViewRepo viewRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "apartment-event", groupId = "statistics-group")
    public void listenFavorite(String message) {
        try {
            // Десериализация JSON в Map
            var jsonNode = objectMapper.readTree(message);

            UUID userId = UUID.fromString(jsonNode.get("userId").asText());
            UUID apartmentId = UUID.fromString(jsonNode.get("apartmentId").asText());
            String event = jsonNode.get("event").asText();

            // Обработка события
            switch (event) {
                case "view":
//                    try {
                        viewRepo.save(new Views(userId, apartmentId));
                        logger.info("Saved view: userId={}, rentalId={}", userId, apartmentId);
//                    }catch (DataIntegrityViolationException e){
//                        logger.warn("Duplicate view: userId={}, rentalId={}", userId, apartmentId);
//                    }
//                    break;

                case "addFavorite":
                    favoriteRepo.save(new Favorite(userId, apartmentId));
                    logger.info("Added favorite: userId={}, rentalId={}", userId, apartmentId);
                    break;
                case "remFavorite":
                    favoriteRepo.delete(new Favorite(userId, apartmentId));
                    logger.info("Removed favorite: userId={}, rentalId={}", userId, apartmentId);
                    break;
                default:
                    logger.warn("Unknown event type: {}", event);
            }
        } catch (Exception e) {
            logger.error("Failed to process message: {}", message, e);
        }
    }
}