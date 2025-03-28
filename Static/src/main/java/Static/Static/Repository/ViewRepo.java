package Static.Static.Repository;

import Static.Static.Models.Favorite;
import Static.Static.Models.Views;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;


@Repository
public interface ViewRepo extends JpaRepository<Views, UUID> {

    // Получаем последние просмотренные rentalId для пользователя
    @Query(value = "SELECT v.rental_id FROM views v WHERE v.user_id = :userId ORDER BY v.viewed_at DESC LIMIT :limit", nativeQuery = true)
    List<UUID> findRecentViewsByUserId(@Param("userId") UUID userId, @Param("limit") int limit);

    // Получаем всех пользователей, которые просматривали конкретные rentalId
    @Query("SELECT DISTINCT v.userId FROM Views v WHERE v.rentalId IN :rentalIds")
    Set<UUID> findUserIdsByListingIds(@Param("rentalIds") List<UUID> rentalIds);


}
