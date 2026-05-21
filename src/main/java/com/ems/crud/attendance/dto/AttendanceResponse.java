package com.ems.crud.attendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceResponse(
		Long id,
		Long employeeId,
		String employeeName,
		LocalDate attendanceDate,
		LocalDateTime checkInTime,
		LocalDateTime checkOutTime,
		Double workingHours
) {
}
