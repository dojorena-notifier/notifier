package com.epam.dojo.notifier.model;

import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.hubspot.slack.client.models.Attachment;
import com.hubspot.slack.client.models.actions.Action;
import com.hubspot.slack.client.models.actions.ActionType;

public class LeaderboardNotification implements Notification {

    public static final String BUTTON_TEXT = "View Leaderboard";
    public static final String BUTTON_STYLE = "primary";
    //TODO Change this to real url
    public static final String BUTTON_REDIRECT_URL = "http://localhost:8081/api/v1/codenjoy/leaderboard";
    public static final String MESSAGE = "Change in Leaderboard!";

    @Override
    public ChatPostMessageParams.Builder convertToSlackNotification() {
        return ChatPostMessageParams.builder()
                .setText(MESSAGE)
                .addAttachments(Attachment.builder()
                        .addActions(Action.builder()
                                .setType(ActionType.BUTTON)
                                .setText(BUTTON_TEXT)
                                .setRawStyle(BUTTON_STYLE)
                                .setUrl(BUTTON_REDIRECT_URL)
                                .build())
                        .build());
    }
}
