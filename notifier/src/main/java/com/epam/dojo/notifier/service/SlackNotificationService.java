package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.contest.Contest;
import com.epam.dojo.notifier.contest.NotifierType;
import com.epam.dojo.notifier.model.notification.Notification;
import com.epam.dojo.notifier.model.user.UserDetails;
import com.hubspot.algebra.Result;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.SlackClientFactory;
import com.hubspot.slack.client.SlackClientRuntimeConfig;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.hubspot.slack.client.methods.params.conversations.ConversationOpenParams;
import com.hubspot.slack.client.methods.params.users.UserEmailParams;
import com.hubspot.slack.client.models.response.SlackError;
import com.hubspot.slack.client.models.response.chat.ChatPostMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SlackNotificationService implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackNotificationService.class);

    @Override
    public NotifierType getNotificationServiceTypeMapping() {
        return NotifierType.SLACK;
    }

    // Notify user
    @Override
    public void notify(UserDetails userDetails, Notification notification, Contest contest) {
        SlackClient slackClient = getSlackClient(contest.getSlackToken());
        postMessagePart(notification
                .convertToSlackNotification(this::getSlackUserId, slackClient)
                .setChannelId(getConversationId(userDetails.getEmail(), slackClient))
                .build(), slackClient);
    }

    // Notify channel
    @Override
    public void notify(Notification notification, Contest contest) {
        SlackClient slackClient = getSlackClient(contest.getSlackToken());
        postMessagePart(notification
                .convertToSlackNotification(this::getSlackUserId, slackClient)
                .setChannelId(contest.getSlackChannel())
                .build(), slackClient);

    }

    private void postMessagePart(ChatPostMessageParams chatPostMessageParams, SlackClient slackClient) {
        Result<ChatPostMessageResponse, SlackError> postResult = slackClient.postMessage(chatPostMessageParams).join();
        try {
            postResult.unwrapOrElseThrow(); // release failure here as a RTE
        } catch (IllegalStateException e) {
            LOGGER.warn("Error occurred while trying to send Slack notification to channel {}.", chatPostMessageParams.getChannelId());
            return;
        }
        LOGGER.info("Slack notification send to channel {}.", chatPostMessageParams.getChannelId());
    }

    private String getConversationId(String email, SlackClient slackClient) {
        try {
            return slackClient.openConversation(
                    ConversationOpenParams.builder()
                            .addUsers(getSlackUserId(email, slackClient))
                            .build())
                    .join().unwrapOrElseThrow().getConversation().getId();
        } catch (IllegalStateException e) {
            LOGGER.warn("Could not find conversation for user with email {}.", email);
            return "";
        }
    }

    private static SlackClient getSlackClient(String token) {
        SlackClientRuntimeConfig runtimeConfig = SlackClientRuntimeConfig.builder()
                .setTokenSupplier(() -> token)
                .build();

        return SlackClientFactory.defaultFactory().build(runtimeConfig);
    }

    private String getSlackUserId(String email, SlackClient slackClient) {
        try {
            return slackClient
                    .lookupUserByEmail(UserEmailParams.builder()
                            .setEmail(email)
                            .build())
                    .join().unwrapOrElseThrow().getUser().getId();
        } catch (IllegalStateException e) {
            LOGGER.warn("Could not find user with email {}.", email);
            return "";
        }
    }
}
