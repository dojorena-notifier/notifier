package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.model.LeaderBoard;
import com.epam.dojo.notifier.model.Notification;
import com.epam.dojo.notifier.model.User;
import com.hubspot.algebra.Result;
import com.hubspot.slack.client.SlackClient;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
public class SlackNotificationService implements NotificationService<LeaderBoard> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackNotificationService.class);

    @Autowired
    private SlackClient slackClient;
    private String channelToPostIn = "software-development";

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
        LOGGER.info("Slack notification send to user with email {}.", email);
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

    // post to channel

    @Override
    public void notify(LeaderBoard newLeaderBoard) {

        // post header
        postMessagePart(ChatPostMessageParams.builder()
                .setChannelId(channelToPostIn)
                .setBlocks(Arrays.asList(
                        Divider.builder().build(),
                        Section.of(Text.of(TextType.MARKDOWN, "*Leader board update*"))
                                .withFields(
                                        Text.of(TextType.MARKDOWN, "*User*"),
                                        Text.of(TextType.MARKDOWN, "*Score*")
                                )
                )).build());

        // post each participant separately
        int i = 1;
        for (User user : newLeaderBoard.getUsers()) {
            postMessagePart(ChatPostMessageParams.builder()
                    .setChannelId(channelToPostIn)
                    .setBlocks(Collections.singletonList(
                            Section.builder().setText(Text.of(TextType.PLAIN_TEXT, " "/*String.valueOf(i++)*/)).addFields(
                                    Text.of(TextType.MARKDOWN, user.getUser().getName()),
                                    Text.of(TextType.MARKDOWN, String.valueOf(user.getScore()))
                            ).build()
                    )).build());
        }

        // post footer
        postMessagePart(ChatPostMessageParams.builder()
                .setChannelId(channelToPostIn)
                .setBlocks(Collections.singletonList(Divider.builder().build()))
                .build());

    }

    void postMessagePart(ChatPostMessageParams chatPostMessageParams) {
        Result<ChatPostMessageResponse, SlackError> postResult = slackClient.postMessage(chatPostMessageParams).join();

        ChatPostMessageResponse response = postResult.unwrapOrElseThrow(); // release failure here as a RTE
    }

    public void slackPostSample(LeaderBoard newLeaderBoard) {
        Result<ChatPostMessageResponse, SlackError> postResult = slackClient.postMessage(
                ChatPostMessageParams.builder()
                        .setText("Here is an example message with blocks:")
                        .setChannelId(channelToPostIn)
                        .setBlocks(Arrays.asList(
                                Blocks.markdownSection(":newspaper:  *Paper Company Newsletter*  :newspaper:"),
                                Blocks.markdownContext("*November 12, 2019*  |  Sales Team Announcements"),
                                Blocks.divider(),
                                Blocks.markdownSection(":loud_sound: *IN CASE YOU MISSED IT* :loud_sound:"),
                                Blocks.markdownSection("Replay our screening of *Threat Level Midnight* and pick up a copy of the DVD to give to your customers at the front desk.")
                                        .withAccessory(Blocks.plainTextButton("Watch Now")),
                                Blocks.markdownSection("The *2019 Dundies* happened. \nAwards were given, heroes were recognized. \nCheck out *#dundies-2019* to see who won awards."),
                                Blocks.divider(),
                                Blocks.markdownSection(":calendar: |   *UPCOMING EVENTS*  | :calendar:"),
                                Blocks.markdownSection("`11/20-11/22` *Beet the Competition* _ annual retreat at Schrute Farms_")
                                        .withAccessory(Blocks.plainTextButton("RSVP")),
                                Blocks.markdownSection("`12/01` *Toby's Going Away Party* at _Benihana_")
                                        .withAccessory(Blocks.plainTextButton("Learn More")),
                                Blocks.markdownSection("`11/13` :pretzel: *Pretzel Day* :pretzel: at _Scranton Office_")
                                        .withAccessory(Blocks.plainTextButton("RSVP")),
                                Blocks.divider(),
                                Blocks.markdownSection(":calendar: |   *PAST EVENTS*  | :calendar:"),
                                Blocks.markdownSection("`10/21` *Conference Room Meeting*")
                                        .withAccessory(Blocks.staticSelectMenu("Manage",
                                                Blocks.option("Edit it", "value=0"),
                                                Blocks.option("Read it", "value=1"),
                                                Blocks.option("Save it", "value=2"))),
                                Blocks.divider()
                        )).build()
        ).join();

        ChatPostMessageResponse response = postResult.unwrapOrElseThrow(); // release failure here as a RTE
    }
}
