package com.ems.crud.payroll.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PayrollGenerateRequest(
		@NotNull Long employeeId,
		@NotNull @Min(1) @Max(12) Integer payPeriodMonth,
		@NotNull @Min(2000) Integer payPeriodYear,
		@NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal bonus,
		@NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal deductions
) {
}
