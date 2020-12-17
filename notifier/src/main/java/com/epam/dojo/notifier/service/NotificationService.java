package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.model.Notification;
import com.epam.dojo.notifier.model.NotificationMessage;

public interface NotificationService<T extends NotificationMessage> {

    void notify(String email, Notification notification);
    void notify(T notificationMessage, String slackChannel);
}
