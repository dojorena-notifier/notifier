package com.epam.dojo.notifier.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private long id;
    private String name;
    private String picture;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInfo)) return false;
        UserInfo userInfo = (UserInfo) o;
        return id == userInfo.id &&
                name.equals(userInfo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
