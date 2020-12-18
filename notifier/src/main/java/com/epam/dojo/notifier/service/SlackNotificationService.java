package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.model.Contest;
import com.epam.dojo.notifier.model.LeaderBoard;
import com.epam.dojo.notifier.model.Notification;
import com.epam.dojo.notifier.model.User;
import com.hubspot.algebra.Result;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.SlackClientFactory;
import com.hubspot.slack.client.SlackClientRuntimeConfig;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.hubspot.slack.client.methods.params.conversations.ConversationOpenParams;
import com.hubspot.slack.client.methods.params.users.UserEmailParams;
import com.hubspot.slack.client.models.blocks.Divider;
import com.hubspot.slack.client.models.blocks.Section;
import com.hubspot.slack.client.models.blocks.objects.Text;
import com.hubspot.slack.client.models.blocks.objects.TextType;
import com.hubspot.slack.client.models.response.SlackError;
import com.hubspot.slack.client.models.response.chat.ChatPostMessageResponse;
import com.hubspot.slack.client.models.response.conversations.ConversationsOpenResponse;
import com.hubspot.slack.client.models.response.users.UsersInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
public class SlackNotificationService implements NotificationService<LeaderBoard> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackNotificationService.class);

    @Override
    public void notify(String email, Notification notification, Contest contest) {
        try {
            SlackClient slackClient = getSlackClient(contest.getSlackToken());
            slackClient.postMessage(notification
                    .convertToSlackNotification()
                    .setChannelId(getConversationId(email, slackClient))
                    .build());
        } catch (IllegalStateException e) {
            LOGGER.warn("Error occurred while trying to send Slack notification to user with email {}.", email);
            return;
        }
        LOGGER.info("Slack notification send to user with email {}.", email);
    }

    private String getConversationId(String email, SlackClient slackClient) {
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

    private SlackClient getSlackClient(String token) {
        SlackClientRuntimeConfig runtimeConfig = SlackClientRuntimeConfig.builder()
                .setTokenSupplier(() -> token)
                .build();

        return SlackClientFactory.defaultFactory().build(runtimeConfig);
    }

    @Override
    public void notify(LeaderBoard newLeaderBoard, Contest contest) {
        SlackClient slackClient = getSlackClient(contest.getSlackToken());

        // post header
        postMessagePart(ChatPostMessageParams.builder()
                .setChannelId(contest.getSlackChannel())
                .setBlocks(Arrays.asList(
                        Divider.builder().build(),
                        Section.of(Text.of(TextType.MARKDOWN, "*Leader board update*"))
                                .withFields(
                                        Text.of(TextType.MARKDOWN, "*User*"),
                                        Text.of(TextType.MARKDOWN, "*Score*")
                                )
                )).build(), slackClient);

        // post each participant separately
        int i = 1;
        for (User user : newLeaderBoard.getUsers()) {
            if (i > 20) break;
            postMessagePart(ChatPostMessageParams.builder()
                    .setChannelId(contest.getSlackChannel())
                    .setBlocks(Collections.singletonList(
                            Section.builder().setText(Text.of(TextType.PLAIN_TEXT, " "/*String.valueOf(i)*/)).addFields(
                                    Text.of(TextType.MARKDOWN, user.getUser().getName()),
                                    Text.of(TextType.MARKDOWN, String.valueOf(user.getScore()))
                            ).build()
                    )).build(), slackClient);
            i++;
        }

        // post footer
        postMessagePart(ChatPostMessageParams.builder()
                .setChannelId(contest.getSlackChannel())
                .setBlocks(Collections.singletonList(Divider.builder().build()))
                .build(), slackClient);
    }

    void postMessagePart(ChatPostMessageParams chatPostMessageParams, SlackClient slackClient) {
        Result<ChatPostMessageResponse, SlackError> postResult = slackClient.postMessage(chatPostMessageParams).join();
        try {
            ChatPostMessageResponse chatPostMessageResponse = postResult.unwrapOrElseThrow();
            LOGGER.debug("Slack notification send successfully!");
        } catch (Exception e) {
            LOGGER.warn("Error occurred while trying to send Slack notification: {}", e.getMessage());
        }
    }
}
