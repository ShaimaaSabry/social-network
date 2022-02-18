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
public class CreatePostRequest {
    @NotBlank(message = "Content is required.")
    @Size(max = 500)
    private String content;
}
