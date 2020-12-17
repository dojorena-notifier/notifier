package com.epam.dojo.notifier.model;

import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;

import java.util.function.Function;

public interface Notification {

    ChatPostMessageParams.Builder convertToSlackNotification(Function<String, String> getChannelId);
}
