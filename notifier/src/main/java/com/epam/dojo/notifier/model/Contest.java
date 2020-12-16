package com.epam.dojo.notifier.model;

import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class Contest {

    private final String contestId;
    private final Map<EventType, Set<NotifierType>> notifiers;
}
