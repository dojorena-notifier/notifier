package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.model.Notification;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.methods.params.conversations.ConversationOpenParams;
import com.hubspot.slack.client.methods.params.users.UserEmailParams;
import com.hubspot.slack.client.models.response.conversations.ConversationsOpenResponse;
import com.hubspot.slack.client.models.response.users.UsersInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SlackNotificationService implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackNotificationService.class);

    @Autowired
    private SlackClient slackClient;

    @Override
    public void notify(String email, Notification notification) {
        try {
            slackClient.postMessage(notification
                    .convertToSlackNotification()
                    .setChannelId(getConversationId(email))
                    .build());
        } catch (IllegalStateException e) {
            LOGGER.warn("Error occurred while trying to send Slack notification to user with email {}.", email);
            return;
        }
        LOGGER.info("Notification \"{}\" send to user with email {}.", notification.getMessage(), email);
    }

    private String getConversationId(String email) {
        UsersInfoResponse usersInfoResponse = slackClient
                .lookupUserByEmail(UserEmailParams.builder()
                        .setEmail(email)
                        .build())
                .join().unwrapOrElseThrow();

        ConversationsOpenResponse conversationsOpenResponse = slackClient.openConversation(
                ConversationOpenParams.builder()
                        .addUsers(usersInfoResponse.getUser().getId())
                        .build())
                .join().unwrapOrElseThrow();

        return conversationsOpenResponse.getConversation().getId();
    }
}
