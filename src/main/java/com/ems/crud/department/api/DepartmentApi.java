package com.ems.crud.department.api;

import com.ems.crud.department.dto.DepartmentRequest;
import com.ems.crud.department.dto.DepartmentResponse;
import com.ems.crud.employee.dto.EmployeeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@Tag(name = "Departments", description = "Department CRUD and employee assignment APIs")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/departments")
public interface DepartmentApi {

	@Operation(summary = "List all departments", description = "Returns every department. All authenticated roles.")
	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	ResponseEntity<List<DepartmentResponse>> getAllDepartments();

	@Operation(summary = "Get department by ID", description = "Returns one department by primary key.")
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	ResponseEntity<DepartmentResponse> getDepartmentById(
			@Parameter(description = "Department ID") @PathVariable Long id
	);

	@Operation(summary = "Create department", description = "Adds a new department. Admin or HR only.")
	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request);

	@Operation(summary = "Update department", description = "Updates name and description by ID. Admin or HR only.")
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	ResponseEntity<DepartmentResponse> updateDepartment(
			@Parameter(description = "Department ID") @PathVariable Long id,
			@Valid @RequestBody DepartmentRequest request
	);

	@Operation(summary = "Delete department", description = "Removes a department by ID. Admin only.")
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	ResponseEntity<Void> deleteDepartment(
			@Parameter(description = "Department ID") @PathVariable Long id
	);

	@Operation(summary = "List employees in department", description = "Returns all employees assigned to the given department. Admin or HR only.")
	@GetMapping("/{id}/employees")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	ResponseEntity<List<EmployeeResponse>> getDepartmentEmployees(
			@Parameter(description = "Department ID") @PathVariable Long id
	);

	@Operation(summary = "Assign employee to department", description = "Links an employee to a department. Admin or HR only.")
	@PostMapping("/{departmentId}/employees/{employeeId}")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	ResponseEntity<EmployeeResponse> assignEmployee(
			@Parameter(description = "Department ID") @PathVariable Long departmentId,
			@Parameter(description = "Employee ID") @PathVariable Long employeeId
	);
}
