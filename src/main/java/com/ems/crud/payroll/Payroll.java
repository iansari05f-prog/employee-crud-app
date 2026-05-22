package com.ems.crud.payroll;

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
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
		name = "payrolls",
		uniqueConstraints = @UniqueConstraint(
				name = "uk_payroll_employee_period",
				columnNames = {"employee_id", "pay_period_month", "pay_period_year"}
		)
)
@Getter
@Setter
@NoArgsConstructor
public class Payroll {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal baseSalary;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal bonus = BigDecimal.ZERO;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal deductions = BigDecimal.ZERO;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal netSalary;

	@Column(nullable = false)
	private Integer payPeriodMonth;

	@Column(nullable = false)
	private Integer payPeriodYear;

	@Column(nullable = false, updatable = false)
	private LocalDateTime generatedAt;

	public Payroll(
			Employee employee,
			BigDecimal baseSalary,
			BigDecimal bonus,
			BigDecimal deductions,
			BigDecimal netSalary,
			Integer payPeriodMonth,
			Integer payPeriodYear
	) {
		this.employee = employee;
		this.baseSalary = baseSalary;
		this.bonus = bonus;
		this.deductions = deductions;
		this.netSalary = netSalary;
		this.payPeriodMonth = payPeriodMonth;
		this.payPeriodYear = payPeriodYear;
	}

	@PrePersist
	void onCreate() {
		generatedAt = LocalDateTime.now();
	}
}
