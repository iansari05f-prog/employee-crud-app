package com.ems.crud.employee;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record EmployeeRequest(
		@NotBlank(message = "First name is required")
		@Size(max = 100, message = "First name must not exceed 100 characters")
		String firstName,

		@NotBlank(message = "Last name is required")
		@Size(max = 100, message = "Last name must not exceed 100 characters")
		String lastName,

		@NotBlank(message = "Email is required")
		@Email(message = "Email must be valid")
		@Size(max = 150, message = "Email must not exceed 150 characters")
		String email,

		@Size(max = 20, message = "Phone number must not exceed 20 characters")
		String phoneNumber,

		@NotBlank(message = "Job title is required")
		@Size(max = 100, message = "Job title must not exceed 100 characters")
		String jobTitle,

		@NotBlank(message = "Department is required")
		@Size(max = 100, message = "Department must not exceed 100 characters")
		String department,

		@NotNull(message = "Salary is required")
		@DecimalMin(value = "0.00", message = "Salary must be zero or greater")
		@Digits(integer = 10, fraction = 2, message = "Salary must contain up to 10 digits and 2 decimals")
		BigDecimal salary
) {
}
