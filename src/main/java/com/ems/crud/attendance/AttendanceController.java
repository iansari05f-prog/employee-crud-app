package com.ems.crud.attendance;

import com.ems.crud.attendance.api.AttendanceApi;
import com.ems.crud.attendance.dto.AttendanceResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AttendanceController implements AttendanceApi {

	private final AttendanceService attendanceService;

	public AttendanceController(AttendanceService attendanceService) {
		this.attendanceService = attendanceService;
	}

	@Override
	public ResponseEntity<AttendanceResponse> checkIn() {
		return ResponseEntity.ok(attendanceService.checkIn());
	}

	@Override
	public ResponseEntity<AttendanceResponse> checkOut() {
		return ResponseEntity.ok(attendanceService.checkOut());
	}

	@Override
	public ResponseEntity<List<AttendanceResponse>> getMyAttendance() {
		return ResponseEntity.ok(attendanceService.getMyAttendance());
	}

	@Override
	public ResponseEntity<List<AttendanceResponse>> getEmployeeAttendance(Long employeeId) {
		return ResponseEntity.ok(attendanceService.getEmployeeAttendance(employeeId));
	}
}
