package com.epam.dojo.notifier.model;

import lombok.Data;

import java.util.*;

@Data
public class Contest {

    private String contestId;
    private String title;
    private String slackToken;
    private String slackChannel;
    private Map<EventType, Set<NotifierType>> notifiers;

    public Contest() {
        notifiers = new HashMap<>();
        notifiers.put(EventType.ANY_LEADERBOARD_CHANGE,
                new HashSet<>(Arrays.asList(NotifierType.SLACK)));
    }
}
