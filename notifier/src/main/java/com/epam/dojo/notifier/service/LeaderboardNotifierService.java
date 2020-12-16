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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class LeaderboardNotifierService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderboardNotifierService.class);
    private final Configuration configuration;
    private final RestTemplate restTemplate;
    private final List<User> leaderboard;

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

    public void getLeaderBoard() {
        ResponseEntity<List<User>> responseEntity = restTemplate.exchange(configuration.getLeaderboardApi(),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
                });
        List<User> response = responseEntity.getBody();

        if (response != null && !leaderboard.equals(response)) {
            LOGGER.info("There are changes in leaderboard!");

            // notifyChanges(response);

            // TODO: determine the type of the change - is it just a participant score change
            //  or the participant changed the position in the leaderboard
            EventType currentEventType = EventType.ANY_LEADERBOARD_CHANGE;
            for (Map.Entry<EventType, Set<NotifierType>> notifiersConfig : configuration.getNotifiers().entrySet()) {
                if (currentEventType == notifiersConfig.getKey()) {
                    for (NotifierType notifierType : notifiersConfig.getValue()) {
                        notificationServiceFactory(notifiersConfig.getKey(), notifierType)
                                .notify(new LeaderBoard(response));
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

    private void notifyChanges(List<User> newLeaderboard) {
        int size = Math.min(newLeaderboard.size(), leaderboard.size());

        List<String> emails = IntStream.range(0, size)
                .filter(i -> !leaderboard.get(i).equals(newLeaderboard.get(i)))
                .mapToObj(i -> leaderboard.get(i).getEmail())
                .collect(Collectors.toList());
        emails.forEach(e -> notificationServices.forEach(service ->
                        service.notify(e, new LeaderboardNotification())));
    }

    @PostConstruct
    private void init() {
        executorService.scheduleAtFixedRate(this::getLeaderBoard, 0, configuration.getPeriod(), TimeUnit.SECONDS);
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
