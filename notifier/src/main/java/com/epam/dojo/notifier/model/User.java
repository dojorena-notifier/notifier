package com.epam.dojo.notifier.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private UserInfo user;
    private long score;

    public String getEmail() {
        return user.getEmail();
    }

    public void setEmail(String email) {
        this.user.setEmail(email);
    }
}
