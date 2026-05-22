package com.ems.crud.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
		String currentPassword,
		@NotBlank @Size(min = 6, max = 100) String newPassword
) {
}
