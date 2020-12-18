package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.configuration.RestApiConfiguration;
import com.epam.dojo.notifier.model.*;
import com.epam.dojo.notifier.model.notification.FullLeaderboardNotification;
import com.epam.dojo.notifier.model.notification.PersonalLeaderboardNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class LeaderboardNotifierService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderboardNotifierService.class);
    private final RestApiConfiguration configuration;
    private final RestTemplate restTemplate;
    private final Map<String, List<User>> leaderboards;

    private final Map<NotifierType, NotificationService> notificationServices;
    private final UserDetailsService userDetailsService;

    @Autowired
    public LeaderboardNotifierService(RestApiConfiguration configuration,
                                      Collection<NotificationService> notificationServices,
                                      UserDetailsService userDetailsService) {
        this.configuration = configuration;
        this.restTemplate = new RestTemplate();
        this.leaderboards = new ConcurrentHashMap<>();
        this.notificationServices = notificationServices.stream()
                .collect(Collectors.toMap(NotificationService::getNotificationServiceTypeMapping, Function.identity()));
        this.userDetailsService = userDetailsService;

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

            if (leaderboards.size() > 0) {
                notifyUsersForChangedPosition(response, contest);
            }

            // TODO: determine the type of the change - is it just a participant score change
            //  or the participant changed the position in the leaderboard
            EventType currentEventType = EventType.ANY_LEADERBOARD_CHANGE;
            for (NotifierType notifierType : contest.getNotifiers().get(currentEventType)) {
                notificationServices.get(notifierType).notify(new FullLeaderboardNotification(response, userDetailsService), contest);
            }

            leaderboards.put(contest.getContestId(), response);
        }
    }

    private void notifyUsersForChangedPosition(List<User> newLeaderboard, Contest contest) {
        List<User> leaderboard = leaderboards.get(contest.getContestId());
        int size = Math.min(newLeaderboard.size(), leaderboard.size());

        List<UserDetails> userDetails = IntStream.range(0, size)
                .filter(i -> !leaderboard.get(i).equals(newLeaderboard.get(i)))
                .mapToObj(i -> userDetailsService.getUserDetails(leaderboard.get(i).getUser().getId()))
                .collect(Collectors.toList());
        userDetails.forEach(user -> {
            for (NotifierType notifierType : contest.getNotifiers().get(EventType.PARTICIPANT_SCORE_CHANGE)) {
                notificationServices.get(notifierType)
                        .notify(user, new PersonalLeaderboardNotification(newLeaderboard,userDetailsService, user), contest);
            }
        });
    }
}
