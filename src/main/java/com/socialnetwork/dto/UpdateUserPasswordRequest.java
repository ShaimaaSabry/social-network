package com.socialnetwork.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateUserPasswordRequest {
	@NotBlank(message = "Current password is required.")
	@Size(min = 5, max = 20)
	private String currentPassword;
	
	@NotBlank(message = "New password is required.")
	@Size(min = 5, max = 20)
	private String newPassword;
	
	@NotBlank(message = "Confirm new password is required.")
	@Size(min = 5, max = 20)
	private String confirmNewPassword;
}
