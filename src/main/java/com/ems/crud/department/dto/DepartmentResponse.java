package com.ems.crud.department.dto;

import java.time.LocalDateTime;

public record DepartmentResponse(
		Long id,
		String name,
		String description,
		long employeeCount,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}
