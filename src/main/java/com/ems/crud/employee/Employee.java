package com.ems.crud.employee;

import com.ems.crud.department.Department;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
		name = "employees",
		uniqueConstraints = @UniqueConstraint(name = "uk_employees_email", columnNames = "email")
)
@Getter
@Setter
@NoArgsConstructor
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String firstName;

	@Column(nullable = false, length = 100)
	private String lastName;

	@Column(nullable = false, unique = true, length = 150)
	private String email;

	@Column(length = 20)
	private String phoneNumber;

	@Column(nullable = false, length = 100)
	private String jobTitle;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "department_id", nullable = false)
	private Department department;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal salary;

	@Column(nullable = false)
	private Integer experienceYears = 0;

	@Column(length = 500)
	private String photoPath;

	@Column(length = 500)
	private String resumePath;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public Employee(
			String firstName,
			String lastName,
			String email,
			String phoneNumber,
			String jobTitle,
			Department department,
			BigDecimal salary,
			Integer experienceYears
	) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.jobTitle = jobTitle;
		this.department = department;
		this.salary = salary;
		this.experienceYears = experienceYears;
	}

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
