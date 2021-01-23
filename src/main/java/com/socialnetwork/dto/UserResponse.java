package com.socialnetwork.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private boolean emailVerified;
	private String profilePictureUrl;
	private boolean profilePictureValidSelfie;
}
