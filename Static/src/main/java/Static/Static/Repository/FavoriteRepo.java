package Static.Static.Repository;

import Static.Static.Models.Favorite;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FavoriteRepo extends JpaRepository<Favorite, UUID> {

    @Query("SELECT DISTINCT f.rentalId FROM Favorite f WHERE f.userId IN :userIds")
    List<UUID> findListingIdsByUserIds(@Param("userIds") List<UUID> userIds);
}