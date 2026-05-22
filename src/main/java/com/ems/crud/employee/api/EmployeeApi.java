package com.ems.crud.employee.api;

import com.ems.crud.employee.dto.CreateEmployeeRequest;
import com.ems.crud.employee.dto.EmployeeRequest;
import com.ems.crud.employee.dto.EmployeeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Employees", description = "Employee CRUD, search, and profile APIs")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/employees")
public interface EmployeeApi {

	@Operation(summary = "List all employees", description = "Returns every employee. Admin or HR only.")
	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	ResponseEntity<List<EmployeeResponse>> getAllEmployees();

	@Operation(summary = "Search employees", description = "Filter by name, department, salary range, or minimum experience. Admin or HR only.")
	@GetMapping("/search")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	ResponseEntity<List<EmployeeResponse>> searchEmployees(
			@Parameter(description = "Partial match on employee name") @RequestParam(required = false) String name,
			@Parameter(description = "Department ID") @RequestParam(required = false) Long departmentId,
			@Parameter(description = "Minimum salary") @RequestParam(required = false) BigDecimal minSalary,
			@Parameter(description = "Maximum salary") @RequestParam(required = false) BigDecimal maxSalary,
			@Parameter(description = "Minimum years of experience") @RequestParam(required = false) Integer minExperience
	);

	@Operation(summary = "Get my profile", description = "Returns the employee record linked to the logged-in user. Employee role only.")
	@GetMapping("/me")
	@PreAuthorize("hasRole('EMPLOYEE')")
	ResponseEntity<EmployeeResponse> getMyProfile();

	@Operation(summary = "Get employee by ID", description = "Returns one employee by primary key. Admin, HR, or Employee.")
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	ResponseEntity<EmployeeResponse> getEmployeeById(
			@Parameter(description = "Employee ID") @PathVariable Long id
	);

	@Operation(
			summary = "Create employee",
			description = "Adds a new employee with a linked EMPLOYEE login account (same email, initial password). Admin or HR only."
	)
	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request);

	@Operation(summary = "Update employee", description = "Updates an existing employee by ID. Admin or HR only.")
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	ResponseEntity<EmployeeResponse> updateEmployee(
			@Parameter(description = "Employee ID") @PathVariable Long id,
			@Valid @RequestBody EmployeeRequest request
	);

	@Operation(summary = "Delete employee", description = "Permanently removes an employee by ID. Admin or HR only.")
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	ResponseEntity<Void> deleteEmployee(
			@Parameter(description = "Employee ID") @PathVariable Long id
	);
}
