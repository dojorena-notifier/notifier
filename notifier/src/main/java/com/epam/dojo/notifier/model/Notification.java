package com.epam.dojo.notifier.model;

import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;

public interface Notification {

    ChatPostMessageParams.Builder convertToSlackNotification();
}
