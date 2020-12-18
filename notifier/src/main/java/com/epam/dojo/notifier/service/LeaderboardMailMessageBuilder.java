package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.model.LeaderBoard;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class LeaderboardMailMessageBuilder extends MailContentBuilder<LeaderBoard> {

    public LeaderboardMailMessageBuilder(TemplateEngine templateEngine) {
        super(templateEngine);
    }

    @Override
    public String generateMailContent(LeaderBoard leaderBoard) {
        Context context = new Context();
        context.setVariable("users", leaderBoard.getUsers());
        return getTemplateEngine().process("mailTemplate", context);
    }
}
