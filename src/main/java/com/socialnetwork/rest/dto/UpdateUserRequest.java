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
public class UpdateUserRequest {
    @NotBlank(message = "First name is required.")
    @Size(max = 20)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 20)
    private String lastName;
}
