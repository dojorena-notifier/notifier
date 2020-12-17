package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.configuration.Configuration;
import com.epam.dojo.notifier.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class LeaderboardNotifierService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderboardNotifierService.class);
    private final Configuration configuration;
    private final RestTemplate restTemplate;
    private final Map<String, List<User>> leaderboards;
    private final Map<Long, String> emails = new HashMap<>();
    private final Map<NotifierType, NotificationService> notificationServices;

    @Value("${leaderboardApi}")
    private StringBuilder leaderboardApi;


    @Autowired
    public LeaderboardNotifierService(Configuration configuration, Collection<NotificationService> notificationServices) {
        this.configuration = configuration;
        this.restTemplate = new RestTemplate();
        this.notificationServices = notificationServices.stream()
                .collect(Collectors.toMap(NotificationService::getNotificationServiceTypeMapping, Function.identity()));
        this.leaderboards = new ConcurrentHashMap<>();
    }

    public void getLeaderBoard(final Contest contest) {
        StringBuilder getLeaderboardApiBuilder = leaderboardApi.append(contest.getContestId());
        ResponseEntity<List<User>> responseEntity = restTemplate.exchange(getLeaderboardApiBuilder.toString(),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
                });

        List<User> response = responseEntity.getBody();

        if (response != null && !response.equals(leaderboards.get(contest.getContestId())) ) {
            LOGGER.info("There are changes in leaderboard!");

            response.stream().filter(user -> !emails.containsKey(user.getUser().getId()))
                    .forEach(user -> {
                        ResponseEntity<UserDetails> userDetailsResponse = restTemplate.exchange(configuration.getUserDetailsApi() + user.getUser().getId(),
                                HttpMethod.GET, null, new ParameterizedTypeReference<UserDetails>() {
                                });
                        UserDetails userDetails = userDetailsResponse.getBody();
                        if (userDetails != null) {
                            emails.put(user.getUser().getId(), userDetails.getEmail());
                        }
                    });

            notifyUsersForChangedPosition(response);

            // TODO: determine the type of the change - is it just a participant score change
            //  or the participant changed the position in the leaderboard
            EventType currentEventType = EventType.ANY_LEADERBOARD_CHANGE;
            for (NotifierType notifierType : configuration.getNotifiers().get(currentEventType)) {
                notificationServices.get(notifierType).notify(new FullLeaderboardNotification(response), contest.getSlackChannel());
            }

            leaderboards.put(contest.getContestId(), response);
        }
    }

    private void notifyUsersForChangedPosition(List<User> newLeaderboard) {
        int size = Math.min(newLeaderboard.size(), leaderboard.size());

        List<String> emails = IntStream.range(0, size)
                .filter(i -> !leaderboards.get(i).equals(newLeaderboard.get(i)))
                .mapToObj(i -> leaderboards.get(i).getEmail())
                .collect(Collectors.toList());
        emails.forEach(email -> {
            for (NotifierType notifierType : configuration.getNotifiers().get(EventType.PARTICIPANT_SCORE_CHANGE)) {
                notificationServices.get(notifierType).notify(email, new PersonalLeaderboardNotification(newLeaderboard, email));
            }
        });
    }
}
