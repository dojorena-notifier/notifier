package com.epam.dojo.notifier.service.emailNotifier;

import com.epam.dojo.notifier.model.leaderboard.LeaderboardNotification;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class LeaderboardMailMessageBuilder extends MailContentBuilder<LeaderboardNotification> {

    public LeaderboardMailMessageBuilder(TemplateEngine templateEngine) {
        super(templateEngine);
    }

    @Override
    public String generateMailContent(LeaderboardNotification notification) {
        Context context = new Context();
        context.setVariable("users", notification.getLeaderboard());

        return getTemplateEngine().process("mailTemplate", context);
    }
}
