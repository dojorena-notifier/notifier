package com.epam.dojo.notifier.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String picture;
    private List<String> technologies;
    private String email;
    private String name;
    private long id;
    private String city;
    private String company;
    private String country;
    private String position;
    private boolean isInternal;
}
