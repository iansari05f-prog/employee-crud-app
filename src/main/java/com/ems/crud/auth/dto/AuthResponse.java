package com.ems.crud.auth.dto;

import com.ems.crud.common.Role;

public record AuthResponse(
		String token,
		String tokenType,
		Long userId,
		String email,
		Role role,
		Long employeeId
) {
}
