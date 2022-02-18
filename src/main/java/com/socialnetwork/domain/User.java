package com.socialnetwork.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class User {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private boolean emailVerified;

    private String password;

    private String passwordHash;

    private Photo profilePicture;

    void verifyEmail() {
        this.emailVerified = true;
    }

    void updateName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    void updatePassword(String password) {
        this.password = password;
    }
}
