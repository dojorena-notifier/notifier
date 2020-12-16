package com.epam.dojo.notifier.api;

import com.epam.dojo.notifier.model.Contest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContestController {

    @PostMapping("/api/v1/contest")
    public Contest subscribeForContest(@RequestBody Contest contest) {
        return contest;
    }
}
