package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.model.Notification;
import com.epam.dojo.notifier.model.NotifierType;

public interface NotificationService {

    NotifierType getNotificationServiceTypeMapping();

    //Private message
    void notify(String email, Notification notification);

    //Message in channel
    void notify(Notification notification, String slackChannel);
}
