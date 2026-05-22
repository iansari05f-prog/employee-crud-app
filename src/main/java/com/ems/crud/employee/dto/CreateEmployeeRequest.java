package com.ems.crud.employee.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateEmployeeRequest(
		@NotBlank @Size(max = 100) String firstName,
		@NotBlank @Size(max = 100) String lastName,
		@NotBlank @Email @Size(max = 150) String email,
		@Size(max = 20) String phoneNumber,
		@NotBlank @Size(max = 100) String jobTitle,
		@NotNull Long departmentId,
		@NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal salary,
		@NotNull @Min(0) Integer experienceYears,
		@NotBlank @Size(min = 6, max = 100) String password
) {
}
