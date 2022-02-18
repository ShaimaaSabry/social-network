package com.socialnetwork.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@ToString
public class AuthRequest {
    @NotBlank(message = "Email is required.")
    @Size(min = 5, max = 50)
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 5, max = 20)
    private String password;
}
