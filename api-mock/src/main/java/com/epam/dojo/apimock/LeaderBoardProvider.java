package com.epam.dojo.apimock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Component
public class LeaderBoardProvider {

    @Value("classpath:static/leaderboard-responses.json")
    private Resource resourceFile;
    private  List<List<Object>> scenarios;

    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderBoardProvider.class);

    @PostConstruct
    private void load() throws IOException {
        this.scenarios = new ObjectMapper().readValue(resourceFile.getFile(), new TypeReference<List<List<Object>>>(){});
    }

    public List<Object> generateLeaderBoard(final int requestId){
        int scenarioId = requestId < scenarios.size() ? requestId : scenarios.size()-1;
        List<Object> scenario = scenarios.get(scenarioId);
        LOGGER.info("Scenario {} retrieved for request {}: {}", scenarioId, requestId, scenario);
        return scenario;
    }

}
