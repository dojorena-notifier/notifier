package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.configuration.Configuration;
import com.epam.dojo.notifier.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LeaderboardNotifierService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderboardNotifierService.class);
    private final Configuration configuration;
    private final RestTemplate restTemplate;
    private final Map<String, List<User>> leaderboards;
    private final Map<Long, String> emails = new HashMap<>();
    private final SlackNotificationService slackNotificationService;

    @Autowired
    public LeaderboardNotifierService(Configuration configuration, SlackNotificationService slackNotificationService) {
        this.configuration = configuration;
        this.restTemplate = new RestTemplate();
        this.slackNotificationService = slackNotificationService;
        this.leaderboards = new ConcurrentHashMap<>();
    }

    public void getLeaderBoard(final Contest contest) {
        UriComponentsBuilder leaderboardApiBuilder = UriComponentsBuilder.fromHttpUrl(configuration.getLeaderboardApi())
                .queryParam("eventId", contest.getContestId())
                .queryParam("userMode", "spectator");
        ResponseEntity<List<User>> responseEntity = restTemplate.exchange(leaderboardApiBuilder.toUriString(),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
                });

        List<User> response = responseEntity.getBody();

        if (response != null && !response.equals(leaderboards.get(contest.getContestId())) ) {
            LOGGER.info("There are changes in leaderboard!");

            response.stream().filter(user -> !emails.containsKey(user.getUser().getId()))
                    .forEach(user -> {
                        ResponseEntity<UserDetails> userDetailsResponse = restTemplate.exchange(
                                UriComponentsBuilder.fromHttpUrl(configuration.getUserDetailsApi()).pathSegment(String.valueOf(user.getUser().getId())).toUriString(),
                                HttpMethod.GET, null, new ParameterizedTypeReference<UserDetails>() {
                                });
                        UserDetails userDetails = userDetailsResponse.getBody();
                        if (userDetails != null) {
                            emails.put(user.getUser().getId(), userDetails.getEmail());
                        }
                    });


            // TODO: determine the type of the change - is it just a participant score change
            //  or the participant changed the position in the leaderboard
            EventType currentEventType = EventType.ANY_LEADERBOARD_CHANGE;
            for (Map.Entry<EventType, Set<NotifierType>> notifiersConfig : contest.getNotifiers().entrySet()) {
                if (currentEventType == notifiersConfig.getKey()) {
                    for (NotifierType notifierType : notifiersConfig.getValue()) {
                        notificationServiceFactory(notifiersConfig.getKey(), notifierType)
                                .notify(new LeaderBoard(response), contest);
                    }
                }
            }

            leaderboards.put(contest.getContestId(), response);
        }
    }

    private NotificationService<LeaderBoard> notificationServiceFactory(EventType eventType, NotifierType notifierType) {
        if (eventType == EventType.ANY_LEADERBOARD_CHANGE && notifierType == NotifierType.SLACK) {
            return slackNotificationService;
        }
        return null;
    }

}
