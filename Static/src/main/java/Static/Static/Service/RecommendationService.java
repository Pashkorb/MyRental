package Static.Static.Service;

import Static.Static.Repository.FavoriteRepo;
import Static.Static.Repository.ViewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationService  {
    @Autowired
    ViewRepo viewRepo;
    @Autowired
    FavoriteRepo favoriteRepo;
    public List<UUID> getRecomendations (UUID userId){
        System.out.println("Start find last view");

        List<UUID> lastView=viewRepo.findRecentViewsByUserId(userId,500);
        System.out.println("Start find last users");

        List<UUID> similarUsers=findSimilarUsers(lastView);
        System.out.println("Start find last recomm");

        List<UUID> recommendation = favoriteRepo.findListingIdsByUserIds(similarUsers);
        System.out.println("return recomm");

        return recommendation;
    }

    private List<UUID> findSimilarUsers(List<UUID> lastView) {
        Set<UUID> users = viewRepo.findUserIdsByListingIds(lastView);
        return users.stream().toList();
    }

}
