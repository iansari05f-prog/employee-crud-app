package com.ems.crud.auth.dto;

import com.ems.crud.common.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank @Email @Size(max = 150) String email,
		@NotBlank @Size(min = 6, max = 100) String password,
		@NotNull Role role,
		Long employeeId
) {
}
