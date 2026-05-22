package com.ems.crud.employee;

import com.ems.crud.employee.api.EmployeeApi;
import com.ems.crud.employee.dto.CreateEmployeeRequest;
import com.ems.crud.employee.dto.EmployeeRequest;
import com.ems.crud.employee.dto.EmployeeResponse;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class EmployeeController implements EmployeeApi {

	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@Override
	public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
		return ResponseEntity.ok(employeeService.getAllEmployees());
	}

	@Override
	public ResponseEntity<List<EmployeeResponse>> searchEmployees(
			String name,
			Long departmentId,
			BigDecimal minSalary,
			BigDecimal maxSalary,
			Integer minExperience
	) {
		return ResponseEntity.ok(employeeService.searchEmployees(
				name, departmentId, minSalary, maxSalary, minExperience));
	}

	@Override
	public ResponseEntity<EmployeeResponse> getMyProfile() {
		return ResponseEntity.ok(employeeService.getMyProfile());
	}

	@Override
	public ResponseEntity<EmployeeResponse> getEmployeeById(Long id) {
		return ResponseEntity.ok(employeeService.getEmployeeById(id));
	}

	@Override
	public ResponseEntity<EmployeeResponse> createEmployee(CreateEmployeeRequest request) {
		EmployeeResponse employee = employeeService.createEmployee(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(employee.id())
				.toUri();
		return ResponseEntity.created(location).body(employee);
	}

	@Override
	public ResponseEntity<EmployeeResponse> updateEmployee(Long id, EmployeeRequest request) {
		return ResponseEntity.ok(employeeService.updateEmployee(id, request));
	}

	@Override
	public ResponseEntity<Void> deleteEmployee(Long id) {
		employeeService.deleteEmployee(id);
		return ResponseEntity.noContent().build();
	}
}
