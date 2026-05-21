package com.ems.crud.employee.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EmployeeResponse(
		Long id,
		String firstName,
		String lastName,
		String email,
		String phoneNumber,
		String jobTitle,
		Long departmentId,
		String departmentName,
		BigDecimal salary,
		Integer experienceYears,
		String photoPath,
		String resumePath,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}
