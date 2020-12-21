package com.epam.dojo.notifier.model.notification;

import com.epam.dojo.notifier.service.emailNotifier.MailContentBuilder;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;

import java.util.function.BiFunction;

public interface Notification {

    ChatPostMessageParams.Builder convertToSlackNotification(BiFunction<String, SlackClient, String> getChannelId, SlackClient slackClient);

    String convertToEmailNotification(MailContentBuilder mailContentBuilder);
}
