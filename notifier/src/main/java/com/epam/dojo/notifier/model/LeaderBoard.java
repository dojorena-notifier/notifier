package com.epam.dojo.notifier.model;

import java.util.List;

public class LeaderBoard extends NotificationMessage {
    private final List<User> users;

    public LeaderBoard(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }
}
