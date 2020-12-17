package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.model.Notification;
import com.epam.dojo.notifier.model.NotifierType;
import com.hubspot.algebra.Result;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.hubspot.slack.client.methods.params.conversations.ConversationOpenParams;
import com.hubspot.slack.client.methods.params.users.UserEmailParams;
import com.hubspot.slack.client.models.response.SlackError;
import com.hubspot.slack.client.models.response.chat.ChatPostMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SlackNotificationService implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackNotificationService.class);

    @Value("${slack.channel.name}")
    private String channelToPostIn;

    private final SlackClient slackClient;

    @Autowired
    public SlackNotificationService(SlackClient slackClient) {
        this.slackClient = slackClient;
    }

    @Override
    public NotifierType getNotificationServiceTypeMapping() {
        return NotifierType.SLACK;
    }

    // Notify user
    @Override
    public void notify(String email, Notification notification) {
        postMessagePart(notification
                .convertToSlackNotification(this::getSlackUserId)
                .setChannelId(getConversationId(email))
                .build());
    }

    // Notify channel
    @Override
    public void notify(Notification notification) {
        postMessagePart(notification
                .convertToSlackNotification(this::getSlackUserId)
                .setChannelId(channelToPostIn)
                .build());

    }

    private void postMessagePart(ChatPostMessageParams chatPostMessageParams) {
        Result<ChatPostMessageResponse, SlackError> postResult = slackClient.postMessage(chatPostMessageParams).join();
        try {
            postResult.unwrapOrElseThrow(); // release failure here as a RTE
        } catch (IllegalStateException e) {
            LOGGER.warn("Error occurred while trying to send Slack notification to channel {}.", chatPostMessageParams.getChannelId());
            return;
        }
        LOGGER.info("Slack notification send to channel {}.", chatPostMessageParams.getChannelId());
    }

    private String getConversationId(String email) {
        try {
            return slackClient.openConversation(
                    ConversationOpenParams.builder()
                            .addUsers(getSlackUserId(email))
                            .build())
                    .join().unwrapOrElseThrow().getConversation().getId();
        } catch (IllegalStateException e) {
            LOGGER.warn("Could not find conversation for user with email {}.", email);
            return "";
        }
    }

    private String getSlackUserId(String email) {
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
