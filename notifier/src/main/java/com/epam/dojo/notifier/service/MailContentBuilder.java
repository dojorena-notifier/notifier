package com.epam.dojo.notifier.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Service
public abstract class MailContentBuilder<T> {

    private final TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public abstract String generateMailContent(T content);

    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }
}