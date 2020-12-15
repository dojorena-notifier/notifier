package com.epam.dojo.notifier.model;

import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.hubspot.slack.client.models.Attachment;
import com.hubspot.slack.client.models.actions.Action;
import com.hubspot.slack.client.models.actions.ActionType;

public class LeaderboardNotification extends Notification {

    public static final String BUTTON_TEXT = "View Leaderboard";
    public static final String BUTTON_STYLE = "primary";
    public static final String BUTTON_REDIRECT_URL = "";

    public LeaderboardNotification(String message) {
        super(message);
    }

    @Override
    public ChatPostMessageParams.Builder convertToSlackNotification() {
        return ChatPostMessageParams.builder()
                .setText(super.getMessage())
                .addAttachments(Attachment.builder()
                        .addActions(Action.builder()
                                .setType(ActionType.BUTTON)
                                .setText(BUTTON_TEXT)
                                .setRawStyle(BUTTON_STYLE)
//                                .setUrl(BUTTON_REDIRECT_URL)
                                .build())
                        .build());
    }
}
