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
public class UpdateUserRequest {
	@NotBlank(message = "First name is required.")
	@Size(max = 20)
	private String firstName;

	@NotBlank(message = "Last name is required.")
	@Size(max = 20)
	private String lastName;
}
