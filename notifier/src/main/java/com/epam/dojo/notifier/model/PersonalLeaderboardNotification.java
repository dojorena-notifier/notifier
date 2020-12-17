package com.epam.dojo.notifier.model;

import com.hubspot.slack.client.models.blocks.objects.Text;
import com.hubspot.slack.client.models.blocks.objects.TextType;

import java.util.List;
import java.util.function.Function;

import static com.epam.dojo.notifier.model.SlackNotificationUtils.makeBold;

public class PersonalLeaderboardNotification extends LeaderboardNotification {

    private final String userEmail;

    public PersonalLeaderboardNotification(List<User> leaderboard, String userEmail) {
        super(leaderboard, "Your position in leaderboard has changed");
        this.userEmail = userEmail;
    }

    @Override
    public final Text buildLeaderboardNames(Function<String, String> getSlackUserId) {
        StringBuilder names = new StringBuilder();

        getLeaderboard().forEach(user -> {
            String name = user.getUser().getEmail().equals(userEmail) ?
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
            String score = user.getUser().getEmail().equals(userEmail) ? makeBold(user.getScore())
                    : String.valueOf(user.getScore());
            scores.append(score).append("\n");
        });
        return Text.of(TextType.MARKDOWN, String.valueOf(scores));
    }
}
