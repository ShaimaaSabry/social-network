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
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token is required.")
    private String refreshToken;
}
