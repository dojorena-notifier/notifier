package com.epam.dojo.notifier.model;

import java.util.*;

public class LeaderBoard extends NotificationMessage {
    private final List<User> users;
    private final Map<Long, String> emails;

    public LeaderBoard(List<User> users, Map<Long, String> emails) {
        this.users = new ArrayList<>(users);
        this.emails = new HashMap<>(emails);
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    public String getEmailById(long id) {
        return this.emails.get(id);
    }
}
