package com.epam.dojo.notifier.service;

import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.hubspot.slack.client.methods.params.conversations.ConversationOpenParams;
import com.hubspot.slack.client.methods.params.users.UserEmailParams;
import com.hubspot.slack.client.models.Attachment;
import com.hubspot.slack.client.models.actions.Action;
import com.hubspot.slack.client.models.actions.ActionType;
import com.hubspot.slack.client.models.response.conversations.ConversationsOpenResponse;
import com.hubspot.slack.client.models.response.users.UsersInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SlackNotificationService implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackNotificationService.class);
    public static final String BUTTON_TEXT = "View Leaderboard";
    public static final String BUTTON_STYLE = "primary";
    public static final String BUTTON_REDIRECT_URL = "";

    @Autowired
    private SlackClient slackClient;


    public void notify(String email, String message) {
        slackClient.postMessage(ChatPostMessageParams.builder()
                .setChannelId(getConversationId(email))
                .setText(message)
                .addAttachments(Attachment.builder()
                        .addActions(Action.builder()
                                .setType(ActionType.BUTTON)
                                .setText(BUTTON_TEXT)
                                .setRawStyle(BUTTON_STYLE)
//                                .setUrl(BUTTON_REDIRECT_URL)
                                .build())
                        .build())
                .build());

        LOGGER.info("Notification \"{}\" send to user with email {}.", message, email);
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
