package com.socialnetwork.rest.dto;

import com.socialnetwork.rest.validation.ConfirmPasswordConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@ToString
@ConfirmPasswordConstraint(passwordField = "password", confirmPasswordField = "confirmPassword", message = "Password and confirm password don't match.")
public class CreateUserRequest {
    @NotBlank(message = "First name is required.")
    @Size(max = 20)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 20)
    private String lastName;

    @NotBlank(message = "Email is required.")
    @Size(min = 5, max = 50)
    @Email
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 5, max = 20)
    private String password;

    @NotBlank(message = "Confirm password is required.")
    @Size(min = 5, max = 20)
    private String confirmPassword;
}
