package com.ems.crud.leave.dto;

import com.ems.crud.common.LeaveStatus;
import com.ems.crud.common.LeaveType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LeaveResponse(
		Long id,
		Long employeeId,
		String employeeName,
		LeaveType leaveType,
		LocalDate startDate,
		LocalDate endDate,
		String reason,
		LeaveStatus status,
		String reviewComment,
		LocalDateTime reviewedAt,
		LocalDateTime createdAt
) {
}
