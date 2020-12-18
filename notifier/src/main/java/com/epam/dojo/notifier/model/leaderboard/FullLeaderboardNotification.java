package com.epam.dojo.notifier.model.leaderboard;

import com.epam.dojo.notifier.model.user.User;
import com.epam.dojo.notifier.service.UserDetailsService;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.models.blocks.objects.Text;
import com.hubspot.slack.client.models.blocks.objects.TextType;

import java.util.List;
import java.util.function.BiFunction;

import static com.epam.dojo.notifier.model.SlackNotificationUtils.makeBold;

public class FullLeaderboardNotification extends LeaderboardNotification {

    private final List<User> leaderboard;

    public FullLeaderboardNotification(List<User> leaderboard, UserDetailsService userDetailsService) {
        super(leaderboard, userDetailsService, "Leaderboard update");
        this.leaderboard = leaderboard;
    }

    @Override
    public final Text buildLeaderboardNames(BiFunction<String, SlackClient, String> getSlackUserId, SlackClient slackClient) {
        StringBuilder names = new StringBuilder();
        leaderboard.forEach(user -> {
            String userId = getSlackUserId.apply(getUserDetailsService().getUserEmail(user.getUser().getId()), slackClient);
            String nameWithLink = "<slack://user?team=null&id=" + userId + "|" + user.getUser().getName() + ">";
            names.append(makeBold(getPositionAndIncrease()))
                    .append(". ")
                    .append(userId.isEmpty() ? user.getUser().getName() : nameWithLink)
                    .append("\n");
        });
        return Text.of(TextType.MARKDOWN, String.valueOf(names));
    }

    @Override
    public Text buildLeaderboardScores() {
        StringBuilder scores = new StringBuilder();

        leaderboard.forEach(user -> scores.append(user.getScore()).append("\n"));
        return Text.of(TextType.MARKDOWN, String.valueOf(scores));
    }
}
