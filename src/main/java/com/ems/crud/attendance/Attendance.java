package com.ems.crud.attendance;

import com.ems.crud.employee.Employee;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@NoArgsConstructor
public class Attendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;

	@Column(nullable = false)
	private LocalDate attendanceDate;

	@Column(nullable = false)
	private LocalDateTime checkInTime;

	private LocalDateTime checkOutTime;

	private Double workingHours;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public Attendance(Employee employee, LocalDate attendanceDate, LocalDateTime checkInTime) {
		this.employee = employee;
		this.attendanceDate = attendanceDate;
		this.checkInTime = checkInTime;
	}

	@PrePersist
	void onCreate() {
		createdAt = LocalDateTime.now();
	}
}
