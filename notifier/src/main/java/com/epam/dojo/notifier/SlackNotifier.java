package com.epam.dojo.notifier;

import org.springframework.stereotype.Component;

@Component
public class SlackNotifier implements Notifier {
    @Override
    public void notify(String email, String message) {

    }
}
