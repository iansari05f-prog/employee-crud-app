package com.ems.crud.attendance;

import com.ems.crud.attendance.dto.AttendanceResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attendance")
@Tag(name = "Attendance", description = "Check-in, check-out, and attendance history")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {

	private final AttendanceService attendanceService;

	public AttendanceController(AttendanceService attendanceService) {
		this.attendanceService = attendanceService;
	}

	@PostMapping("/check-in")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	public ResponseEntity<AttendanceResponse> checkIn() {
		return ResponseEntity.ok(attendanceService.checkIn());
	}

	@PostMapping("/check-out")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	public ResponseEntity<AttendanceResponse> checkOut() {
		return ResponseEntity.ok(attendanceService.checkOut());
	}

	@GetMapping("/me")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	public ResponseEntity<List<AttendanceResponse>> getMyAttendance() {
		return ResponseEntity.ok(attendanceService.getMyAttendance());
	}

	@GetMapping("/employee/{employeeId}")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	public ResponseEntity<List<AttendanceResponse>> getEmployeeAttendance(@PathVariable Long employeeId) {
		return ResponseEntity.ok(attendanceService.getEmployeeAttendance(employeeId));
	}
}
