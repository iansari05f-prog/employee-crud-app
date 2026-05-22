package com.ems.crud.department;

import com.ems.crud.department.api.DepartmentApi;
import com.ems.crud.department.dto.DepartmentRequest;
import com.ems.crud.department.dto.DepartmentResponse;
import com.ems.crud.employee.dto.EmployeeResponse;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class DepartmentController implements DepartmentApi {

	private final DepartmentService departmentService;

	public DepartmentController(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	@Override
	public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
		return ResponseEntity.ok(departmentService.getAllDepartments());
	}

	@Override
	public ResponseEntity<DepartmentResponse> getDepartmentById(Long id) {
		return ResponseEntity.ok(departmentService.getDepartmentById(id));
	}

	@Override
	public ResponseEntity<DepartmentResponse> createDepartment(DepartmentRequest request) {
		DepartmentResponse response = departmentService.createDepartment(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.id())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}

	@Override
	public ResponseEntity<DepartmentResponse> updateDepartment(Long id, DepartmentRequest request) {
		return ResponseEntity.ok(departmentService.updateDepartment(id, request));
	}

	@Override
	public ResponseEntity<Void> deleteDepartment(Long id) {
		departmentService.deleteDepartment(id);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<List<EmployeeResponse>> getDepartmentEmployees(Long id) {
		return ResponseEntity.ok(departmentService.getEmployeesByDepartment(id));
	}

	@Override
	public ResponseEntity<EmployeeResponse> assignEmployee(Long departmentId, Long employeeId) {
		return ResponseEntity.ok(departmentService.assignEmployeeToDepartment(departmentId, employeeId));
	}
}
