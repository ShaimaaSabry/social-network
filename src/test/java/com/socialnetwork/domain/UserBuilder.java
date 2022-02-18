package com.socialnetwork.domain;

public class UserBuilder {
    private User user;

    public UserBuilder(String email) {
//        user = new User();
//        user.setEmail(email);
//        user.setFirstName(email);
//        user.setLastName(email);
//        user.setPasswordHash(email);
    }

    public UserBuilder firstName(String firstName) {
//        user.setFirstName(firstName);
        return this;
    }

    public UserBuilder lastName(String lastName) {
//        user.setLastName(lastName);
        return this;
    }

    public UserBuilder passwordHash(String passwordHash) {
//        user.setPasswordHash(passwordHash);
        return this;
    }

    public User build() {
        return user;
    }
}
