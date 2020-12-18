package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.model.*;

public interface NotificationService {

    NotifierType getNotificationServiceTypeMapping();

    //Private message
    void notify(UserDetails userDetails, Notification notification, Contest contest);
    //Message in channel
    void notify(Notification notification, Contest contest);
}
