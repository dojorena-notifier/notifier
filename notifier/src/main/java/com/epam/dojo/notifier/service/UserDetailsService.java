package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.configuration.RestApiConfiguration;
import com.epam.dojo.notifier.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserDetailsService {

    private final RestApiConfiguration configuration;
    private final RestTemplate restTemplate;
    private final Map<Long, UserDetails> userDetailsCache;

    @Autowired
    public UserDetailsService(RestApiConfiguration configuration) {
        this.configuration = configuration;
        this.restTemplate = new RestTemplate();
        this.userDetailsCache = new ConcurrentHashMap<>();
    }

    public UserDetails getUserDetails(long userId) {
        UserDetails userDetails = userDetailsCache.get(userId);
        if (userDetails == null) {
            ResponseEntity<UserDetails> userDetailsResponse = restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(configuration.getUserDetailsApi()).pathSegment(String.valueOf(userId)).toUriString(),
                    HttpMethod.GET, null, new ParameterizedTypeReference<UserDetails>() {
                    });
            userDetails = userDetailsResponse.getBody();
            if (userDetails != null) {
                userDetailsCache.put(userId, userDetails);
            }
        }
        return userDetails;
    }

    public String getUserEmail(long userId) {
        UserDetails userDetails = getUserDetails(userId);
        return userDetails != null ? userDetails.getEmail() : null;
    }
}
