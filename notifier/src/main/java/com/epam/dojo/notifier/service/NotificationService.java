package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.contest.Contest;
import com.epam.dojo.notifier.contest.enums.NotifierType;
import com.epam.dojo.notifier.model.notification.Notification;
import com.epam.dojo.notifier.model.user.UserDetails;

public interface NotificationService {

    NotifierType getNotificationServiceTypeMapping();

    //Private message
    void notify(UserDetails userDetails, Notification notification, Contest contest);
    //Message in channel
    void notify(Notification notification, Contest contest);
}
