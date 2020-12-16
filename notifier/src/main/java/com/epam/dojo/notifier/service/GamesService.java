package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.model.Contest;
import com.epam.dojo.notifier.model.Game;
import com.epam.dojo.notifier.model.GamesList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GamesService {

    @Value("${gamesApi}")
    private String gamesApi;

    private final RestTemplate restTemplate;

    private Map<String, Game> gameRepo;
    private final Map<String, Contest> contestRepo = new HashMap<>();

    @Autowired
    public GamesService() {
        this.restTemplate = new RestTemplate();
    }

    public Collection<Game> getAllGames() {
        if (gameRepo == null) {
            ResponseEntity<GamesList> responseEntity = restTemplate.exchange(gamesApi,
                    HttpMethod.GET, null, new ParameterizedTypeReference<GamesList>() {
                    });
            List<Game> games = responseEntity.getBody().getItems();
            gameRepo = new HashMap<>();
            games.forEach(game -> gameRepo.put(game.getId(), game));
        }
        return gameRepo.values();
    }

    public Game getGameById(String id) {
        return gameRepo.get(id);
    }

    public Collection<Contest> getAllContests() {
        return contestRepo.values();
    }

    public Contest getContestById(String contestId) {
        return contestRepo.get(contestId);
    }

    public void addContest(Contest contest) {
        contestRepo.put(contest.getContestId(), contest);
    }
}
