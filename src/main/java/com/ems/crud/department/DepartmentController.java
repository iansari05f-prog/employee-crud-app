package com.ems.crud.department;

import com.ems.crud.department.dto.DepartmentRequest;
import com.ems.crud.department.dto.DepartmentResponse;
import com.ems.crud.employee.dto.EmployeeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/departments")
@Tag(name = "Departments", description = "Department management APIs")
@SecurityRequirement(name = "bearerAuth")
public class DepartmentController {

	private final DepartmentService departmentService;

	public DepartmentController(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
		return ResponseEntity.ok(departmentService.getAllDepartments());
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
		return ResponseEntity.ok(departmentService.getDepartmentById(id));
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	@Operation(summary = "Create department")
	public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request) {
		DepartmentResponse response = departmentService.createDepartment(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.id())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	public ResponseEntity<DepartmentResponse> updateDepartment(
			@PathVariable Long id,
			@Valid @RequestBody DepartmentRequest request
	) {
		return ResponseEntity.ok(departmentService.updateDepartment(id, request));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
		departmentService.deleteDepartment(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/employees")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	public ResponseEntity<List<EmployeeResponse>> getDepartmentEmployees(@PathVariable Long id) {
		return ResponseEntity.ok(departmentService.getEmployeesByDepartment(id));
	}

	@PostMapping("/{departmentId}/employees/{employeeId}")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	public ResponseEntity<EmployeeResponse> assignEmployee(
			@PathVariable Long departmentId,
			@PathVariable Long employeeId
	) {
		return ResponseEntity.ok(departmentService.assignEmployeeToDepartment(departmentId, employeeId));
	}
}
