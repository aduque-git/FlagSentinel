package com.example.flagsentinelapi.factory;

import com.example.flagsentinelapi.model.Role;
import com.example.flagsentinelapi.model.User;

public class UserTestFactory {

    public static User createAdmin(String password) {
        return new User("admin", password, Role.ADMIN);
    }

    public static User createUser(String password) {
        return new User("user", password, Role.USER);
    }
}
