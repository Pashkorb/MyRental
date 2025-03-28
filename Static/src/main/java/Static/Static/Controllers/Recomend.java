package Static.Static.Controllers;

import Static.Static.Service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class Recomend {
    @Autowired
    RecommendationService recommendationService;
    @GetMapping("/Recomendation")
    public List<UUID> getRecomendation(@RequestHeader UUID userId){
        System.out.println("Start recommend");
        return recommendationService.getRecomendations(userId);
    }


}
