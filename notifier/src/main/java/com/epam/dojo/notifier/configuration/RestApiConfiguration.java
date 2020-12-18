package com.epam.dojo.notifier.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties
@Component
public class RestApiConfiguration {

    private String leaderboardApi;
    private String userDetailsApi;

    public void setLeaderboardApi(String leaderboardApi) {
        this.leaderboardApi = leaderboardApi;
    }

    public String getLeaderboardApi() {
        return leaderboardApi;
    }

    public String getUserDetailsApi() {
        return userDetailsApi;
    }

    public void setUserDetailsApi(String userDetailsApi) {
        this.userDetailsApi = userDetailsApi;
    }
}

