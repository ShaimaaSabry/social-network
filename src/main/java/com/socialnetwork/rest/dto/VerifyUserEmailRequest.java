package com.socialnetwork.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@ToString
public class VerifyUserEmailRequest {
    @NotBlank(message = "Token is required.")
    private String token;
}
