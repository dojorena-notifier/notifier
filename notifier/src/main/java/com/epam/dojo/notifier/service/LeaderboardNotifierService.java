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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class LeaderboardNotifierService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderboardNotifierService.class);
    private final Configuration configuration;
    private final RestTemplate restTemplate;
    private final List<User> leaderboard;
    private final Map<Long, String> emails = new HashMap<>();

    private final ScheduledExecutorService executorService;
    private final List<NotificationService> notificationServices;
    private final SlackNotificationService slackNotificationService;

    @Autowired
    public LeaderboardNotifierService(Configuration configuration, List<NotificationService> notificationServices, SlackNotificationService slackNotificationService) {
        this.leaderboard = new ArrayList<>();
        this.configuration = configuration;
        this.restTemplate = new RestTemplate();
        this.notificationServices = notificationServices;
        this.slackNotificationService = slackNotificationService;
        this.executorService = Executors.newScheduledThreadPool(configuration.getThreadPoolSize());
    }


    public void getLeaderBoard(Contest contest) {
        ResponseEntity<List<User>> responseEntity = restTemplate.exchange(configuration.getLeaderboardApi(),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
                });

        List<User> response = responseEntity.getBody();

        if (response != null && !leaderboard.equals(response)) {
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


            // TODO: determine the type of the change - is it just a participant score change
            //  or the participant changed the position in the leaderboard
            EventType currentEventType = EventType.ANY_LEADERBOARD_CHANGE;
            for (Map.Entry<EventType, Set<NotifierType>> notifiersConfig : contest.getNotifiers().entrySet()) {
                if (currentEventType == notifiersConfig.getKey()) {
                    for (NotifierType notifierType : notifiersConfig.getValue()) {
//                        notificationServiceFactory(notifiersConfig.getKey(), notifierType)
//                                .notify(new LeaderBoard(response, this.emails));
                        notificationServices.get(0).notify(new LeaderBoard(response,emails));
                    }
                }
            }

            leaderboard.clear();
            leaderboard.addAll(response);
        }
    }

    private NotificationService<LeaderBoard> notificationServiceFactory(EventType eventType, NotifierType notifierType) {
        if (eventType == EventType.ANY_LEADERBOARD_CHANGE && notifierType == NotifierType.SLACK) {
            return slackNotificationService;
        }
        return null;
    }

    @PostConstruct
    private void init() {
        Contest contest = new Contest();

        executorService.scheduleAtFixedRate(() -> getLeaderBoard(contest), 0, configuration.getPeriod(), TimeUnit.SECONDS);
    }

    @PreDestroy
    private void destroy() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                executorService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
