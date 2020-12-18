package com.epam.dojo.notifier.model;

import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.models.blocks.objects.Text;
import com.hubspot.slack.client.models.blocks.objects.TextType;

import java.util.List;
import java.util.function.BiFunction;

import static com.epam.dojo.notifier.model.SlackNotificationUtils.makeBold;

public class PersonalLeaderboardNotification extends LeaderboardNotification {

    private final UserDetails userDetails;

    public PersonalLeaderboardNotification(List<User> leaderboard, UserDetails userDetails) {
        super(leaderboard, "Your position in leaderboard has changed");
        this.userDetails = userDetails;
    }

    @Override
    public final Text buildLeaderboardNames(BiFunction<String, SlackClient, String> getSlackUserId, SlackClient slackClient) {
        StringBuilder names = new StringBuilder();

        getLeaderboard().forEach(user -> {
            String name = (user.getUser().getId() == userDetails.getId()) ?
                    makeBold(user.getUser().getName()) : user.getUser().getName();
            names.append(makeBold(getPositionAndIncrease()))
                    .append(". ")
                    .append(name)
                    .append("\n");
        });
        return Text.of(TextType.MARKDOWN, String.valueOf(names));
    }

    @Override
    public Text buildLeaderboardScores() {
        StringBuilder scores = new StringBuilder();

        getLeaderboard().forEach(user -> {
            String score = (user.getUser().getId() == userDetails.getId()) ? makeBold(user.getScore())
                    : String.valueOf(user.getScore());
            scores.append(score).append("\n");
        });
        return Text.of(TextType.MARKDOWN, String.valueOf(scores));
    }
}
