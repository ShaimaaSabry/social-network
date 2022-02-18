package com.socialnetwork.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Photo {
    private Long id;

    private User user;

    private String path;

    private boolean validSelfie;
}
