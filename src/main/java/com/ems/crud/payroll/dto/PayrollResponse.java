package com.ems.crud.payroll.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PayrollResponse(
		Long id,
		Long employeeId,
		String employeeName,
		BigDecimal baseSalary,
		BigDecimal bonus,
		BigDecimal deductions,
		BigDecimal netSalary,
		Integer payPeriodMonth,
		Integer payPeriodYear,
		LocalDateTime generatedAt
) {
}
