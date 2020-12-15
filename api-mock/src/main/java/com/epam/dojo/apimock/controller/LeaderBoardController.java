package com.epam.dojo.apimock.controller;

import com.epam.dojo.apimock.LeaderBoardProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class LeaderBoardController {

    private final AtomicInteger requestId;
    private final LeaderBoardProvider leaderBoardProvider;

    @Autowired
    public LeaderBoardController(LeaderBoardProvider leaderBoardProvider) {
        this.leaderBoardProvider = leaderBoardProvider;
        this.requestId = new AtomicInteger(0);
    }

    @GetMapping("/api/v1/codenjoy/leaderboard")
    public List<Object> getLeaderBoard(){
        return leaderBoardProvider.generateLeaderBoard(requestId.getAndIncrement());
    }

}
