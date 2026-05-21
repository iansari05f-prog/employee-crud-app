package com.ems.crud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, Upload upload) {

	public record Jwt(String secret, long expirationMs) {
	}

	public record Upload(String directory) {
	}
}
