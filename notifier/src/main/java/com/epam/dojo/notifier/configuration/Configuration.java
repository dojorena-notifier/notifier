package com.epam.dojo.notifier.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties
@Component
public class Configuration {
    private String leaderboardApi;
    private int period;
    private int threadPoolSize;

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
}

