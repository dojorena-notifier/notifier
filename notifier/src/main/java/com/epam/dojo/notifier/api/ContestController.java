package com.epam.dojo.notifier.api;

import com.epam.dojo.notifier.model.Contest;
import com.epam.dojo.notifier.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContestController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public ContestController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/api/v1/subscribe-contest")
    public void subscribeForContest(@RequestBody Contest contest) {
        subscriptionService.subscribeContest(contest);
    }

    @PostMapping("/api/v1/unsubscribe-contest")
    public void unsubscribeForContest(@RequestBody Contest contest) {
        subscriptionService.unsubscribeContest(contest.getContestId());
    }


}
