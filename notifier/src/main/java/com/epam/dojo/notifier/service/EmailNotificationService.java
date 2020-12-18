package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.configuration.EmailConfig;
import com.epam.dojo.notifier.model.LeaderBoard;
import com.epam.dojo.notifier.model.Notification;
import com.epam.dojo.notifier.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailNotificationService implements NotificationService<LeaderBoard> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotificationService.class);

    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private MailContentBuilder<LeaderBoard> mailContentBuilder;

    @Override
    public void notify(String email, Notification notification) {

    }

    @Override
    public void notify(LeaderBoard notificationMessage) {
        List<User> users = notificationMessage.getUsers();
        users.forEach(user -> sendEmail(notificationMessage.getEmailById(user.getUser().getId()), notificationMessage));
    }

    private void sendEmail(String to, LeaderBoard data) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);

            String content = mailContentBuilder.generateMailContent(data);
            helper.setFrom(emailConfig.getUsername());
            helper.setTo(to);
            helper.setSubject("Leaderboard");
            helper.setText(content, true);

            emailSender.send(message);

        } catch (MessagingException e) {
            LOGGER.warn("Email could not be sent: {}", e.getCause().getMessage());
        }
    }
}

