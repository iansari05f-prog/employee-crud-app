package com.ems.crud.attendance;

import com.ems.crud.attendance.dto.AttendanceResponse;
import com.ems.crud.auth.User;
import com.ems.crud.common.Role;
import com.ems.crud.employee.Employee;
import com.ems.crud.employee.EmployeeRepository;
import com.ems.crud.exception.BadRequestException;
import com.ems.crud.exception.ForbiddenException;
import com.ems.crud.exception.ResourceNotFoundException;
import com.ems.crud.security.SecurityUtils;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttendanceService {

	private final AttendanceRepository attendanceRepository;
	private final EmployeeRepository employeeRepository;
	private final SecurityUtils securityUtils;

	public AttendanceService(
			AttendanceRepository attendanceRepository,
			EmployeeRepository employeeRepository,
			SecurityUtils securityUtils
	) {
		this.attendanceRepository = attendanceRepository;
		this.employeeRepository = employeeRepository;
		this.securityUtils = securityUtils;
	}

	@Transactional
	public AttendanceResponse checkIn() {
		Employee employee = getCurrentEmployee();
		LocalDate today = LocalDate.now();

		attendanceRepository.findByEmployeeIdAndAttendanceDateAndCheckOutTimeIsNull(employee.getId(), today)
				.ifPresent(a -> {
					throw new BadRequestException("Already checked in for today");
				});

		Attendance attendance = new Attendance(employee, today, LocalDateTime.now());
		return toResponse(attendanceRepository.save(attendance));
	}

	@Transactional
	public AttendanceResponse checkOut() {
		Employee employee = getCurrentEmployee();
		Attendance attendance = attendanceRepository
				.findTopByEmployeeIdAndCheckOutTimeIsNullOrderByCheckInTimeDesc(employee.getId())
				.orElseThrow(() -> new BadRequestException("No active check-in found"));

		LocalDateTime checkOutTime = LocalDateTime.now();
		attendance.setCheckOutTime(checkOutTime);
		attendance.setWorkingHours(calculateWorkingHours(attendance.getCheckInTime(), checkOutTime));

		return toResponse(attendance);
	}

	@Transactional(readOnly = true)
	public List<AttendanceResponse> getMyAttendance() {
		Employee employee = getCurrentEmployee();
		return attendanceRepository.findByEmployeeIdOrderByAttendanceDateDesc(employee.getId())
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<AttendanceResponse> getEmployeeAttendance(Long employeeId) {
		assertAdminOrHr();
		if (!employeeRepository.existsById(employeeId)) {
			throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
		}
		return attendanceRepository.findByEmployeeIdOrderByAttendanceDateDesc(employeeId)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	private Employee getCurrentEmployee() {
		User user = securityUtils.getCurrentUser();
		if (user.getEmployee() == null) {
			throw new BadRequestException("Employee profile is not linked to this user");
		}
		return user.getEmployee();
	}

	private void assertAdminOrHr() {
		User user = securityUtils.getCurrentUser();
		if (user.getRole() != Role.ADMIN && user.getRole() != Role.HR) {
			throw new ForbiddenException("Only ADMIN or HR can view other employee attendance");
		}
	}

	private double calculateWorkingHours(LocalDateTime checkIn, LocalDateTime checkOut) {
		long minutes = Duration.between(checkIn, checkOut).toMinutes();
		return Math.round((minutes / 60.0) * 100.0) / 100.0;
	}

	private AttendanceResponse toResponse(Attendance attendance) {
		Employee employee = attendance.getEmployee();
		return new AttendanceResponse(
				attendance.getId(),
				employee.getId(),
				employee.getFirstName() + " " + employee.getLastName(),
				attendance.getAttendanceDate(),
				attendance.getCheckInTime(),
				attendance.getCheckOutTime(),
				attendance.getWorkingHours()
		);
	}
}
