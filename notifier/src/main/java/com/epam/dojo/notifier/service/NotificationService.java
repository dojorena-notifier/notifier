package com.epam.dojo.notifier.service;

import com.epam.dojo.notifier.model.Notification;

public interface NotificationService {

    void notify(String email, Notification notification);
}
