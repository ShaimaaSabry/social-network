package com.socialnetwork.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean emailVerified;
    private String profilePictureUrl;
    private boolean profilePictureValidSelfie;
}
