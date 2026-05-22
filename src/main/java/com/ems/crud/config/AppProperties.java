package com.ems.crud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, Upload upload, PasswordReset passwordReset) {

	public record Jwt(String secret, long expirationMs) {
	}

	public record Upload(String directory) {
	}

	public record PasswordReset(long expirationMinutes, boolean exposeTokenInResponse) {
	}
}
