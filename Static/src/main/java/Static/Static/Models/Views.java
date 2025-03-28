package Static.Static.Models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;
@Entity
//@Table(uniqueConstraints = {
//        @UniqueConstraint(columnNames = {"userId", "rentalId"})
//})
public class Views {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID userId;
    private UUID rentalId;
    @CreationTimestamp
    private LocalDateTime viewedAt;

    public Views(UUID userId, UUID rentalId) {
        this.userId = userId;
        this.rentalId = rentalId;
    }
}
