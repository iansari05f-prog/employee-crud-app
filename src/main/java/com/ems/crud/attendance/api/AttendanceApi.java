package com.ems.crud.attendance.api;

import com.ems.crud.attendance.dto.AttendanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Attendance", description = "Daily check-in, check-out, and attendance history APIs")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/attendance")
public interface AttendanceApi {

	@Operation(summary = "Check in", description = "Records today's check-in time for the logged-in user's employee profile.")
	@PostMapping("/check-in")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	ResponseEntity<AttendanceResponse> checkIn();

	@Operation(summary = "Check out", description = "Records today's check-out time for the logged-in user's employee profile.")
	@PostMapping("/check-out")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	ResponseEntity<AttendanceResponse> checkOut();

	@Operation(summary = "My attendance history", description = "Returns all attendance records for the logged-in employee.")
	@GetMapping("/me")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	ResponseEntity<List<AttendanceResponse>> getMyAttendance();

	@Operation(summary = "Employee attendance history", description = "Returns attendance records for a specific employee. Admin or HR only.")
	@GetMapping("/employee/{employeeId}")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	ResponseEntity<List<AttendanceResponse>> getEmployeeAttendance(
			@Parameter(description = "Employee ID") @PathVariable Long employeeId
	);
}
