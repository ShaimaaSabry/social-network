package com.socialnetwork.config.email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationEmail {
	private String subject;
	private String frontendUrl;
}
