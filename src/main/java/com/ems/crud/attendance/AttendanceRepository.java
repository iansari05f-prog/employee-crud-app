package com.ems.crud.attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

	Optional<Attendance> findByEmployeeIdAndAttendanceDateAndCheckOutTimeIsNull(Long employeeId, LocalDate date);

	List<Attendance> findByEmployeeIdOrderByAttendanceDateDesc(Long employeeId);

	Optional<Attendance> findTopByEmployeeIdAndCheckOutTimeIsNullOrderByCheckInTimeDesc(Long employeeId);
}
