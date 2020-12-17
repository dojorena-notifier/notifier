package com.epam.dojo.notifier.configuration;

import com.epam.dojo.notifier.model.EventType;
import com.epam.dojo.notifier.model.NotifierType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ConfigurationProperties
@Component
public class Configuration {
    private String leaderboardApi;
    private String userDetailsApi;
    private int period;
    private int threadPoolSize;
    private String contestId;
    private Map<EventType, Set<NotifierType>> notifiers = new HashMap<>();

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public void setLeaderboardApi(String leaderboardApi) {
        this.leaderboardApi = leaderboardApi;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getPeriod() {
        return period;
    }

    public String getLeaderboardApi() {
        return leaderboardApi;
    }

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public Map<EventType, Set<NotifierType>> getNotifiers() {
        return notifiers;
    }

    public void setNotifiers(Map<EventType, Set<NotifierType>> notifiers) {
        this.notifiers = notifiers;
    }

    public String getUserDetailsApi() {
        return userDetailsApi;
    }

    public void setUserDetailsApi(String userDetailsApi) {
        this.userDetailsApi = userDetailsApi;
    }
}

