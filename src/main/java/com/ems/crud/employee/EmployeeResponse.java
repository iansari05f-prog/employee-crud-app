package com.ems.crud.employee;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EmployeeResponse(
		Long id,
		String firstName,
		String lastName,
		String email,
		String phoneNumber,
		String jobTitle,
		String department,
		BigDecimal salary,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}
