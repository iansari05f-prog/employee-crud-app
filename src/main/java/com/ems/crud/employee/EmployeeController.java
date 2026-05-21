package com.ems.crud.employee;

import com.ems.crud.employee.dto.EmployeeRequest;
import com.ems.crud.employee.dto.EmployeeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employees", description = "Employee CRUD, search, and profile APIs")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
		return ResponseEntity.ok(employeeService.getAllEmployees());
	}

	@GetMapping("/search")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	@Operation(summary = "Search employees by filters")
	public ResponseEntity<List<EmployeeResponse>> searchEmployees(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) Long departmentId,
			@RequestParam(required = false) BigDecimal minSalary,
			@RequestParam(required = false) BigDecimal maxSalary,
			@RequestParam(required = false) Integer minExperience
	) {
		return ResponseEntity.ok(employeeService.searchEmployees(
				name, departmentId, minSalary, maxSalary, minExperience));
	}

	@GetMapping("/me")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public ResponseEntity<EmployeeResponse> getMyProfile() {
		return ResponseEntity.ok(employeeService.getMyProfile());
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
		return ResponseEntity.ok(employeeService.getEmployeeById(id));
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
		EmployeeResponse employee = employeeService.createEmployee(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(employee.id())
				.toUri();
		return ResponseEntity.created(location).body(employee);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	public ResponseEntity<EmployeeResponse> updateEmployee(
			@PathVariable Long id,
			@Valid @RequestBody EmployeeRequest request
	) {
		return ResponseEntity.ok(employeeService.updateEmployee(id, request));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
		employeeService.deleteEmployee(id);
		return ResponseEntity.noContent().build();
	}
}
